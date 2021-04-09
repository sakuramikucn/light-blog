package cn.sakuramiku.lightblog.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.sakuramiku.lightblog.annotation.OnChange;
import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.IdGenerator;
import cn.sakuramiku.lightblog.entity.Right;
import cn.sakuramiku.lightblog.entity.Role;
import cn.sakuramiku.lightblog.mapper.RoleMapper;
import cn.sakuramiku.lightblog.model.BatchInsertParam;
import cn.sakuramiku.lightblog.service.RightService;
import cn.sakuramiku.lightblog.service.RoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务
 *
 * @author lyy
 */
@LogConfig(reference = "role",name = "角色")
@CacheConfig(cacheNames = "light_blog:role", keyGenerator = "simpleKeyGenerator")
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RightService rightService;

    @WriteLog(action = WriteLog.Action.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveRole(@NonNull Role role) {
        long id = IdGenerator.nextId();
        role.setId(id);
        LocalDateTime now = LocalDateTime.now();
        role.setCreateTime(now);
        Boolean succ = roleMapper.add(role);
        List<Right> rights = role.getRights();
        // 如果权限列表不为空，添加权限
        if (succ && !CollectionUtil.isEmpty(rights)) {
            List<BatchInsertParam> params = rights.parallelStream().map(right -> BatchInsertParam.valueOf(id, right.getId())).collect(Collectors.toList());
            rightService.addRight(params);
        }
        return id;
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @CachePut(key = "#role.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateRole(@NonNull Role role) {
        return roleMapper.update(role);
    }

    @WriteLog(action = WriteLog.Action.DELETE)
    @CachePut(key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean removeRole(Long id, String ref) {
        // 删除关联的权限
        rightService.removeRight(null);
        return roleMapper.delete(id, ref);
    }

    @Cacheable(key = "#id", unless = "null == #result")
    @Override
    public Role getRole(@NonNull Long id) {
        return roleMapper.get(id);
    }

    @Override
    public PageInfo<Role> getRoles(@NonNull Long userId, @Nullable Integer page, @Nullable Integer pageSize) {
        return searchRole(userId, null, page, pageSize);
    }

    @OnChange
    @Cacheable(unless = "null == #result || 0 == #result.size()")
    @Override
    public PageInfo<Role> searchRole(@Nullable Long userId, @Nullable String keyword, @Nullable Integer page, @Nullable Integer pageSize) {
        if (null != page && null != pageSize) {
            PageHelper.startPage(page, pageSize, true);
        }
        if (null == userId) {
            List<Role> roles = roleMapper.find(keyword);
            return PageInfo.of(roles);
        }
        List<Role> roles = roleMapper.search(userId, keyword);
        return PageInfo.of(roles);
    }
}
