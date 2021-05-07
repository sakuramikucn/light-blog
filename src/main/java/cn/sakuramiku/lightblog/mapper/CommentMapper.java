package cn.sakuramiku.lightblog.mapper;

import cn.sakuramiku.lightblog.entity.Comment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;

/**
 * 评论SQL映射
 *
 * @author LYY
 */
public interface CommentMapper {

    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 是否成功
     */
    Boolean delete(@NonNull Long id);

    /**
     * 添加评论
     *
     * @param record 评论
     * @return 是否成功
     */
    Boolean insert(@NonNull Comment record);

    /**
     * 修改评论状态
     *
     * @param id    评论ID
     * @param state 评论状态 {@link cn.sakuramiku.lightblog.util.Constant#COMMENT_STATE_NORMAL}
     * @return 是否成功
     */
    Boolean update(@NonNull @Param("id") Long id, @NonNull @Param("state") Integer state);

    /**
     * 获取评论
     *
     * @param id 评论ID
     * @return 评论
     */
    Comment get(@NonNull Long id);

    /**
     * 搜索评论
     *
     * @param state    状态{@link cn.sakuramiku.lightblog.util.Constant#COMMENT_STATE_NORMAL}
     * @param ref      引用，一般是文章ID
     * @param keyword
     * @param type
     * @param parentId 父评论ID
     * @return 评论列表
     */
    List<Comment> search(@Param("state") Integer state,
                         @Nullable @Param("ref") String ref,
                         @Param("keyword") String keyword,
                         @Param("type") Integer type,
                         @Nullable @Param("parentId") Long parentId);

    @Delete("delete from comment where state = #{state} and modified_time <= #{start}")
    Long deleteForRecycle(@Param("state") Integer status, @Param("start") Date start);
}