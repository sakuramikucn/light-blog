package cn.sakuramiku.lightblog.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分类实体类
 *
 * @author lyy
 */
@ApiModel(value = "分类实体类")
public class Category implements Serializable {
    /**
     * 唯一标识
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "唯一标识", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Long id;

    /**
     * 名称
     */
    @NotBlank
    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "创建时间", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private LocalDateTime createTime;

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

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                ", modifiedTime=" + modifiedTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;

        Category category = (Category) o;

        if (getId() != null ? !getId().equals(category.getId()) : category.getId() != null) return false;
        if (getName() != null ? !getName().equals(category.getName()) : category.getName() != null) return false;
        if (getCreateTime() != null ? !getCreateTime().equals(category.getCreateTime()) : category.getCreateTime() != null)
            return false;
        return getModifiedTime() != null ? getModifiedTime().equals(category.getModifiedTime()) : category.getModifiedTime() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getCreateTime() != null ? getCreateTime().hashCode() : 0);
        result = 31 * result + (getModifiedTime() != null ? getModifiedTime().hashCode() : 0);
        return result;
    }
}