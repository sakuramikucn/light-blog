package cn.sakuramiku.lightblog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.common.util.IdUtil;
import cn.sakuramiku.lightblog.entity.Right;
import cn.sakuramiku.lightblog.mapper.RightMapper;
import cn.sakuramiku.lightblog.service.RightService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 权限服务
 *
 * @author lyy
 */
@CacheConfig(cacheNames = "Right",keyGenerator = "keyGenerator")
@Service
public class RightServiceImpl implements RightService {

    @Resource
    private RightMapper rightMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Right saveRight(Right right) {
        if (ObjectUtil.isNull(right)) {
            return null;
        }
        right.setId(IdUtil.nextId());
        right.setCreateTime(LocalDateTime.now());
        rightMapper.insertSelective(right);
        return right;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateRight(Right right) {
        return rightMapper.updateByPrimaryKeySelective(right);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeRight(Long id) {
        return rightMapper.deleteByPrimaryKey(id);
    }


    @Cacheable
    @Override
    public List<Right> getRights(String reference) {
        if (StrUtil.isEmpty(reference)) {
            return Collections.emptyList();
        }
        return rightMapper.selectByRef(reference);
    }
}
