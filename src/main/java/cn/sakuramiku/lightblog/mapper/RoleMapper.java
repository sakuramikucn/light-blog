package cn.sakuramiku.lightblog.mapper;


import cn.sakuramiku.lightblog.entity.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色mapper
 *
 * @author LYY
 */
public interface RoleMapper {

    boolean deleteByPrimaryKey(@Param("id") Long id);

    boolean insert(Role record);

    boolean insertSelective(Role record);

    Role selectByPrimaryKey(@Param("id") Long id);

    List<Role> selectBySelective(@Param("ref") String ref, @Param("keyword") String keyword);

    boolean updateByPrimaryKeySelective(Role record);

    boolean updateByPrimaryKey(Role record);
}