package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.User;
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
     * @return 是否成功
     */
    Boolean login(@NonNull String username, @NonNull String password);

    /**
     * 注册
     *
     * @param username 用户名
     * @param password 密码
     * @return 是否成功
     */
    Boolean register(@NonNull String username, @NonNull String password);

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
    Boolean updateUser(@NonNull User user);

    /**
     * 搜索用户
     *
     * @param keyword 用户名/昵称关键字
     * @return 用户列表
     */
    PageInfo<User> searchUser(@Nullable String keyword);

    /**
     * 搜索用户
     *
     * @param keyword  用户名/昵称关键字
     * @param page     第几页
     * @param pageSize 每页项数
     * @return 用户列表
     */
    PageInfo<User> searchUser(@Nullable String keyword, @Nullable Integer page, @Nullable Integer pageSize);
}
