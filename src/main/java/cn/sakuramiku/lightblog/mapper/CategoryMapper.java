package cn.sakuramiku.lightblog.mapper;

import cn.sakuramiku.lightblog.entity.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
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

    @Select("select * from category where name = #{name}")
    Category getByName(String name);

    /**
     * 搜索分类
     *
     * @param keyword 名称关键字
     * @param begin   开始时间
     * @param end     截止时间
     * @return 分类集合
     */
    List<Category> search(@Nullable @Param("keyword") String keyword,
                          @Nullable @Param("begin") LocalDateTime begin,
                          @Nullable @Param("end") LocalDateTime end);

    /**
     * 修改分类
     *
     * @param id   分类ID
     * @param name 分类名称
     * @return 是否成功
     */
    Boolean update(@NonNull @Param("id") Long id, @NonNull @Param("name") String name);

}