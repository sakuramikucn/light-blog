package cn.sakuramiku.lightblog.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @author lyy
 */
public class SearchLogParam extends PageParam {

    @NotBlank
    @ApiModelProperty(value = "引用标识", required = true)
    protected String ref;
    @ApiModelProperty(value = "开始时间")
    protected LocalDateTime begin;
    @ApiModelProperty(value = "截止时间")
    protected LocalDateTime end;

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
}
