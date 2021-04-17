package cn.sakuramiku.lightblog.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.sakuramiku.lightblog.annotation.*;
import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.IdGenerator;
import cn.sakuramiku.lightblog.entity.Right;
import cn.sakuramiku.lightblog.entity.Role;
import cn.sakuramiku.lightblog.exception.BusinessException;
import cn.sakuramiku.lightblog.mapper.RoleMapper;
import cn.sakuramiku.lightblog.model.BatchInsertParam;
import cn.sakuramiku.lightblog.service.RightService;
import cn.sakuramiku.lightblog.service.RoleService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.context.annotation.Lazy;
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
@LogConfig(reference = "role", name = "角色")
@RedisCacheConfig(cacheName = "light_blog:role")
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RightService rightService;
    @Lazy
    @Resource
    private RoleService roleService;

    @WriteLog(action = WriteLog.Action.INSERT)
    @RedisCachePut(key = "#result.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Role saveRole(@NonNull Role role) {
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
        if (succ) {
            return roleService.getRole(id);
        }
        return null;
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @RedisCachePut(key = "#result.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Role updateRole(@NonNull Role role) throws BusinessException {
        Boolean update = roleMapper.update(role);
        if (update) {
            // 删除权限
            rightService.deleteForRole(role.getId());
            List<BatchInsertParam> insertParams = role.getRights().parallelStream().
                    map(right -> BatchInsertParam.valueOf(role.getId(), right.getId())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(insertParams)){
                Boolean aBoolean = rightService.addRight(insertParams);
                if (!aBoolean){
                    throw new BusinessException("添加失败");
                }
            }
            return roleService.getRole(role.getId());
        }
        return null;
    }

    @WriteLog(action = WriteLog.Action.DELETE)
    @RedisCacheDelete(key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean removeRole(Long id) throws BusinessException {
        // 1. 看看有没有引用
        Long userCount = roleMapper.getUserCount(id);
        if (userCount > 0) {
            throw new BusinessException("当前角色还有用户在用哦");
        } else {
            // 2.删除关联的权限
            rightService.deleteForRole(id);
            // 3.删除角色
            return roleMapper.delete(id);
        }
    }

    @Override
    public Boolean removeRoleForUser(Long userId) {
        return roleMapper.deleteForUser(userId);
    }


    @RedisCache(key = "#id")
    @Override
    public Role getRole(@NonNull Long id) {
        return roleMapper.get(id);
    }

    @RedisCache(key = "#name")
    @Override
    public Role getRoleByName(String name) {

        return null;
    }

    @OnCacheChange
    @RedisCache
    @Override
    public PageInfo<Role> getRoles(@NonNull Long userId, @Nullable Integer page, @Nullable Integer pageSize) {
        return searchRole(userId, null, page, pageSize);
    }

    @OnCacheChange
    @RedisCache
    @Override
    public PageInfo<Role> searchRole(@Nullable Long userId, @Nullable String keyword, @Nullable Integer page, @Nullable Integer pageSize) {
        if (null != page && null != pageSize) {
            Page<Object> objects = PageHelper.startPage(page, pageSize, true);
            objects.setOrderBy("`role`.modified_time DESC");
        }
        if (null == userId) {
            List<Role> roles = roleMapper.find(keyword);
            return PageInfo.of(roles);
        }
        List<Role> roles = roleMapper.search(userId, keyword);
        return PageInfo.of(roles);
    }

    @Override
    public Boolean addRoles(List<BatchInsertParam> params) {
        return roleMapper.batchInsert(params);
    }
}
