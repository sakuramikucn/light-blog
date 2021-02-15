package cn.sakuramiku.lightblog.mapper;


import cn.sakuramiku.lightblog.entity.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 角色SQL映射
 *
 * @author LYY
 */
public interface RoleMapper {

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 是否成功
     */
    Boolean delete(@NonNull @Param("id") Long id);

    /**
     * 添加角色
     *
     * @param record 角色
     * @return 是否成功
     */
    Boolean insert(@NonNull Role record);

    /**
     * 获取角色
     *
     * @param id 角色ID
     * @return 角色
     */
    Role get(@NonNull @Param("id") Long id);

    /**
     * 搜索角色
     *
     * @param ref     引用，一般为用户ID
     * @param keyword 关键字
     * @return 角色列表
     */
    List<Role> search(@Nullable @Param("ref") String ref, @Nullable @Param("keyword") String keyword);

    /**
     * 修改角色
     *
     * @param record 参数
     * @return 是否成功
     */
    Boolean update(@NonNull Role record);
}