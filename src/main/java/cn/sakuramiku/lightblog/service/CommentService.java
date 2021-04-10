package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Comment;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 评论服务接口
 *
 * @author lyy
 */
public interface CommentService {

    /**
     * 添加评论
     *
     * @param comment 评论
     * @return 评论ID
     */
    Comment saveComment(@NonNull Comment comment);

    Comment getComment(Long id);

    /**
     * 删除评论（逻辑删除）
     *
     * @param id 评论ID
     * @return 是否成功
     */
    Comment removeComment(@NonNull Long id);

    /**
     * 删除评论（真实删除）
     *
     * @param id 评论ID
     * @return 是否成功
     */
    Boolean deleteComment(@NonNull Long id);

    /**
     * 屏蔽评论
     *
     * @param id       评论ID
     * @param isHidden true=屏蔽，false=恢复
     * @return 是否成功
     */
    Comment hiddenComment(@NonNull Long id, @NonNull Boolean isHidden);

    /**
     * 搜索评论
     *
     * @param state    状态{@link cn.sakuramiku.lightblog.util.Constant#COMMENT_STATE_NORMAL}
     * @param ref      引用标识
     * @param parentId 父评论ID
     * @param page     第几页
     * @param pageSize 每页项数 
     * @return 评论列表
     */
    PageInfo<Comment> searchComment(@Nullable Integer state, @Nullable String ref, @Nullable Long parentId, @Nullable Integer page, @Nullable Integer pageSize);

}
