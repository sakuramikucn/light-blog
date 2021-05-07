package cn.sakuramiku.lightblog.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.sakuramiku.lightblog.annotation.*;
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
import cn.sakuramiku.lightblog.model.BatchInsertParam;
import cn.sakuramiku.lightblog.service.RoleService;
import cn.sakuramiku.lightblog.service.UserService;
import cn.sakuramiku.lightblog.util.Constant;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务
 *
 * @author lyy
 */
@LogConfig(reference = "#result.id", category = "user", name = "用户")
@RedisCacheConfig(cacheName = "light_blog:user")
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private RoleService roleService;

    @WriteLog(action = WriteLog.Action.UPDATE)
    @RedisCachePut(key = "#result.id")
    @Override
    public User login(@NonNull String username, @NonNull String password, String ipAddr) throws BusinessException {
        Account account = accountMapper.checkLogin(username, password);
        if (null != account) {
            User old = userMapper.get(null, username);
            if (Constant.USER_STATE_FREEZ.equals(old.getState())) {
                throw new BusinessException("账号已冻结，请联系管理员");
            }
            if (Constant.USER_STATE_DELETE.equals(old.getState())) {
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
    @RedisCachePut(key = "#result.id")
    @Override
    public User register(@NonNull String username, @NonNull String password) {
        long id = IdGenerator.nextId();
        LocalDateTime now = LocalDateTime.now();
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
                return userMapper.get(account.getId(), null);
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
            User user = (User) redisUtil.get("light_blog:user::" + id);
            if (null == user) {
                return old;
            } else {
                return user;
            }
        }
        // 只会执行一次
        User user = userMapper.get(null, username);
        redisUtil.push("light_blog:user::" + username, user);
        return user;
    }

    @RedisCache(key = "#id")
    @Override
    public User getUser(@NonNull Long id) {
        return userMapper.get(id, null);
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @RedisCachePut(key = "#user.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public User updateUser(@NonNull User user) throws BusinessException {
        Boolean update = userMapper.update(user);
        if (update) {
            roleService.removeRoleForUser(user.getId());
            List<BatchInsertParam> insertParams = user.getRoles().parallelStream()
                    .map(role -> BatchInsertParam.valueOf(user.getId(), role.getId())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(insertParams)) {
                Boolean aBoolean = roleService.addRoles(insertParams);
                if (aBoolean) {
                    return userMapper.get(user.getId(), null);
                } else {
                    throw new BusinessException("更新失败");
                }
            } else {
                return userMapper.get(user.getId(), null);
            }
        }
        return null;
    }

    @WriteLog(action = WriteLog.Action.DELETE)
    @RedisCacheDelete(key = "#id")
    @Override
    public Boolean delete(Long id) {
        return userMapper.delete(id) && accountMapper.delete(id, null);
    }

    @Override
    public PageInfo<User> searchUser(String keyword, Integer state) {
        return searchUser(keyword, state, null, null);
    }

    @OnCacheChange
    @RedisCache
    @Override
    public PageInfo<User> searchUser(String keyword, Integer state, Integer page, Integer pageSize) {
        if (null != page && null != pageSize) {
            Page<Object> objects = PageHelper.startPage(page, pageSize, true);
            objects.setOrderBy("`user`.modified_time DESC");
        }
        List<User> users = userMapper.search(keyword, state,null,null);
        return new PageInfo<>(users);
    }

    @Transactional(rollbackFor = Exception.class)
    @RedisCleanQuery
    @WriteLog(action = WriteLog.Action.DELETE, result = true)
    @Override
    public Integer deleteForRecycle(Integer status, Date start) {
        List<User> list = userMapper.search(null, Constant.USER_STATE_DELETE, null, null);
        List<Long> ids = list.parallelStream().filter(user -> start.getTime() >= user.getModifiedTime()
                .toInstant(ZoneOffset.of("+8")).toEpochMilli()).map(User::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)){
            return 0;
        }
        for(Long id:ids){
            // 删account
            accountMapper.delete(id,null);
            // 删user
            delete(id);
        }

        return  ids.size();
    }

    @RedisCachePut(key = "id")
    @Override
    public Boolean restoreForRecycle(Long id) {
        User user = new User();
        user.setId(id);
        user.setState(Constant.USER_STATE_NORMAL);
        return userMapper.update(user);
    }
}
