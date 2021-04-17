package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.annotation.OnCacheChange;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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
    @CachePut(key = "#result.id",unless = "null == #result")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Comment saveComment(@NonNull Comment comment) {
        long id = IdGenerator.nextId();
        comment.setId(id);
        comment.setCreateTime(LocalDateTime.now());
        comment.setState(Constant.COMMENT_STATE_NORMAL);
        Boolean insert = commentMapper.insert(comment);
        if (insert){
            return comment;
        }
        return null;
    }


    @Cacheable(key = "#result.id",unless = "null == #result")
    @Override
    public Comment getComment(Long id) {
        return commentMapper.get(id);
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @CachePut(key = "#result.id",unless = "null == #result")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Comment removeComment(@NonNull Long id) {
        Boolean update = commentMapper.update(id, Constant.COMMENT_STATE_DELETE);
        if (update){
            return this.getComment(id);
        }
        return null;
    }

    @WriteLog(action = WriteLog.Action.DELETE)
    @CacheEvict(key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteComment(@NonNull Long id) {
        return commentMapper.delete(id);
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @CachePut(key = "#result.id",unless = "null == #result")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Comment hiddenComment(@NonNull Long id, @NonNull Boolean isHidden) {
        if (isHidden) {
            Boolean update = commentMapper.update(id, Constant.COMMENT_STATE_HIDDEN);
            if (update){
                return this.getComment(id);
            }
        }
        Boolean update = commentMapper.update(id, Constant.COMMENT_STATE_NORMAL);
        if (update){
            return this.getComment(id);
        }
        return null;
    }

    @OnCacheChange
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
