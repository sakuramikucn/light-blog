package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.User;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author lyy
 */
public interface UserService {

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    boolean login(String username, String password);

    /**
     * 注册
     *
     * @param username
     * @param password
     * @return
     */
    boolean register(String username, String password);

    /**
     * 获取用户
     *
     * @param username
     * @return
     */
    User getUser(String username);

    /**
     * 获取用户
     *
     * @param id 用户ID
     * @return
     */
    User getUser(long id);

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    boolean updateUser(User user);

    /**
     * 搜索用户
     *
     * @param keyword
     * @return
     */
    List<User> searchUser(String keyword);
}
