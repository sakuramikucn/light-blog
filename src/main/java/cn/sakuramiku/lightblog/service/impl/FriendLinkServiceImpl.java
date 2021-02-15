package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.common.util.IdUtil;
import cn.sakuramiku.lightblog.entity.FriendLink;
import cn.sakuramiku.lightblog.mapper.FriendLinkMapper;
import cn.sakuramiku.lightblog.service.FriendLinkService;
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
 * @author lyy
 */
@CacheConfig(cacheNames = "light_blog:friend_link", keyGenerator = "simpleKeyGenerator")
@Service
public class FriendLinkServiceImpl implements FriendLinkService {

    @Resource
    private FriendLinkMapper linkMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveLink(@NonNull FriendLink link) {
        long id = IdUtil.nextId();
        link.setId(id);
        link.setCreateTime(LocalDateTime.now());
        linkMapper.insert(link);
        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    @CachePut(key = "#link.id")
    @Override
    public Boolean updateLink(@NonNull FriendLink link) {
        return linkMapper.update(link);
    }

    @Transactional(rollbackFor = Exception.class)
    @CachePut(key = "#id")
    @Override
    public Boolean removeLink(@NonNull Long id) {
        return linkMapper.delete(id);
    }

    @Cacheable(key = "#id", unless = "null == #result")
    @Override
    public FriendLink getLink(@NonNull Long id) {
        return linkMapper.get(id);
    }

    @Cacheable(unless = "null == #result || 0 == #result.total")
    @Override
    public PageInfo<FriendLink> searchLink(String keyword, Integer page, Integer pageSize) {
        if (null != page && null != pageSize) {
            PageHelper.startPage(page, pageSize, true);
        }
        List<FriendLink> links = linkMapper.search(keyword);
        return PageInfo.of(links);
    }
}
