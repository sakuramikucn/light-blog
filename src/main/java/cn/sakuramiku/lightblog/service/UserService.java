package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.User;
import cn.sakuramiku.lightblog.exception.BusinessException;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 用户服务接口
 *
 * @author lyy
 */
public interface UserService {

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     *                 @param ipAddr 登录ip地址
     * @return 是否成功
     */
    User login(@NonNull String username, @NonNull String password,String ipAddr) throws BusinessException;

    /**
     * 注册
     *
     * @param username 用户名
     * @param password 密码
     *
     * @return 是否成功
     */
    User register(@NonNull String username, @NonNull String password);

    /**
     * 获取用户
     *
     * @param username 用户名
     * @return 用户
     */
    User getUser(@NonNull String username);

    /**
     * 获取用户
     *
     * @param id 用户ID
     * @return 用户
     */
    User getUser(@NonNull Long id);

    /**
     * 修改用户
     *
     * @param user 参数
     * @return 是否成功
     */
    User updateUser(@NonNull User user);

    /**
     * 删除用户
     * @param id
     * @return
     */
    Boolean delete(Long id);

    /**
     * 搜索用户
     *
     * @param keyword 用户名/昵称关键字
     * @param state
     * @return 用户列表
     */
    PageInfo<User> searchUser(@Nullable String keyword,Integer state);

    /**
     * 搜索用户
     *
     * @param keyword  用户名/昵称关键字
     * @param state
     * @param page     第几页
     * @param pageSize 每页项数
     * @return 用户列表
     */
    PageInfo<User> searchUser(@Nullable String keyword,Integer state, @Nullable Integer page, @Nullable Integer pageSize);
}
