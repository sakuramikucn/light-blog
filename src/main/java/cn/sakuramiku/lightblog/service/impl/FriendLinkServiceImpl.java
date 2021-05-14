package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.annotation.*;
import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.IdGenerator;
import cn.sakuramiku.lightblog.entity.FriendLink;
import cn.sakuramiku.lightblog.mapper.FriendLinkMapper;
import cn.sakuramiku.lightblog.service.FriendLinkService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lyy
 */
@LogConfig(reference = "#result.id",category = "friend_link",name = "友情链接")
@RedisCacheConfig(cacheName = "light_blog:friend_link")
@Service
public class FriendLinkServiceImpl implements FriendLinkService {

    @Resource
    private FriendLinkMapper linkMapper;

    @WriteLog(action = WriteLog.Action.INSERT)
    @RedisCachePut(key = "#result.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public FriendLink saveLink(@NonNull FriendLink link) {
        long id = IdGenerator.nextId();
        link.setId(id);
        link.setCreateTime(LocalDateTime.now());
        Boolean insert = linkMapper.insert(link);
        if (insert){
            return this.getLink(id);
        }
        return null;
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @RedisCachePut(key = "#result.id")
    @Override
    public FriendLink updateLink(@NonNull FriendLink link) {
        Boolean update = linkMapper.update(link);
        if (update){
            return this.getLink(link.getId());
        }
        return null;
    }

    @WriteLog(action = WriteLog.Action.DELETE)
    @Transactional(rollbackFor = Exception.class)
    @RedisCacheDelete(key = "#id")
    @Override
    public Boolean removeLink(@NonNull Long id) {
        return linkMapper.delete(id);
    }

    @RedisCache(key = "#id")
    @Override
    public FriendLink getLink(@NonNull Long id) {
        return linkMapper.get(id);
    }

    @OnCacheChange
    @RedisCache
    @Override
    public PageInfo<FriendLink> searchLink(String keyword, Integer page, Integer pageSize) {
        if (null != page && null != pageSize) {
            Page<Object> objects = PageHelper.startPage(page, pageSize, true);
//            objects.setOrderBy("modified_time DESC");
        }
        List<FriendLink> links = linkMapper.search(keyword);
        return PageInfo.of(links);
    }
}
