package cn.sakuramiku.lightblog.mapper;

import cn.sakuramiku.lightblog.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserMapper {

    List<User> selectUserByPrimaryKey(@Param("id") Long id, @Param("username") String username);

    List<User> selectUserByKeyword(@Param("keyword") String keyword);

    @Insert("insert into user(id,username,create_time) values(#{id},#{username},#{createTime})")
    boolean insertBase(@Param("id") Long id, @Param("username") String username, @Param("createTime") LocalDateTime createTime);

    boolean insertSelective(User record);

    boolean updateByPrimaryKeySelective(User record);

    boolean updateByPrimaryKey(User record);
}