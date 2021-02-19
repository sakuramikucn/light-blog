package cn.sakuramiku.lightblog.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.common.util.IdUtil;
import cn.sakuramiku.lightblog.entity.Right;
import cn.sakuramiku.lightblog.entity.Role;
import cn.sakuramiku.lightblog.mapper.RoleMapper;
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

/**
 * 角色服务
 *
 * @author lyy
 */
@CacheConfig(cacheNames = "light_blog:role", keyGenerator = "simpleKeyGenerator")
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RightService rightService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveRole(@NonNull Role role) {
        long id = IdUtil.nextId();
        role.setId(id);
        LocalDateTime now = LocalDateTime.now();
        role.setCreateTime(now);
        roleMapper.insert(role);
        List<Right> rights = role.getRights();
        // 如果权限列表不为空，添加权限
        if (!CollectionUtil.isEmpty(rights)) {
            for (Right right : rights) {
                if (StrUtil.isBlank(right.getPattern())) {
                    continue;
                }
                long rId = IdUtil.nextId();
                right.setId(rId);
                right.setCreateTime(now);
                right.setReference(String.valueOf(id));
                rightService.saveRight(right);
            }
        }
        return id;
    }

    @CachePut(key = "#role.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateRole(@NonNull Role role) {
        return roleMapper.update(role);
    }

    @CachePut(key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean removeRole(Long id, String ref) {
        // 删除关联的权限
        rightService.removeRight(null, String.valueOf(id));
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
