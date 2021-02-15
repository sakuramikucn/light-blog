package cn.sakuramiku.lightblog.mapper;

import cn.sakuramiku.lightblog.entity.FriendLink;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 友链SLQ映射
 *
 * @author LYY
 */
public interface FriendLinkMapper {

    /**
     * 删除友链
     *
     * @param id 友链ID
     * @return 是否成功
     */
    Boolean delete(@NonNull Long id);

    /**
     * 添加友链
     *
     * @param record 参数
     * @return 是否成功
     */
    Boolean insert(@NonNull FriendLink record);

    /**
     * 获取友链
     *
     * @param id 友链ID
     * @return 友链
     */
    FriendLink get(@NonNull Long id);

    /**
     * 搜索友链
     *
     * @param keyword 名称关键字
     * @return 友链列表
     */
    List<FriendLink> search(@Nullable String keyword);

    /**
     * 修改友链
     *
     * @param record 参数
     * @return 是否成功
     */
    Boolean update(@NonNull FriendLink record);

}