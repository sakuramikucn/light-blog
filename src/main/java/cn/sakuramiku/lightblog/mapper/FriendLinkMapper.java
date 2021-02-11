package cn.sakuramiku.lightblog.mapper;

import cn.sakuramiku.lightblog.entity.FriendLink;

public interface FriendLinkMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FriendLink record);

    int insertSelective(FriendLink record);

    FriendLink selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FriendLink record);

    int updateByPrimaryKey(FriendLink record);
}