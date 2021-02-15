package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Tag;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 标签服务接口
 *
 * @author lyy
 */
public interface TagService {

    /**
     * 创建标签
     *
     * @param tag 标签
     * @return 标签ID
     */
    Long saveTag(@NonNull Tag tag);

    /**
     * 修改标签
     *
     * @param tag 参数
     * @return 是否成功
     */
    Boolean updateTag(@NonNull Tag tag);

    /**
     * 删除标签
     *
     * @param id 标签ID
     * @return 是否成功
     */
    Boolean removeTag(@NonNull Long id);

    /**
     * 获取标签
     *
     * @param id 标签ID
     * @return 标签
     */
    Tag getTag(@NonNull Long id);

    /**
     * 搜索标签
     *
     * @param ref      引用，一般为文章ID
     * @param keyword  名称关键字
     * @param page     第几页
     * @param pageSize 每页项数
     * @return 标签列表
     */
    PageInfo<Tag> search(@Nullable String ref, @Nullable String keyword, @Nullable Integer page, @Nullable Integer pageSize);

    /**
     * 搜索标签
     *
     * @param ref     引用，一般为文章ID
     * @param keyword 名称关键字
     * @return 标签列表
     */
    PageInfo<Tag> search(@Nullable String ref, @Nullable String keyword);
}
