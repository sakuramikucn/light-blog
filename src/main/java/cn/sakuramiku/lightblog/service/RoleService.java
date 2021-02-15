package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Role;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

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
    Long saveRole(@NonNull Role role);

    /**
     * 修改角色
     *
     * @param role 参数
     * @return 是否成功
     */
    Boolean updateRole(@NonNull Role role);

    /**
     * 移除角色
     *
     * @param id 角色ID
     * @return 是否成功
     */
    Boolean removeRole(@NonNull Long id);

    /**
     * 获取角色
     *
     * @param id 角色ID
     * @return 角色
     */
    Role getRole(@NonNull Long id);

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
     * @param userId  用户ID
     * @param keyword 名称或描述关键字
     * @return
     */
    PageInfo<Role> searchRole(@Nullable Long userId, @Nullable String keyword,
                              @Nullable Integer page, @Nullable Integer pageSize);
}
