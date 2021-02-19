package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Right;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

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
     * 修改权限
     *
     * @param right 参数
     * @return 是否成功
     */
    Boolean updateRight(@NonNull Right right);

    /**
     * 删除权限
     *
     * @param id  权限ID
     * @param ref 角色ID
     * @return 是否成功
     */
    Boolean removeRight(Long id, String ref);


    /**
     * 搜索权限
     *
     * @param reference 引用，一般为角色ID
     * @param page      页
     * @param pageSize  项数
     * @return 权限列表
     */
    PageInfo<Right> searchRight(@Nullable String reference, @Nullable Integer page, @Nullable Integer pageSize);
}
