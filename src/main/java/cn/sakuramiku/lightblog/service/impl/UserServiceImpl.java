package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.annotation.OnChange;
import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.IdGenerator;
import cn.sakuramiku.lightblog.common.util.RedisUtil;
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
@LogConfig(reference = "user",name = "用户")
@CacheConfig(cacheNames = "light_blog:user", keyGenerator = "simpleKeyGenerator")
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private RedisUtil redisUtil;

    @WriteLog(action = WriteLog.Action.UPDATE)
    @Override
    public Boolean login(@NonNull String username, @NonNull String password) {
        password = SecurityUtil.md5(password);
        Account account = accountMapper.checkLogin(username, password);
        if (null != account) {
            User user = new User();
            user.setId(account.getId());
            user.setLastLoginTime(LocalDateTime.now());
            userMapper.update(user);
            return true;
        }
        return false;
    }

    @WriteLog(action = WriteLog.Action.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean register(@NonNull String username, @NonNull String password) {
        long id = IdGenerator.nextId();
        LocalDateTime now = LocalDateTime.now(ZoneId.ofOffset("GMT", ZoneOffset.ofHours(8)));
        password = SecurityUtil.md5(password);
        Account account = new Account();
        account.setId(id);
        account.setUsername(username);
        account.setPassword(password);
        account.setCreateTime(now);
        Boolean insert = accountMapper.insert(account);
        if (insert) {
            User user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setNickName(username);
            user.setCreateTime(now);
            return userMapper.insert(user);
        }
        return false;
    }

    @Override
    public User getUser(@NonNull String username) {
        Object o = redisUtil.get("light_blog:user::" + username);
        if (null != o){
            return (User) o;
        }
        return userMapper.get(null, username);
    }

    @Cacheable(unless = "null == #result")
    @Override
    public User getUser(@NonNull Long id) {
        return userMapper.get(id, null);
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @CachePut(key = "#user.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateUser(@NonNull User user) {
        return userMapper.update(user);
    }

    @WriteLog(action = WriteLog.Action.DELETE)
    @CachePut(key = "#id")
    @Override
    public Boolean delete(Long id) {
        return userMapper.delete(id) && accountMapper.delete(id,null);
    }

    @Override
    public PageInfo<User> searchUser(String keyword) {
        return searchUser(keyword, null, null);
    }

    @OnChange
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
