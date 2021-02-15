package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.common.util.IdUtil;
import cn.sakuramiku.lightblog.common.util.SecurityUtil;
import cn.sakuramiku.lightblog.entity.Account;
import cn.sakuramiku.lightblog.entity.User;
import cn.sakuramiku.lightblog.mapper.AccountMapper;
import cn.sakuramiku.lightblog.mapper.UserMapper;
import cn.sakuramiku.lightblog.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

/**
 * 用户服务
 *
 * @author lyy
 */
@Service("userService")
@CacheConfig(cacheNames = "light_blog:user", keyGenerator = "simpleKeyGenerator")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private AccountMapper accountMapper;

    @Override
    public Boolean login(@NonNull String username, @NonNull String password) {
        password = SecurityUtil.md5(password);
        Account account = accountMapper.checkLogin(username, password);
        return null != account;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean register(@NonNull String username, @NonNull String password) {
        long id = IdUtil.nextId();
        LocalDateTime now = LocalDateTime.now(ZoneId.ofOffset("GMT", ZoneOffset.ofHours(8)));
        password = SecurityUtil.md5(password);
        Account account = new Account();
        account.setId(id);
        account.setUsername(username);
        account.setPassword(password);
        account.setCreateTime(now);
        accountMapper.insert(account);
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setCreateTime(now);
        return userMapper.insert(user);
    }

    @Cacheable(key = "#username", unless = "null == #result")
    @Override
    public User getUser(@NonNull String username) {
        return userMapper.get(null, username);
    }

    @Cacheable(unless = "null == #result")
    @Override
    public User getUser(@NonNull Long id) {
        return userMapper.get(id, null);
    }

    @CachePut(key = "#user.username")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateUser(@NonNull User user) {
        return userMapper.update(user);
    }

    @Override
    public PageInfo<User> searchUser(String keyword) {
        return searchUser(keyword, null, null);
    }

    @Cacheable(unless = "#result==null || 0 == #result.total")
    @Override
    public PageInfo<User> searchUser(String keyword, Integer page, Integer pageSize) {
        if (null != page && null != pageSize) {
            PageHelper.startPage(page, pageSize, true);
        }
        List<User> users = userMapper.search(keyword);
        return PageInfo.of(users);
    }
}
