package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Tag;
import cn.sakuramiku.lightblog.exception.BusinessException;
import cn.sakuramiku.lightblog.model.BatchInsertParam;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 标签服务接口
 *
 * @author lyy
 */
public interface TagService {

    /**
     * 创建标签
     *
     * @param name 标签名称
     * @return 标签ID
     */
    Tag saveTag(@NonNull String name);

    /**
     * 修改标签
     *
     * @param id   标签ID
     * @param name 标签名称
     * @return 是否成功
     */
    Tag updateTag(@NonNull Long id, @NonNull String name);

    /**
     * 删除标签
     *
     * @param id 标签ID
     * @return 是否成功
     */
    Boolean removeTag(@NonNull Long id) throws BusinessException;

    /**
     * 获取标签
     *
     * @param id 标签ID
     * @return 标签
     */
    Tag getTag(@NonNull Long id);

    Tag getTagByName(String name);

    /**
     * 搜索标签
     *
     * @param articleId 文章ID
     * @param keyword   名称关键字
     * @param page      第几页
     * @param pageSize  每页项数
     * @return 标签列表
     */
    PageInfo<Tag> search(@Nullable Long articleId, @Nullable String keyword, @Nullable Integer page, @Nullable Integer pageSize);

    /**
     * 搜索标签
     *
     * @param articleId 文章ID
     * @param keyword   名称关键字
     * @return 标签列表
     */
    PageInfo<Tag> search(@Nullable Long articleId, @Nullable String keyword);

    /**
     * 批量往中间表插入记录
     *
     * @param params
     * @return
     */
    Boolean batchInsert(List<BatchInsertParam> params);

    Boolean deleteForArticle(Long articleId);
}
