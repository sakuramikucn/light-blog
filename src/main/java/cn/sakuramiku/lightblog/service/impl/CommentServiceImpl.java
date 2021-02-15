package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.common.util.IdUtil;
import cn.sakuramiku.lightblog.entity.Comment;
import cn.sakuramiku.lightblog.mapper.CommentMapper;
import cn.sakuramiku.lightblog.service.CommentService;
import cn.sakuramiku.lightblog.util.Constant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论服务
 *
 * @author lyy
 */
@CacheConfig(cacheNames = "light_blog:comment", keyGenerator = "simpleKeyGenerator")
@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveComment(@NonNull Comment comment) {
        long id = IdUtil.nextId();
        comment.setId(id);
        comment.setCreateTime(LocalDateTime.now());
        commentMapper.insert(comment);
        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean removeComment(@NonNull Long id) {
        return commentMapper.update(id, Constant.COMMENT_STATE_DELETE);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean hiddenComment(@NonNull Long id) {
        return commentMapper.update(id, Constant.COMMENT_STATE_HIDDEN);
    }

    @Cacheable(unless = "null == #result || 0 == #result.total")
    @Override
    public PageInfo<Comment> searchComment(String ref, Long parentId, Integer page, Integer pageSize) {
        if (null != page && null != pageSize) {
            PageHelper.startPage(page, pageSize, true);
        }
        List<Comment> comments = commentMapper.search(ref, parentId);
        return PageInfo.of(comments);
    }

}
