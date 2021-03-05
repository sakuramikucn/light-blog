package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Right;
import cn.sakuramiku.lightblog.model.BatchInsertParam;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 权限服务接口
 *
 * @author lyy
 */
public interface RightService {

    /**
     * 添加权限
     *
     * @param right 权限
     * @return 权限ID
     */
    Long saveRight(@NonNull Right right);

    /**
     * 为角色添加权限(批量)
     *
     * @param params 参数
     * @return
     */
    Boolean addRight(List<BatchInsertParam> params);

    /**
     * 修改权限
     *
     * @param right 参数
     * @return 是否成功
     */
    Boolean updateRight(@NonNull Right right);

    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return 是否成功
     */
    Boolean removeRight(Long id);


    /**
     * 搜索角色权限
     *
     * @param roleId   角色ID
     * @param page     页
     * @param pageSize 项数
     * @return 权限列表
     */
    PageInfo<Right> searchRight(@NonNull Long roleId, @Nullable String keyword, @Nullable Integer page, @Nullable Integer pageSize);

    /**
     * 搜索权限
     *
     * @param page     页
     * @param pageSize 项数
     * @return 权限列表
     */
    PageInfo<Right> findRight(@Nullable String keyword, @Nullable Integer page, @Nullable Integer pageSize);
}
