package cn.sakuramiku.lightblog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.sakuramiku.lightblog.common.util.IdUtil;
import cn.sakuramiku.lightblog.entity.Role;
import cn.sakuramiku.lightblog.mapper.RoleMapper;
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

/**
 * 角色服务
 *
 * @author lyy
 */
@CacheConfig(cacheNames = "role", keyGenerator = "keyGenerator")
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveRole(@NonNull Role role) {
        if (ObjectUtil.isNull(role)) {
            return null;
        }
        long id = IdUtil.nextId();
        role.setId(id);
        role.setCreateTime(LocalDateTime.now());
        roleMapper.insert(role);
        return id;
    }

    @CachePut(key = "#role.getId()")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateRole(@NonNull Role role) {
        return roleMapper.update(role);
    }

    @CachePut(key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean removeRole(@NonNull Long id) {
        return roleMapper.delete(id);
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

    @Cacheable(unless = "null == #result || 0 == #result.size()")
    @Override
    public PageInfo<Role> searchRole(@Nullable Long userId, @Nullable String keyword, @Nullable Integer page, @Nullable Integer pageSize) {
        if (null != page && null != pageSize) {
            PageHelper.startPage(page, pageSize, true);
        }
        List<Role> roles = roleMapper.search(String.valueOf(userId), keyword);
        return PageInfo.of(roles);
    }
}
