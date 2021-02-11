package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 *
 * @author lyy
 */
public interface CategoryService {

    /**
     * 创建分类
     *
     * @param category
     * @return
     */
    Category saveCategory(Category category);

    /**
     * 修改分类
     *
     * @param name
     * @return
     */
    Category updateCategory(String name);

    /**
     * 删除分类
     *
     * @param id
     * @return
     */
    boolean removeCategory(String id);

    /**
     * 获取所有分类
     *
     * @return
     */
    List<Category> listCategories();
}
