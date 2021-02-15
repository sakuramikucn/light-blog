package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.common.util.IdUtil;
import cn.sakuramiku.lightblog.entity.Tag;
import cn.sakuramiku.lightblog.mapper.TagMapper;
import cn.sakuramiku.lightblog.service.TagService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 标签服务实现
 *
 * @author lyy
 */
@CacheConfig(cacheNames = "light_blog:tag", keyGenerator = "simpleKeyGenerator")
@Service
public class TagServiceImpl implements TagService {

    @Resource
    private TagMapper tagMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveTag(@NonNull Tag tag) {
        long id = IdUtil.nextId();
        tag.setId(id);
        tag.setCreateTime(LocalDateTime.now());
        tagMapper.insert(tag);
        return id;
    }

    @CachePut(key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateTag(@NonNull Long id, @NonNull String name) {
        return tagMapper.update(id, name);
    }

    @CachePut(key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean removeTag(@NonNull Long id) {
        return tagMapper.delete(id);
    }

    @Cacheable(key = "#id", unless = "null == #result")
    @Override
    public Tag getTag(@NonNull Long id) {
        return tagMapper.get(id);
    }

    @Cacheable(unless = "null == #result || 0 == #result.total")
    @Override
    public PageInfo<Tag> search(String ref, String keyword, Integer page, Integer pageSize) {
        if (null != page && null != pageSize) {
            PageHelper.startPage(page, pageSize, true);
        }
        List<Tag> tags = tagMapper.search(ref, keyword, null, null);
        return PageInfo.of(tags);
    }

    @Override
    public PageInfo<Tag> search(String ref, String keyword) {
        return search(ref, keyword, null, null);
    }
}
