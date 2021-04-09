package cn.sakuramiku.lightblog.vo;

import cn.sakuramiku.lightblog.util.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 搜索文章参数
 *
 * @author lyy
 */
@ApiModel("搜索文章参数")
public class SearchArticleParam extends PageParam {

    @ApiModelProperty("状态（1=正常，2=待删除）")
    protected Integer state = Constant.ARTICLE_STATE_NORMAL;
    @ApiModelProperty("文章关键字")
    protected String keyword;
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    protected Date begin;
    @ApiModelProperty(value = "截止时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    protected Date end;
    @ApiModelProperty("排序方式，创键时间（ASC=升序,DESC=降序）")
    protected String order = "DESC";
    @ApiModelProperty("是否全文搜索")
    protected Boolean isFull = false;
    @ApiModelProperty(value = "按浏览数排序")
    protected Boolean isHotOrderBy = false;
    @ApiModelProperty(value = "是否公开")
    protected Boolean isPublic ;

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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Boolean getHotOrderBy() {
        return isHotOrderBy;
    }

    public void setHotOrderBy(Boolean hotOrderBy) {
        isHotOrderBy = hotOrderBy;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
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
                ", isHotOrderBy=" + isHotOrderBy +
                ", isPublic=" + isPublic +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
