package cn.sakuramiku.lightblog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

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

    /**
     * 发表评论时的昵称
     */
    @ApiModelProperty(value = "发表评论时的昵称")
    private String nickName;

    /**
     * 接收回复通知的邮箱
     */
    @ApiModelProperty(value = "接收回复通知的邮箱")
    private String email;

    /**
     * 状态，0=正常，1=屏蔽，3=删除
     */
    @ApiModelProperty(value = "状态，0=正常，1=屏蔽，3=删除", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Integer state;

    /**
     * 评论内容
     */
    @NotBlank
    @ApiModelProperty(value = "评论内容")
    private String content;

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

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", reference='" + reference + '\'' +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", state=" + state +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", modifiedTime=" + modifiedTime +
                '}';
    }
}