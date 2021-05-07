package cn.sakuramiku.lightblog.vo;

import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * @author lyy
 */
public class SearchLogParam extends PageParam {

    @ApiModelProperty(value = "分类")
    protected String category;
    @ApiModelProperty(value = "引用标识")
    protected String ref;
    @ApiModelProperty(value = "开始时间")
    protected LocalDateTime begin;
    @ApiModelProperty(value = "截止时间")
    protected LocalDateTime end;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
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

    @Override
    public String toString() {
        return "SearchLogParam{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", category='" + category + '\'' +
                ", ref='" + ref + '\'' +
                ", begin=" + begin +
                ", end=" + end +
                '}';
    }
}
