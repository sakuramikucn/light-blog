package cn.sakuramiku.lightblog.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.StringJoiner;

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
    @ApiModelProperty("关键字")
    public String keyword;
    @ApiModelProperty("类型，1=文章，2=留言板")
    public Integer type = 0;

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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SearchCommentParam.class.getSimpleName() + "[", "]")
                .add("page=" + page)
                .add("pageSize=" + pageSize)
                .add("ref='" + ref + "'")
                .add("parentId=" + parentId)
                .add("state=" + state)
                .add("keyword='" + keyword + "'")
                .add("type=" + type)
                .toString();
    }
}
