package cn.sakuramiku.lightblog.mapper;

import cn.sakuramiku.lightblog.entity.Account;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 账户SQL映射
 *
 * @author LYY
 */
public interface AccountMapper {

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 账号
     */
    @Select("select id from account where username=#{username} and password=#{password}")
    Account checkLogin(@NonNull @Param("username") String username, @NonNull @Param("password") String password);

    /**
     * 获取账号
     *
     * @param username 用户名
     * @return 账户
     */
    @Select("select id,username from account where username=#{username}")
    Account get(@NonNull String username);

    /**
     * 删除账号
     *
     * @param id       账号ID
     * @param username 用户名
     * @return 是否成功
     */
    Boolean delete(@Nullable @Param("id") Long id, @Nullable @Param("username") String username);

    /**
     * 添加账号
     *
     * @param record 参数
     * @return 是否成功
     */
    Boolean insert(@NonNull Account record);

    /**
     * 修改密码
     *
     * @param id       账号ID,与{@code username}二选一
     * @param username 用户名,与{@code id}二选一
     * @param password 新密码
     * @return 是否成功
     */
    Boolean update(@Nullable @Param("id") Long id,
                   @Nullable @Param("username") String username,
                   @NonNull @Param("password") String password);



}