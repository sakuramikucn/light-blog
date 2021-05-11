package cn.sakuramiku.lightblog.vo;

import cn.sakuramiku.lightblog.util.Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.StringJoiner;

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
    protected Date begin;
    @ApiModelProperty(value = "截止时间")
    protected Date end;
    @ApiModelProperty("排序方式，创键时间（ASC=升序,DESC=降序）")
    protected String order = "DESC";
    @ApiModelProperty("是否全文搜索")
    protected Boolean isFull = false;
    @ApiModelProperty(value = "按浏览数排序")
    protected Boolean isHotOrderBy = false;
    @ApiModelProperty(value = "是否公开")
    protected Boolean isPublic ;
    @ApiModelProperty(value = "是否简单搜索，不查浏览数和评论数")
    protected Boolean isSimple = false;
    @ApiModelProperty(value = "分类")
    protected String category;

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

    public Boolean getSimple() {
        return isSimple;
    }

    public void setSimple(Boolean simple) {
        isSimple = simple;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SearchArticleParam.class.getSimpleName() + "[", "]")
                .add("page=" + page)
                .add("pageSize=" + pageSize)
                .add("state=" + state)
                .add("keyword='" + keyword + "'")
                .add("begin=" + begin)
                .add("end=" + end)
                .add("order='" + order + "'")
                .add("isFull=" + isFull)
                .add("isHotOrderBy=" + isHotOrderBy)
                .add("isPublic=" + isPublic)
                .add("isSimple=" + isSimple)
                .add("category='" + category + "'")
                .toString();
    }
}
