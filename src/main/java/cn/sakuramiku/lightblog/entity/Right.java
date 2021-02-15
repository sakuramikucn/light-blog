package cn.sakuramiku.lightblog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限实体类
 *
 * @author lyy
 */
@ApiModel(value = "角色实体类")
public class Right implements Serializable {
    /**
     * 唯一标识
     */
    @ApiModelProperty(value = "唯一标识")
    private Long id;

    /**
     * 授权url，例如：/login、/admin/*
     */
    @ApiModelProperty(value = "授权url，例如：/login、/admin/*")
    private String pattern;

    /**
     * 规则，0=未指定，1=允许，2=禁止
     */
    @ApiModelProperty(value = "规则，0=未指定，1=允许，2=禁止")
    private Boolean rule;

    /**
     * 引用，主要是角色ID，用于组织角色的权限
     */
    @ApiModelProperty(value = "引用，主要是角色ID，用于组织角色的权限")
    private String reference;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime modifiedTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Boolean getRule() {
        return rule;
    }

    public void setRule(Boolean rule) {
        this.rule = rule;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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
}