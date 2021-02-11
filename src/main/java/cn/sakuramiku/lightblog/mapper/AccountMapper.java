package cn.sakuramiku.lightblog.mapper;

import cn.sakuramiku.lightblog.entity.Account;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AccountMapper {

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    @Select("select id from account where username=#{username} and password=#{password}")
    Account checkLogin(@Param("username") String username, @Param("password") String password);

    /**
     * 根据用户名查询
     *
     * @param username
     * @return
     */
    @Select("select id,username from account where username=#{username}")
    Account selectAccountByUsername(String username);

    /**
     * 根据主键删除账号
     *
     * @param id
     * @param username
     * @return
     */
    boolean deleteBySelective(@Param("id") long id, @Param("username") String username);

    /**
     * 添加账号
     *
     * @param record
     * @return
     */
    boolean insert(Account record);

    /**
     * 更新密码
     *
     * @param record
     * @return
     */
    boolean updateByPrimaryKeySelective(Account record);

}