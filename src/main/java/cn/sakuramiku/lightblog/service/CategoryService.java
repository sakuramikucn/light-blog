package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Category;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

/**
 * 分类服务接口
 *
 * @author lyy
 */
public interface CategoryService {

    /**
     * 创建分类
     *
     * @param name 名称
     * @return 分类ID
     */
    Category saveCategory(String name);

    /**
     * 获取分类
     * @param id
     * @return
     */
    Category getCategory(Long id);

    /**
     * 修改分类
     *
     * @param id   分类ID
     * @param name 名称
     * @return 是否成功
     */
    Category updateCategory(Long id, String name);

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 是否成功
     */
    Boolean removeCategory(Long id);

    /**
     * 搜索分类
     *
     * @param keyword  名称关键字
     * @param begin    开始时间
     * @param end      截止时间
     * @param page     第几页
     * @param pageSize 每页多少项
     * @return 分类列表
     */
    PageInfo<Category> search(@Nullable String keyword, @Nullable LocalDateTime begin, @Nullable LocalDateTime end,
                              @Nullable Integer page, @Nullable Integer pageSize);
}
