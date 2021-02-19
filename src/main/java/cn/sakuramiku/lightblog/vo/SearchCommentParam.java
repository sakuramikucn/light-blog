package cn.sakuramiku.lightblog.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author lyy
 */
public class SearchCommentParam extends PageParam {

    @ApiModelProperty("引用标识，一般为文章ID")
    public String ref;
    @ApiModelProperty("父评论ID")
    public Long parentId;
    @ApiModelProperty(value = "状态（1=正常，2=屏蔽，3=删除）")
    public Integer state;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
