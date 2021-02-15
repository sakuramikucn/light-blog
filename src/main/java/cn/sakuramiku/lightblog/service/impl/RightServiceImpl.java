package cn.sakuramiku.lightblog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.common.util.IdUtil;
import cn.sakuramiku.lightblog.entity.Right;
import cn.sakuramiku.lightblog.mapper.RightMapper;
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
@CacheConfig(cacheNames = "right", keyGenerator = "keyGenerator")
@Service
public class RightServiceImpl implements RightService {

    @Resource
    private RightMapper rightMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveRight(@NonNull Right right) {
        if (ObjectUtil.isNull(right)) {
            return null;
        }
        long id = IdUtil.nextId();
        right.setId(id);
        right.setCreateTime(LocalDateTime.now());
        rightMapper.insert(right);
        return id;
    }

    @CachePut(key = "#right.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateRight(@NonNull Right right) {
        return rightMapper.update(right);
    }

    @CachePut(key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean removeRight(@NonNull Long id) {
        return rightMapper.delete(id);
    }

    @Cacheable(unless = "null == #result || 0 == #result.list.size()")
    @Override
    public PageInfo<Right> getRights(@Nullable String reference, @Nullable Integer page, @Nullable Integer pageSize) {
        if (StrUtil.isEmpty(reference)) {
            return new PageInfo<>();
        }
        if (null != page && null != pageSize) {
            PageHelper.startPage(page, pageSize, true);
        }
        List<Right> rights = rightMapper.search(reference);
        return PageInfo.of(rights);
    }
}
