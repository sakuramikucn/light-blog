package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.annotation.*;
import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.IdGenerator;
import cn.sakuramiku.lightblog.entity.Right;
import cn.sakuramiku.lightblog.exception.BusinessException;
import cn.sakuramiku.lightblog.mapper.RightMapper;
import cn.sakuramiku.lightblog.model.BatchInsertParam;
import cn.sakuramiku.lightblog.service.RightService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限服务
 *
 * @author lyy
 */
@LogConfig(reference = "#result.id",category = "right",name = "权限")
@RedisCacheConfig(cacheName = "light_blog:right")
@Service
public class RightServiceImpl implements RightService {

    @Resource
    private RightMapper rightMapper;

    @WriteLog(action = WriteLog.Action.INSERT)
    @RedisCachePut(key = "#result.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Right saveRight(@NonNull Right right) {
        long id = IdGenerator.nextId();
        right.setId(id);
        right.setCreateTime(LocalDateTime.now());
        Boolean add = rightMapper.add(right);
        if (add){
            return right;
        }
        return null;
    }

    @RedisCache(key = "#id")
    @Override
    public Right getRight(Long id) {
        return rightMapper.get(id);
    }

    @RedisCache(key = "#name")
    @Override
    public Right getRightByName(String name) {
        return rightMapper.getByName(name);
    }

    @WriteLog(action = WriteLog.Action.INSERT)
    @Override
    public Boolean addRight(List<BatchInsertParam> params) {
        return rightMapper.batchInsert(params);
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @RedisCachePut(key = "#result.id")
    @RedisClean(key = {"light_blog:user:*","light_blog:role:*"})
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Right updateRight(@NonNull Right right) {
        Boolean update = rightMapper.update(right);
        if (update){
            return right;
        }
        return null;
    }

    @WriteLog(action = WriteLog.Action.DELETE)
    @RedisCacheDelete(key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean removeRight(Long id) throws BusinessException {
        Long roleCount = rightMapper.roleCount(id);
        if (roleCount>0){
            throw new BusinessException("该权限还有角色在使用哦");
        }
        return rightMapper.delete(id);
    }

    @OnCacheChange
    @RedisCache
    @Override
    public PageInfo<Right> searchRight(@NonNull Long roleId, @Nullable String keyword, @Nullable Integer page, @Nullable Integer pageSize) {
        if (null != page && null != pageSize) {
           PageHelper.startPage(page, pageSize, true);
        }
        List<Right> rights = rightMapper.search(roleId);
        return PageInfo.of(rights);
    }

    @OnCacheChange
    @RedisCache
    @Override
    public PageInfo<Right> findRight(String keyword, Integer page, Integer pageSize) {
        if (null != page && null != pageSize) {
            Page<Object> objects = PageHelper.startPage(page, pageSize, true);
            objects.setOrderBy("modified_time DESC");
        }
        List<Right> rights = rightMapper.find(keyword);
        return PageInfo.of(rights);
    }

    @Override
    public Boolean deleteForRole(Long ref) {
        return rightMapper.remove(ref);
    }

}
