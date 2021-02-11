package cn.sakuramiku.lightblog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.sakuramiku.lightblog.common.util.IdUtil;
import cn.sakuramiku.lightblog.entity.Role;
import cn.sakuramiku.lightblog.mapper.RoleMapper;
import cn.sakuramiku.lightblog.service.RoleService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
@CacheConfig(cacheNames = "Role",keyGenerator = "keyGenerator")
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Role saveRole(Role role) {
        if (ObjectUtil.isNull(role)) {
            return null;
        }
        role.setId(IdUtil.nextId());
        role.setCreateTime(LocalDateTime.now());
        roleMapper.insertSelective(role);
        return role;
    }

    @CachePut(key = "#role.getId()")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateRole(Role role) {
        return roleMapper.updateByPrimaryKeySelective(role);
    }

    @CachePut(key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeRole(Long id) {
        return roleMapper.deleteByPrimaryKey(id);
    }

    @Cacheable(key = "#id")
    @Override
    public Role getRole(Long id) {
        return roleMapper.selectByPrimaryKey(id);
    }

    @Cacheable
    @Override
    public List<Role> getRoles(Long userId) {
        return roleMapper.selectBySelective(String.valueOf(userId), null);
    }

    @Cacheable
    @Override
    public List<Role> searchRole(Long userId, String keyword) {
        return roleMapper.selectBySelective(String.valueOf(userId), keyword);
    }
}
