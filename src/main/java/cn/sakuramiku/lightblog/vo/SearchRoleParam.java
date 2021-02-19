package cn.sakuramiku.lightblog.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author lyy
 */
public class SearchRoleParam extends PageParam {

    @ApiModelProperty("用户ID")
    protected Long userId;
    @ApiModelProperty("名称/描述 关键字")
    protected String keyword;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
