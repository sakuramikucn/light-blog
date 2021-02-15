package cn.sakuramiku.lightblog.mapper;

import cn.sakuramiku.lightblog.entity.Category;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 分类SQL映射
 *
 * @author LYY
 */
public interface CategoryMapper {

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 是否成功
     */
    Boolean delete(@NonNull Long id);

    /**
     * 添加分类
     *
     * @param record 分类
     * @return 是否成功
     */
    Boolean insert(@NonNull Category record);

    /**
     * 获取分类
     *
     * @param id 分类ID
     * @return 分类
     */
    Category get(@NonNull Long id);

    /**
     * 搜索分类
     *
     * @param keyword 名称关键字
     * @return 分类集合
     */
    List<Category> search(@Nullable String keyword);

    /**
     * 修改分类
     *
     * @param id   分类ID
     * @param name 分类名称
     * @return 是否成功
     */
    Boolean update(@NonNull @Param("id") Long id, @NonNull @Param("name") String name);
}