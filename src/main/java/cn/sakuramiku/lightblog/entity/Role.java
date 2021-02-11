package cn.sakuramiku.lightblog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * role
 *
 * @author
 */
@ApiModel(value = "generate.Role角色表")
public class Role implements Serializable {
    /**
     * 唯一标识
     */
    @ApiModelProperty(value = "唯一标识")
    private Long id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 引用，主要是用户ID，用于组织用户的权限
     */
    @ApiModelProperty(value = "引用，主要是用户ID，用于组织用户的权限")
    private String reference;

    @ApiModelProperty(value = "说明")
    private String desc;


    @ApiModelProperty(value = "权限")
    private List<Right> rights;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<Right> getRights() {
        if (null == rights) {
            rights = new ArrayList<>();
        }
        return rights;
    }

    public void setRights(List<Right> rights) {
        this.rights = rights;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}