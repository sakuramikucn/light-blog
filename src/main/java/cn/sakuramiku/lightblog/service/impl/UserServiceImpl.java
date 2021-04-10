package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.annotation.OnChange;
import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.IdGenerator;
import cn.sakuramiku.lightblog.common.util.RedisUtil;
import cn.sakuramiku.lightblog.common.util.SecurityUtil;
import cn.sakuramiku.lightblog.entity.Account;
import cn.sakuramiku.lightblog.entity.User;
import cn.sakuramiku.lightblog.exception.BusinessException;
import cn.sakuramiku.lightblog.mapper.AccountMapper;
import cn.sakuramiku.lightblog.mapper.UserMapper;
import cn.sakuramiku.lightblog.service.UserService;
import cn.sakuramiku.lightblog.util.Constant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
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
@LogConfig(reference = "user", name = "用户")
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
    @CachePut(key = "#result.id", unless = "null == #result")
    @Override
    public User login(@NonNull String username, @NonNull String password,String ipAddr) throws BusinessException {
        password = SecurityUtil.md5(password);
        Account account = accountMapper.checkLogin(username, password);
        if (null != account) {
            User old = this.getUser(username);
            if (Constant.USER_STATE_FREEZ.equals(old.getState())){
                throw new BusinessException("账号已冻结，请联系管理员");
            }
            if (Constant.USER_STATE_DELETE.equals(old.getState())){
                throw new BusinessException("账号已删除，请联系管理员");
            }
            User user = new User();
            user.setId(account.getId());
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(ipAddr);
            userMapper.update(user);
            return userMapper.get(account.getId(), null);
        }
        return null;
    }

    @WriteLog(action = WriteLog.Action.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @CachePut(key = "#result.id", unless = "null == #result")
    @Override
    public User register(@NonNull String username, @NonNull String password) {
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
            Boolean insert1 = userMapper.insert(user);
            if (insert1) {
                // 更新缓存
                return userMapper.get(id, null);
            }
        }
        return null;
    }

    @Override
    public User getUser(@NonNull String username) {
        // TODO 待优化
        Object o = redisUtil.get("light_blog:user::" + username);
        if (null != o) {
            //旧缓存
            User old = (User) o;
            Long id = old.getId();
            // 用id才拿的到最新缓存
            return (User) redisUtil.get("light_blog:user::" + id);
        }
        // 只会执行一次
        User user = userMapper.get(null, username);
        redisUtil.push("light_blog:user::" + username, user);
        return user;
    }

    @Cacheable(key = "#id", unless = "null == #result")
    @Override
    public User getUser(@NonNull Long id) {
        return userMapper.get(id, null);
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @CachePut(key = "#user.id", unless = "null  == #result")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public User updateUser(@NonNull User user) {
        Boolean update = userMapper.update(user);
        if (update) {
            return this.getUser(user.getId());
        }
        return null;
    }

    @WriteLog(action = WriteLog.Action.DELETE)
    @CacheEvict(key = "#id")
    @Override
    public Boolean delete(Long id) {
        return userMapper.delete(id) && accountMapper.delete(id, null);
    }

    @Override
    public PageInfo<User> searchUser(String keyword, Integer state) {
        return searchUser(keyword, state, null, null);
    }

    @OnChange
    @Cacheable(unless = "#result==null || 0 == #result.total")
    @Override
    public PageInfo<User> searchUser(String keyword, Integer state, Integer page, Integer pageSize) {
        if (null != page && null != pageSize) {
            PageHelper.startPage(page, pageSize, true);
        }
        List<User> users = userMapper.search(keyword, state);
        return PageInfo.of(users);
    }
}
