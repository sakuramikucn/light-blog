package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Right;

import java.util.List;

/**
 * 权限服务接口
 * @author lyy
 */
public interface RightService {

    /**
     * 添加权限
     * @param right
     * @return
     */
    Right saveRight(Right right);

    /**
     * 修改权限
     * @param right
     * @return
     */
    boolean updateRight(Right right);

    /**
     * 删除一个权限
     * @param id
     * @return
     */
    boolean removeRight(Long id);


    /**
     * 获取reference关联的权限
     * @param reference
     * @return
     */
    List<Right> getRights(String reference);
}
