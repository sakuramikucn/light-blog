package cn.sakuramiku.lightblog.mapper;


import cn.sakuramiku.lightblog.entity.Right;
import org.apache.ibatis.annotations.Param;
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
     * @param ref 引用，一般为角色ID
     * @return 是否成功
     */
    Boolean delete(@Param("id") Long id,@Param("ref") String ref);


    /**
     * 添加权限
     *
     * @param record 权限
     * @return 是否成功
     */
    Boolean insert(@NonNull Right record);

    /**
     * 获取权限
     *
     * @param id 权限ID
     * @return 权限
     */
    Right get(@NonNull @Param("id") Long id);

    /**
     * 修改权限
     *
     * @param record 参数
     * @return 是否成功
     */
    Boolean update(@NonNull Right record);

    /**
     * 搜索权限
     *
     * @param ref 引用，一般为角色ID
     * @return 权限列表
     */
    List<Right> search(@Nullable @Param("ref") String ref);
}