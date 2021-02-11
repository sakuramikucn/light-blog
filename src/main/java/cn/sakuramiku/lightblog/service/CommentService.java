package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Comment;

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
     * @param comment
     * @return
     */
    Comment saveComment(Comment comment);

    /**
     * 删除评论
     *
     * @param id
     * @return
     */
    boolean removeComment(String id);

//    /**
//     * 搜索评论
//     *
//     * @param condition 条件
//     * @return
//     */
//    List<Comment> searchComment(CommentExample condition);

    /**
     * 获取子评论
     *
     * @param perentId
     * @return
     */
    List<Comment> getChildComments(String perentId);

    /**
     * 扭转状态
     *
     * @param state 状态
     * @return
     */
    Comment opsStatus(int state);
}
