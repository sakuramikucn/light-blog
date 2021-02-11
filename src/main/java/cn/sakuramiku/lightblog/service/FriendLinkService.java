package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.FriendLink;

/**
 * 友链服务接口
 * @author lyy
 */
public interface FriendLinkService {

    FriendLink saveLink(FriendLink link);

    FriendLink updateLink(FriendLink link);

    boolean removeLink(String id);

}
