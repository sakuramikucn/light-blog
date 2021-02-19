package cn.sakuramiku.lightblog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * @author lyy
 */
@ApiModel("搜索分类参数")
public class SearchCategoryParam extends PageParam {

    @ApiModelProperty("名称关键字")
    protected String keyword;
    @ApiModelProperty(value = "开始时间")
    protected LocalDateTime begin;
    @ApiModelProperty(value = "截止时间")
    protected LocalDateTime end;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public LocalDateTime getBegin() {
        return begin;
    }

    public void setBegin(LocalDateTime begin) {
        this.begin = begin;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}
