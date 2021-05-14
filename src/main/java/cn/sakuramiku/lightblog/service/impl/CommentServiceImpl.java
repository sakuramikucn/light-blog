package cn.sakuramiku.lightblog.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import cn.sakuramiku.lightblog.annotation.*;
import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.IdGenerator;
import cn.sakuramiku.lightblog.entity.Article;
import cn.sakuramiku.lightblog.entity.Comment;
import cn.sakuramiku.lightblog.entity.User;
import cn.sakuramiku.lightblog.mapper.CommentMapper;
import cn.sakuramiku.lightblog.service.ArticleService;
import cn.sakuramiku.lightblog.service.CommentService;
import cn.sakuramiku.lightblog.service.CommonService;
import cn.sakuramiku.lightblog.service.UserService;
import cn.sakuramiku.lightblog.util.Constant;
import cn.sakuramiku.lightblog.vo.CommentWrapView;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 评论服务
 *
 * @author lyy
 */
@LogConfig(reference = "#result.id", category = "comment", name = "评论")
@RedisCacheConfig(cacheName = "light_blog:comment")
@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;
    @Resource
    private ArticleService articleService;
    @Resource
    private UserService userService;
    @Resource
    private CommonService commonService;

    @WriteLog(action = WriteLog.Action.INSERT)
    @RedisCachePut(key = "#result.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Comment saveComment(@NonNull Comment comment) {
        long id = IdGenerator.nextId();
        comment.setId(id);
        comment.setCreateTime(LocalDateTime.now());
        comment.setState(Constant.COMMENT_STATE_NORMAL);

        // 判断评论用户角色类型
        String username = comment.getUsername();
        Article article = null;
        if (!StringUtils.isEmpty(username)) {
            User user = userService.getUser(username);
            if (Constant.COMMENT_TYPE_ARTICLE.equals(comment.getType())) {
                article = articleService.getArticle(Long.parseLong(comment.getSubReference()));
                if (null != article) {
                    String authorId = article.getAuthorId();
                    if (null != user) {
                        if (user.getId().toString().equals(authorId)) {
                            comment.setRoleType(Constant.COMMENT_ROLE_TYPE_AUTHOR);
                        } else {
                            comment.setRoleType(Constant.COMMENT_ROLE_TYPE_MANAGER);
                        }
                    }

                }
            } else {
                if (null != user) {
                    comment.setRoleType(Constant.COMMENT_ROLE_TYPE_MANAGER);

                }
            }
        }

        Boolean insert = commentMapper.insert(comment);
        if (insert) {
            // 邮件通知，暂时只通知目标评论
            Long parentId = comment.getParentId();
            if (null != parentId && !parentId.equals(0L)) {
                Comment parentComment = getComment(parentId);
                String email = parentComment.getEmail();
                if (StrUtil.isNotBlank(email)) {
                    try {
                        String content = templateContent(parentComment);
                        commonService.sendEmail(email, "Light Blog - 评论/留言回复通知", content);
                    } catch (Exception e) {
                        StaticLog.error("发送评论/留言回复通知 失败", e);
                    }
                }
            }
            // 通知文章作者
            if (Constant.COMMENT_TYPE_ARTICLE.equals(comment.getType())) {
                if (null == article) {
                    article = articleService.getArticle(Long.parseLong(comment.getReference()));
                }
                if (article != null) {
                    String authorId = article.getAuthorId();
                    User user = userService.getUser(Long.parseLong(authorId));
                    if (null != user) {
                        String email = user.getEmail();
                        if (StrUtil.isNotBlank(email)) {
                            try {
                                String content = templateContent2(comment);
                                commonService.sendEmail(email, "Light Blog - 文章评论通知", content);
                            } catch (Exception e) {
                                StaticLog.error("文章评论通知 失败", e);
                            }
                        }
                    }
                }
            } else {
                // 发给我自己
                String email = "1519381662@qq.com";
                commonService.sendEmail(email, "Light Blog - 留言板留言通知", templateContent2(comment));
            }
            return comment;
        }
        return null;
    }


    protected String templateContent(Comment comment) {
        String baseUrl = System.getProperty("server.base.url", "https://blog.sakuramicn.cn");
        StringBuilder builder = new StringBuilder();
        // 文章评论
        if (Constant.COMMENT_TYPE_ARTICLE.equals(comment.getType())) {
            builder.append("您好，您在文章 ").append(baseUrl).append("/article/").append(comment.getReference());
        }
        // 留言板
        else if (Constant.COMMENT_TYPE_BOARD.equals(comment.getType())) {
            builder.append("您好，您在留言板 ").append(baseUrl).append("/message").append(comment.getReference());
        }
        builder.append(" 的留言：[ ").append(comment.getContent()).append(" ]").append("得到了回复，快去看看吧！");
        return builder.toString();
    }

    protected String templateContent2(Comment comment) {
        String baseUrl = System.getProperty("server.base.url", "https://blog.sakuramicn.cn");
        StringBuilder builder = new StringBuilder();
        // 文章评论
        if (Constant.COMMENT_TYPE_ARTICLE.equals(comment.getType())) {
            builder.append("您好，您的文章 ").append(baseUrl).append("article/").append(comment.getReference());
            builder.append(" 有新的评论，快去看看吧！");
        }
        // 留言板
        else if (Constant.COMMENT_TYPE_BOARD.equals(comment.getType())) {
            builder.append("留言板 ").append(baseUrl).append("/message");
            builder.append(" 有新的留言了，快去看看吧！");
        }

        return builder.toString();
    }


    @RedisCache(key = "#result.id")
    @Override
    public Comment getComment(Long id) {
        return commentMapper.get(id);
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @RedisCachePut(key = "#result.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Comment removeComment(@NonNull Long id) {
        Boolean update = commentMapper.update(id, Constant.COMMENT_STATE_DELETE);
        if (update) {
            Comment comment = commentMapper.get(id);
            List<Comment> commentList = commentMapper.search(null, comment.getReference(), null, null, comment.getId());
            for (Comment c : commentList) {
                commentMapper.update(c.getId(), Constant.COMMENT_STATE_DELETE);
            }
            return this.getComment(id);
        }
        return null;
    }

    @WriteLog(action = WriteLog.Action.DELETE)
    @RedisCacheDelete(key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteComment(@NonNull Long id) {
        return commentMapper.delete(id);
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @RedisCachePut(key = "#result.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Comment hiddenComment(@NonNull Long id, @NonNull Boolean isHidden) {
        if (isHidden) {
            Boolean update = commentMapper.update(id, Constant.COMMENT_STATE_HIDDEN);
            if (update) {
                return this.getComment(id);
            }
        }
        Boolean update = commentMapper.update(id, Constant.COMMENT_STATE_NORMAL);
        if (update) {
            return this.getComment(id);
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @RedisCleanQuery
    @WriteLog(action = WriteLog.Action.DELETE, result = true)
    @Override
    public Long deleteForRecycle(Integer status, Date start) {
        return commentMapper.deleteForRecycle(status, start);
    }

    @Transactional(rollbackFor = Exception.class)
    @WriteLog(action = WriteLog.Action.DELETE, result = true)
    @RedisCachePut(key = "id")
    @Override
    public Boolean restoreForRecycle(Long id) {
        return commentMapper.update(id, Constant.COMMENT_STATE_NORMAL);
    }

    @OnCacheChange
    @RedisCache
    @Override
    public PageInfo<Comment> searchComment(Integer state, String ref, Long parentId, String keyword, Integer type, Integer page, Integer pageSize) {
        if (null != page && null != pageSize) {
            PageHelper.startPage(page, pageSize, true);
        }
        List<Comment> comments = commentMapper.search(state, ref, keyword, type, parentId);
        return PageInfo.of(comments);
    }

    @OnCacheChange
    @RedisCache
    @Override
    public PageInfo<CommentWrapView> searchCommentWrap(Integer state, String ref, Long parentId, String keyword, Integer type, Integer page, Integer pageSize) {
        if (null != page && null != pageSize) {
            PageHelper.startPage(page, pageSize, true);
        }
        List<Comment> comments = commentMapper.search(state, ref, keyword, type, parentId);
        // id == 评论
        Map<Long, Comment> commentMap = comments.parallelStream().peek(c-> {
            Long parentId1 = c.getParentId();
            if (parentId1 != null && parentId1.equals(0L)){
                c.setParentId(null);
            }
        })
                .collect(Collectors.toMap(Comment::getId, Function.identity()));

        // 一级评论
        List<Comment> topList = comments.parallelStream().filter(c -> c.getParentId() == null || c.getParentId().equals(0L)).collect(Collectors.toList());


        // 剩余的评论
        List<Comment> otherList = new ArrayList<>(comments);

        List<Comment> copy = new ArrayList<>(comments);

        //一级评论ID ==> 二级评论，父ID直接为 一级评论ID
        Map<Long, List<Comment>> parentMap = comments.parallelStream()
                .filter(c -> c.getParentId() != null && !c.getParentId().equals(0L) && hasKey(topList,c.getParentId()))
                .peek(copy::remove)
                .collect(Collectors.groupingBy(Comment::getParentId));


        //一级评论ID ==> 三级评论，子引用为一级评论ID
        Map<Long, List<Comment>> subRefMap = copy.parallelStream()
                .filter(comment -> StrUtil.isNotBlank(comment.getSubReference()) && hasKey(topList,Long.parseLong(comment.getSubReference())))
                .collect(Collectors.groupingBy(comment -> Long.parseLong(comment.getSubReference())));

        // 合并
        Map<Long, List<Comment>> fullMap = Stream.of(parentMap, subRefMap).flatMap(map -> map.entrySet().parallelStream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> {
                    v1.addAll(v2);
                    return v1.parallelStream().distinct().collect(Collectors.toList());
                }));

        // 一级包装
        List<CommentWrapView> list = new ArrayList<>();
        for (Comment top : topList) {
            otherList.remove(top);

            Long key = top.getId();
            CommentWrapView wrapView = new CommentWrapView();
            Comment comment = commentMap.get(key);

            // 父评论
            Long parentId1 = comment.getParentId();
            if (null != parentId1 && !parentId1.equals(0L)) {
                Comment par = commentMap.get(parentId1);
                wrapView.setParent(par);
            }

            //当前评论
            wrapView.setComment(comment);

            // 子评论包装
            List<Comment> commentList = fullMap.get(key);
            if (!CollectionUtil.isEmpty(commentList)) {
                List<CommentWrapView> wrapViewList = commentList.parallelStream()
                        .sorted(Comparator.comparing(Comment::getCreateTime))
                        .map(c -> {
                            otherList.remove(c);

                            CommentWrapView commentWrapView = new CommentWrapView();
                            commentWrapView.setComment(c);
                            Long parentId2 = c.getParentId();
                            if (null != parentId2 && !parentId2.equals(0L)) {
                                Comment par = commentMap.get(parentId2);
                                commentWrapView.setParent(par);
                            }
                            return commentWrapView;
                        }).collect(Collectors.toList());
                wrapView.setChildList(wrapViewList);
            }

            list.add(wrapView);
        }

        if (!CollectionUtil.isEmpty(otherList)) {
            // 还有没包装的评论
            List<CommentWrapView> collect = otherList.parallelStream().map(c -> {

                        CommentWrapView commentWrapView = new CommentWrapView();
                        commentWrapView.setComment(c);
                        Long parentId2 = c.getParentId();
                        if (null != parentId2 && !parentId2.equals(0L)) {
                            Comment par = commentMap.get(parentId2);
                            commentWrapView.setParent(par);
                        }
                        return commentWrapView;
                    }
            ).collect(Collectors.toList());
            list.addAll(collect);
        }
        list.sort(Comparator.comparing(o -> o.getComment().getCreateTime()));
        PageInfo<Comment> of = PageInfo.of(comments);
        PageInfo<CommentWrapView> wrapList = new PageInfo<>();
        BeanUtils.copyProperties(of, wrapList);
        wrapList.setList(list);
        return wrapList;
    }

    private boolean hasKey(List<Comment> comments,Long key){
        Optional<Comment> first = comments.parallelStream().filter(comment -> comment.getId().equals(key)).findFirst();
        return first.isPresent();
    }
}
