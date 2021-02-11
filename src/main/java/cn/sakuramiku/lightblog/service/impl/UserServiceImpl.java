package cn.sakuramiku.lightblog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.sakuramiku.lightblog.common.util.IdUtil;
import cn.sakuramiku.lightblog.common.util.SecurityUtil;
import cn.sakuramiku.lightblog.entity.Account;
import cn.sakuramiku.lightblog.entity.User;
import cn.sakuramiku.lightblog.mapper.AccountMapper;
import cn.sakuramiku.lightblog.mapper.UserMapper;
import cn.sakuramiku.lightblog.service.UserService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
@CacheConfig(cacheNames = "user", keyGenerator = "keyGenerator")
@CachePut()
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private AccountMapper accountMapper;

    @Override
    public boolean login(String username, String password) {
        password = SecurityUtil.md5(password);
        Account account = accountMapper.checkLogin(username, password);
        return !ObjectUtil.isNull(account);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean register(String username, String password) {
        long id = IdUtil.nextId();
        LocalDateTime now = LocalDateTime.now(ZoneId.ofOffset("GMT", ZoneOffset.ofHours(8)));
        password = SecurityUtil.md5(password);
        Account account = new Account();
        account.setId(id);
        account.setUsername(username);
        account.setPassword(password);
        account.setCreateTime(now);
        accountMapper.insert(account);
        return userMapper.insertBase(id, username, now);
    }

    @Cacheable(key = "#username")
    @Override
    public User getUser(String username) {
        List<User> users = userMapper.selectUserByPrimaryKey(null, username);
        return users.get(0);
    }

    @Cacheable
    @Override
    public User getUser(long id) {
        List<User> users = userMapper.selectUserByPrimaryKey(id, null);
        return users.get(0);
    }

    @CachePut(key = "#user.getUsername()")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateUser(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Cacheable
    @Override
    public List<User> searchUser(String keyword) {
        return userMapper.selectUserByKeyword(keyword);
    }
}
