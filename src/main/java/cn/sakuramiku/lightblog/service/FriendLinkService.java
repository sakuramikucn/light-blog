package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.FriendLink;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 友链服务接口
 *
 * @author lyy
 */
public interface FriendLinkService {

    /**
     * 创建友链
     *
     * @param link 友链
     * @return 友链ID
     */
    FriendLink saveLink(@NonNull FriendLink link);

    /**
     * 修改友链
     *
     * @param link 参数
     * @return 是否成功
     */
    FriendLink updateLink(@NonNull FriendLink link);


    /**
     * 移除友链
     *
     * @param id 友链ID
     * @return 是否成功
     */
    Boolean removeLink(@NonNull Long id);

    /**
     * 获取友链
     *
     * @param id 友链ID
     * @return 是否成功
     */
    FriendLink getLink(@NonNull Long id);

    /**
     * 搜索友链
     *
     * @param keyword  名称关键字
     * @param page     第几页
     * @param pageSize 每页项数
     * @return 友链列表
     */
    PageInfo<FriendLink> searchLink(@Nullable String keyword, @Nullable Integer page, @Nullable Integer pageSize);
}
