package cn.sakuramiku.lightblog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户实体类
 *
 * @author lyy
 */
@ApiModel(value = "用户实体类")
public class User implements Serializable {

    /**
     * 唯一标识
     */
    @ApiModelProperty(value = "唯一标识")
    private Long id;

    /**
     * 用户名，用于登录
     */
    @ApiModelProperty(value = "用户名，用于登录")
    private String username;

    /**
     * 状态，0=正常，2=冻结
     */
    @ApiModelProperty(value = "状态，0=正常，1=冻结，3=删除")
    private Integer state;

    @ApiModelProperty(value = "昵称")
    private String nickName;
    /**
     * 邮箱，用于接收通知
     */
    @ApiModelProperty(value = "邮箱，用于接收通知")
    private String email;

    @ApiModelProperty(value = "角色")
    private List<Role> roles;

    /**
     * 最近登录时间
     */
    @ApiModelProperty(value = "最近登录时间")
    private LocalDateTime lastLoginTime;

    /**
     * 最近登录IP
     */
    @ApiModelProperty(value = "最近登录IP")
    private String lastLoginIp;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
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

    public List<Role> getRoles() {
        if (null == roles) {
            roles = new ArrayList<>();
        }
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}