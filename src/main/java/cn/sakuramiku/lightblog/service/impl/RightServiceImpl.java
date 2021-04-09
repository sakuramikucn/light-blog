package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.annotation.OnChange;
import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.IdGenerator;
import cn.sakuramiku.lightblog.entity.Right;
import cn.sakuramiku.lightblog.mapper.RightMapper;
import cn.sakuramiku.lightblog.model.BatchInsertParam;
import cn.sakuramiku.lightblog.service.RightService;
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
 * 权限服务
 *
 * @author lyy
 */
@LogConfig(reference = "right",name = "权限")
@CacheConfig(cacheNames = "light_blog:right", keyGenerator = "simpleKeyGenerator")
@Service
public class RightServiceImpl implements RightService {

    @Resource
    private RightMapper rightMapper;

    @WriteLog(action = WriteLog.Action.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveRight(@NonNull Right right) {
        long id = IdGenerator.nextId();
        right.setId(id);
        right.setCreateTime(LocalDateTime.now());
        rightMapper.add(right);
        return id;
    }

    @WriteLog(action = WriteLog.Action.INSERT)
    @Override
    public Boolean addRight(List<BatchInsertParam> params) {
        return rightMapper.batchInsert(params);
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @CachePut(key = "#right.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateRight(@NonNull Right right) {
        return rightMapper.update(right);
    }

    @WriteLog(action = WriteLog.Action.DELETE)
    @CachePut(key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean removeRight(Long id) {
        return rightMapper.delete(id);
    }

    @OnChange
    @Cacheable(unless = "null == #result || 0 == #result.total")
    @Override
    public PageInfo<Right> searchRight(@NonNull Long roleId, @Nullable String keyword, @Nullable Integer page, @Nullable Integer pageSize) {
        if (null != page && null != pageSize) {
            PageHelper.startPage(page, pageSize, true);
        }
        List<Right> rights = rightMapper.search(roleId);
        return PageInfo.of(rights);
    }

    @OnChange
    @Cacheable(unless = "null == #result || 0 == #result.total")
    @Override
    public PageInfo<Right> findRight(String keyword, Integer page, Integer pageSize) {
        if (null != page && null != pageSize) {
            PageHelper.startPage(page, pageSize, true);
        }
        List<Right> rights = rightMapper.find();
        return PageInfo.of(rights);
    }
}
