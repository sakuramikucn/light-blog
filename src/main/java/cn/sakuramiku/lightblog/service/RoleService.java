package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Role;
import cn.sakuramiku.lightblog.exception.BusinessException;
import cn.sakuramiku.lightblog.model.BatchInsertParam;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

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
     * @param role 角色
     * @return 角色ID
     */
    Role saveRole(@NonNull Role role);

    /**
     * 修改角色
     *
     * @param role 参数
     * @return 是否成功
     */
    Role updateRole(@NonNull Role role) throws BusinessException;

    /**
     * 移除角色
     *
     * @param id  角色ID
     * @return 是否成功
     */
    Boolean removeRole(Long id) throws BusinessException;

    Boolean removeRoleForUser(Long userId);

    /**
     * 获取角色
     *
     * @param id 角色ID
     * @return 角色
     */
    Role getRole(@NonNull Long id);

    Role getRoleByName(String name);

    /**
     * 获取用户角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    PageInfo<Role> getRoles(@NonNull Long userId, @Nullable Integer page, @Nullable Integer pageSize);

    /**
     * 搜索角色
     *
     * @param userId   用户ID
     * @param keyword  名称或描述关键字
     * @param page     页
     * @param pageSize 项数
     * @return 角色列表
     */
    PageInfo<Role> searchRole(@Nullable Long userId, @Nullable String keyword,
                              @Nullable Integer page, @Nullable Integer pageSize);

    Boolean addRoles(List<BatchInsertParam> params);

}
