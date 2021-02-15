package cn.sakuramiku.lightblog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 分页参数
 *
 * @author lyy
 */
@ApiModel("分页参数")
public class PageParam {

    @ApiModelProperty(value = "第几页", example = "1")
    protected Integer page = 1;
    @ApiModelProperty(value = "每页多少项", example = "20")
    protected Integer pageSize = 20;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
