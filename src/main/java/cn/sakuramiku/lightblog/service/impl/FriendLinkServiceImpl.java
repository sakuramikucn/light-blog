package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.annotation.OnChange;
import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.IdGenerator;
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
@LogConfig(reference = "friend_link",name = "友情链接")
@CacheConfig(cacheNames = "light_blog:friend_link", keyGenerator = "simpleKeyGenerator")
@Service
public class FriendLinkServiceImpl implements FriendLinkService {

    @Resource
    private FriendLinkMapper linkMapper;

    @WriteLog(action = WriteLog.Action.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveLink(@NonNull FriendLink link) {
        long id = IdGenerator.nextId();
        link.setId(id);
        link.setCreateTime(LocalDateTime.now());
        linkMapper.insert(link);
        return id;
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @CachePut(key = "#link.id")
    @Override
    public Boolean updateLink(@NonNull FriendLink link) {
        return linkMapper.update(link);
    }

    @WriteLog(action = WriteLog.Action.DELETE)
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

    @OnChange
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
