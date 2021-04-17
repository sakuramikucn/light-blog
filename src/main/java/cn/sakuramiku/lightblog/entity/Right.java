package cn.sakuramiku.lightblog.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 权限实体类
 *
 * @author lyy
 */
@ApiModel(value = "权限实体类")
public class Right implements Serializable {

    /**
     * 规则-未指定
     */
    public static final int RULE_NOT = 0;
    /**
     * 规则-允许
     */
    public static final int RULE_ALLOW = 2;
    /**
     * 规则-禁止
     */
    public static final int RULE_BAN = 3;

    /**
     * 唯一标识
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "唯一标识")
    private Long id;

    @ApiModelProperty(value = "授权url，例如：/login、/admin/*")
    private String name;

    /**
     * 规则，0=未指定，1=允许，2=禁止
     */
    @ApiModelProperty(value = "规则，0=未指定，1=允许，2=禁止")
    private Integer rule = RULE_NOT;

    @ApiModelProperty(value = "权限描述")
    private String desc;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private LocalDateTime modifiedTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRule() {
        return rule;
    }

    public void setRule(Integer rule) {
        this.rule = rule;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(LocalDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Right right = (Right) o;
        return id.equals(right.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Right{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rule=" + rule +
                ", desc='" + desc + '\'' +
                ", createTime=" + createTime +
                ", modifiedTime=" + modifiedTime +
                '}';
    }
}