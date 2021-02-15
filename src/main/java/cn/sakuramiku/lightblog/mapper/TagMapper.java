package cn.sakuramiku.lightblog.mapper;


import cn.sakuramiku.lightblog.entity.Tag;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 标签SQL映射
 *
 * @author LYY
 */
public interface TagMapper {

    /**
     * 删除标签
     *
     * @param id
     * @return
     */
    Boolean delete(@NonNull Long id);

    /**
     * 添加标签
     *
     * @param record
     * @return
     */
    Boolean insert(@NonNull Tag record);

    /**
     * 获取标签
     *
     * @param id
     * @return
     */
    Tag get(@NonNull Long id);

    /**
     * 修改标签
     *
     * @param id   标签ID
     * @param name 名称
     * @return
     */
    Boolean update(@NonNull @Param("id") Long id, @NonNull @Param("name") String name);

    /**
     * 搜索标签
     *
     * @param ref     引用，一般为文章ID
     * @param keyword 名称关键字
     * @param begin   开始时间
     * @param end     截止时间
     * @return 标签列表
     */
    List<Tag> search(@Nullable @Param("ref") String ref,
                     @Nullable @Param("keyword") String keyword,
                     @Nullable @Param("begin") LocalDateTime begin,
                     @Nullable @Param("end") LocalDateTime end);
}