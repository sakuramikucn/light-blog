package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author lyy
 */
public interface RoleService {

    /**
     * 创建角色
     *
     * @param role
     * @return
     */
    Role saveRole(Role role);

    /**
     * 修改角色
     *
     * @param role
     * @return
     */
    boolean updateRole(Role role);

    /**
     * 移除角色
     *
     * @param id
     * @return
     */
    boolean removeRole(Long id);

    /**
     * 获取角色
     *
     * @param id 角色ID
     * @return
     */
    Role getRole(Long id);

    /**
     * 获取用户角色
     *
     * @param userId
     * @return
     */
    List<Role> getRoles(Long userId);

    /**
     * 搜索角色
     *
     * @param userId  用户ID
     * @param keyword 名称或描述关键字
     * @return
     */
    List<Role> searchRole(Long userId, String keyword);
}
