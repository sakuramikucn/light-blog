package cn.sakuramiku.lightblog.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.StringJoiner;

/**
 * 评论实体类
 *
 * @author lyy
 */
@ApiModel(value = "评论实体类")
public class Comment implements Serializable {
    /**
     * 唯一标识
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "唯一标识", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Long id;

    /**
     * 父评论ID，为0则表示没有父评论
     */
    @ApiModelProperty(value = "父评论ID，为0则表示没有父评论")
    private Long parentId;

    /**
     * 引用标识，用于找回对象的评论，一般为对象ID
     */
    @ApiModelProperty(value = "引用标识，用于找回对象的评论，一般为对象ID")
    private String reference;

    @ApiModelProperty(value = "子引用")
    private String subReference;

    /**
     * 发表评论时的昵称
     */
    @ApiModelProperty(value = "发表评论时的昵称")
    private String nickName;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 角色类型。0游客，1管理员，2作者
     */
    private Integer roleType;

    /**
     * 接收回复通知的邮箱
     */
    @ApiModelProperty(value = "接收回复通知的邮箱")
    private String email;

    /**
     * 状态，0=正常，1=屏蔽，2=删除
     */
    @ApiModelProperty(value = "状态，0=正常，1=屏蔽，2=删除", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Integer state;

    /**
     * 评论内容
     */
    @NotBlank
    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "类型")
    private Integer type;

    /**
     * 评论时间
     */
    @ApiModelProperty(value = "评论时间", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "评论时间", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private LocalDateTime modifiedTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getSubReference() {
        return subReference;
    }

    public void setSubReference(String subReference) {
        this.subReference = subReference;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;

        Comment comment = (Comment) o;

        if (getId() != null ? !getId().equals(comment.getId()) : comment.getId() != null) return false;
        if (getParentId() != null ? !getParentId().equals(comment.getParentId()) : comment.getParentId() != null)
            return false;
        if (getReference() != null ? !getReference().equals(comment.getReference()) : comment.getReference() != null)
            return false;
        if (getSubReference() != null ? !getSubReference().equals(comment.getSubReference()) : comment.getSubReference() != null)
            return false;
        if (getNickName() != null ? !getNickName().equals(comment.getNickName()) : comment.getNickName() != null)
            return false;
        if (getUsername() != null ? !getUsername().equals(comment.getUsername()) : comment.getUsername() != null)
            return false;
        if (getRoleType() != null ? !getRoleType().equals(comment.getRoleType()) : comment.getRoleType() != null)
            return false;
        if (getEmail() != null ? !getEmail().equals(comment.getEmail()) : comment.getEmail() != null) return false;
        if (getState() != null ? !getState().equals(comment.getState()) : comment.getState() != null) return false;
        if (getContent() != null ? !getContent().equals(comment.getContent()) : comment.getContent() != null)
            return false;
        if (getType() != null ? !getType().equals(comment.getType()) : comment.getType() != null) return false;
        if (getCreateTime() != null ? !getCreateTime().equals(comment.getCreateTime()) : comment.getCreateTime() != null)
            return false;
        return getModifiedTime() != null ? getModifiedTime().equals(comment.getModifiedTime()) : comment.getModifiedTime() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getParentId() != null ? getParentId().hashCode() : 0);
        result = 31 * result + (getReference() != null ? getReference().hashCode() : 0);
        result = 31 * result + (getSubReference() != null ? getSubReference().hashCode() : 0);
        result = 31 * result + (getNickName() != null ? getNickName().hashCode() : 0);
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + (getRoleType() != null ? getRoleType().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getState() != null ? getState().hashCode() : 0);
        result = 31 * result + (getContent() != null ? getContent().hashCode() : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getCreateTime() != null ? getCreateTime().hashCode() : 0);
        result = 31 * result + (getModifiedTime() != null ? getModifiedTime().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Comment.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("parentId=" + parentId)
                .add("reference='" + reference + "'")
                .add("subReference='" + subReference + "'")
                .add("nickName='" + nickName + "'")
                .add("username='" + username + "'")
                .add("roleType=" + roleType)
                .add("email='" + email + "'")
                .add("state=" + state)
                .add("content='" + content + "'")
                .add("type=" + type)
                .add("createTime=" + createTime)
                .add("modifiedTime=" + modifiedTime)
                .toString();
    }
}