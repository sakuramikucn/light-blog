package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Comment;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

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
    Long saveComment(@NonNull Comment comment);

    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 是否成功
     */
    Boolean removeComment(@NonNull Long id);

    /**
     * 搜索评论
     *
     * @param ref      引用标识
     * @param parentId 父评论ID
     * @param page     第几页
     * @param pageSize 每页项数
     * @return 评论列表
     */
    PageInfo<Comment> searchComment(@Nullable String ref, @Nullable Long parentId, @Nullable Integer page, @Nullable Integer pageSize);

    /**
     * 获取子评论
     *
     * @param perentId 父评论ID
     * @return 评论列表
     */
    List<Comment> getSubComments(@NonNull Long perentId);

    /**
     * 扭转状态
     *
     * @param state 状态 {@link cn.sakuramiku.lightblog.util.Constant#COMMENT_STATE_NORMAL}
     * @return 是否成功
     */
    Boolean opsStatus(Integer state);
}
