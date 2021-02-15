package cn.sakuramiku.lightblog.vo;

import cn.sakuramiku.lightblog.util.Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.Date;

/**
 * 搜索文章参数
 *
 * @author lyy
 */
@ApiModel("搜索文章参数")
public class SearchArticleParam extends PageParam {

    @Hidden
    protected Byte state = Constant.ARTICLE_STATE_NORMAL;
    @ApiModelProperty("文章关键字")
    protected String keyword;
    @ApiModelProperty("开始时间")
    protected Date begin;
    @ApiModelProperty("截止时间")
    protected Date end;
    @ApiModelProperty("排序方式，创键时间（ASC=升序,DESC=降序）")
    protected String order = "DESC";
    @ApiModelProperty("是否全文搜索")
    protected Boolean isFull = false;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Boolean getFull() {
        return isFull;
    }

    public void setFull(Boolean full) {
        isFull = full;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "SearchArticleParam{" +
                "state=" + state +
                ", keyword='" + keyword + '\'' +
                ", begin=" + begin +
                ", end=" + end +
                ", order='" + order + '\'' +
                ", isFull=" + isFull +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}