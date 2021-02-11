package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Tag;

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
     * @param tag
     * @return
     */
    Tag saveTag(Tag tag);

    /**
     * 修改标签
     *
     * @param tag
     * @return
     */
    Tag updateTag(Tag tag);

    /**
     * 删除标签
     *
     * @param id
     * @return
     */
    boolean removeTag(String id);

    /**
     * 获取标签
     *
     * @param id
     * @return
     */
    Tag getTag(String id);

    /**
     * 所有标签
     *
     * @return
     */
    List<Tag> listTags();

    /**
     * 获取reference关联的标签
     *
     * @param reference
     * @return
     */
    List<Tag> getTags(String reference);
}
