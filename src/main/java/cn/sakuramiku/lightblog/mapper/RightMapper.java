package cn.sakuramiku.lightblog.mapper;


import cn.sakuramiku.lightblog.entity.Right;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RightMapper {

    boolean deleteByPrimaryKey(@Param("id") Long id);

    boolean insertSelective(Right record);

    Right selectByPrimaryKey(@Param("id") Long id);

    boolean updateByPrimaryKeySelective(Right record);

    List<Right> selectByRef(@Param("ref")String ref);
}