package cn.sakuramiku.lightblog.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 日志实体类
 *
 * @author lyy
 */
@ApiModel(value = "日志实体类")
public class Log implements Serializable {
    /**
     * 唯一标识
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "唯一标识")
    private Long id;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private String operator;

    /**
     * 行为，比如删除、创建
     */
    @ApiModelProperty(value = "行为，比如删除、创建")
    private String action;

    /**
     * 做了什么
     */
    @ApiModelProperty(value = "做了什么")
    private String what;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String note;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 引用标识（用于找回日志），对象ID、标识字符串等
     */
    @ApiModelProperty(value = "引用标识（用于找回日志），对象ID、标识字符串等")
    private String reference;

    private LocalDateTime modifiedTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(LocalDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", operator='" + operator + '\'' +
                ", action='" + action + '\'' +
                ", what='" + what + '\'' +
                ", note='" + note + '\'' +
                ", createTime=" + createTime +
                ", reference='" + reference + '\'' +
                ", modifiedTime=" + modifiedTime +
                '}';
    }
}