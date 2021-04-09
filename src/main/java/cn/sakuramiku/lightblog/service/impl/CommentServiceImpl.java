package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.annotation.OnChange;
import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.IdGenerator;
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
@LogConfig(reference = "comment",name = "评论")
@CacheConfig(cacheNames = "light_blog:comment", keyGenerator = "simpleKeyGenerator")
@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @WriteLog(action = WriteLog.Action.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveComment(@NonNull Comment comment) {
        long id = IdGenerator.nextId();
        comment.setId(id);
        comment.setCreateTime(LocalDateTime.now());
        comment.setState(Constant.COMMENT_STATE_NORMAL);
        commentMapper.insert(comment);
        return id;
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean removeComment(@NonNull Long id) {
        return commentMapper.update(id, Constant.COMMENT_STATE_DELETE);
    }

    @WriteLog(action = WriteLog.Action.DELETE)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteComment(@NonNull Long id) {
        return commentMapper.delete(id);
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean hiddenComment(@NonNull Long id, @NonNull Boolean isHidden) {
        if (isHidden) {
            return commentMapper.update(id, Constant.COMMENT_STATE_HIDDEN);
        }
        return commentMapper.update(id, Constant.COMMENT_STATE_NORMAL);
    }

    @OnChange
    @Cacheable(unless = "null == #result || 0 == #result.total")
    @Override
    public PageInfo<Comment> searchComment(Integer state, String ref, Long parentId, Integer page, Integer pageSize) {
        if (null != page && null != pageSize) {
            PageHelper.startPage(page, pageSize, true);
        }
        List<Comment> comments = commentMapper.search(state, ref, parentId);
        return PageInfo.of(comments);
    }

}
