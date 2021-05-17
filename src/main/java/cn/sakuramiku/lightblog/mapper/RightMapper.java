package cn.sakuramiku.lightblog.mapper;


import cn.sakuramiku.lightblog.entity.Right;
import cn.sakuramiku.lightblog.model.BatchInsertParam;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 权限SQL映射
 *
 * @author LYY
 */
public interface RightMapper {

    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return 是否成功
     */
    Boolean delete(@Param("id") Long id);

    /**
     * 添加权限
     *
     * @param record 权限
     * @return 是否成功
     */
    Boolean add(@NonNull Right record);

    /**
     * 插入一条记录到中间表
     *
     * @param roleId  角色ID
     * @param rightId 权限ID
     * @return 是否成功
     */
    Boolean insert(@Param("roleId") Long roleId, @Param("rightId") Long rightId);

    /**
     * 获取权限
     *
     * @param id 权限ID
     * @return 权限
     */
    Right get(@NonNull @Param("id") Long id);

    /**
     * 获取权限
     *
     * @param name 权限名称
     * @return 权限
     */
    @Select("select * from `right` where name = #{name}")
    Right getByName(@NonNull @Param("name") String name);

    /**
     * 修改权限
     *
     * @param record 参数
     * @return 是否成功
     */
    Boolean update(@NonNull Right record);

    /**
     * 搜索角色权限
     *
     * @param role 角色ID
     * @return 权限列表
     */
    List<Right> search(@Nullable @Param("role") Long role);

    /**
     * 搜索权限
     *
     * @return
     * @param keyword
     */
    List<Right> find(@Param("keyword") String keyword);

    Boolean batchInsert(List<BatchInsertParam> params);

    /**
     * 删除角色关联的权限映射
     * @param ref
     * @return
     */
    Boolean remove(Long ref);

    Long roleCount(Long right);
}