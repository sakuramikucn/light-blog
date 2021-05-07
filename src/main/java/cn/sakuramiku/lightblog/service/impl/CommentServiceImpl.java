package cn.sakuramiku.lightblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
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
import cn.sakuramiku.lightblog.service.UserService;
import cn.sakuramiku.lightblog.util.Constant;
import cn.sakuramiku.lightblog.vo.CommentWrapView;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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

    @WriteLog(action = WriteLog.Action.INSERT)
    @RedisCachePut(key = "#result.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Comment saveComment(@NonNull Comment comment) {
        long id = IdGenerator.nextId();
        comment.setId(id);
        comment.setCreateTime(LocalDateTime.now());
        comment.setState(Constant.COMMENT_STATE_NORMAL);

        String username = comment.getUsername();
        if (!StringUtils.isEmpty(username)) {
            User user = userService.getUser(username);
            if (NumberUtil.isNumber(comment.getReference())) {
                Article article = articleService.getArticle(Long.parseLong(comment.getSubReference()));
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
            return comment;
        }
        return null;
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
            for (Comment c:commentList){
                commentMapper.update(c.getId(),Constant.COMMENT_STATE_DELETE);
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
        return commentMapper.update(id,Constant.COMMENT_STATE_NORMAL);
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
        Map<Long, Comment> commentMap = comments.parallelStream().collect(Collectors.toMap(Comment::getId, Function.identity()));

        // 顶级评论
        List<Comment> topList = comments.parallelStream().filter(c -> c.getParentId() == null || c.getParentId().equals(0L))
                .sorted(Comparator.comparing(Comment::getCreateTime)).collect(Collectors.toList());

        // 剩余的评论
        List<Comment> otherList = new ArrayList<>(comments);

        //顶级评论的子评论，id ==> 子评论
        Map<Long, List<Comment>> parentMap = comments.parallelStream()
                .filter(c -> c.getParentId() != null && !c.getParentId().equals(0L))
                .collect(Collectors.groupingBy(Comment::getParentId));

        //顶级评论的子评论，id ==> 子评论
        Map<Long, List<Comment>> subRefMap = comments.parallelStream()
                .filter(comment -> !StrUtil.isBlank(comment.getSubReference()))
                .collect(Collectors.groupingBy(comment -> Long.parseLong(comment.getSubReference())));
        // 合并
        Map<Long, List<Comment>> fullMap = Stream.of(parentMap, subRefMap).flatMap(map -> map.entrySet().parallelStream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> {
                    v1.addAll(v2);
                    return v1.parallelStream().distinct().sorted(Comparator.comparing(Comment::getCreateTime)).collect(Collectors.toList());
                }));

        // 包装
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
                List<CommentWrapView> wrapViewList = commentList.parallelStream().map(c -> {
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
        PageInfo<CommentWrapView> pageInfo = BeanUtil.copyProperties(comments, PageInfo.class);
        pageInfo.setList(list);
        return pageInfo;
    }


}
