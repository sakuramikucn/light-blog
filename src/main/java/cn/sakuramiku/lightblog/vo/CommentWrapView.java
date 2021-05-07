package cn.sakuramiku.lightblog.vo;

import cn.sakuramiku.lightblog.entity.Comment;

import java.util.List;

/**
 * 评论视图
 * @author lyy
 */
public class CommentWrapView {

    /**
     * 当前评论
     */
    protected Comment comment;

    /**
     * 父评论
     */
    protected Comment parent;

    /**
     * 子评论
     */
    protected List<CommentWrapView> childList;

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public List<CommentWrapView> getChildList() {
        return childList;
    }

    public void setChildList(List<CommentWrapView> childList) {
        this.childList = childList;
    }
    
}
