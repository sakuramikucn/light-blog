package cn.sakuramiku.lightblog.mapper;

import cn.sakuramiku.lightblog.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;

/**
 * 用户SQL映射
 *
 * @author LYY
 */
public interface UserMapper {

    /**
     * 获取用户
     *
     * @param id       用户ID
     * @param username 用户名
     * @return 用户
     */
    User get(@Nullable @Param("id") Long id, @Nullable @Param("username") String username);

    /**
     * 搜索用户
     *
     * @param keyword 用户名/昵称 关键字
     * @return 用户列表
     */
    List<User> search(@Nullable @Param("keyword") String keyword,
                      @Param("state") Integer state,
                      @Param("start") Date start,
                      @Param("end") Date end);

    /**
     * 添加用户
     *
     * @param record 用户
     * @return 是否成功
     */
    Boolean insert(@NonNull User record);

    /**
     * 修改用户
     *
     * @param record 参数
     * @return 是否成功
     */
    Boolean update(@NonNull User record);

    Boolean delete(Long id);

}