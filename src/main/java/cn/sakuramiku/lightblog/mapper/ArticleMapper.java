package cn.sakuramiku.lightblog.mapper;

import cn.sakuramiku.lightblog.entity.Article;
import cn.sakuramiku.lightblog.vo.SearchArticleParam;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.List;

/**
 * 文章SQL映射
 *
 * @author LYY
 */
public interface ArticleMapper {

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 是否成功
     */
    Boolean delete(@NonNull Long id);

    /**
     * 添加文章
     *
     * @param record 参数
     * @return 是否成功
     */
    Boolean insert(@NonNull Article record);

    /**
     * 获取文章
     *
     * @param id 文章ID
     * @return 文章
     */
    Article get(@NonNull Long id);

    /**
     * 搜索文章
     *
     * @param param 搜索参数
     * @return 文章列表
     */
    List<Article> search(@NonNull SearchArticleParam param);

    /**
     * 修改文章
     *
     * @param record 参数
     * @return 是否成功
     */
    Boolean update(@NonNull Article record);

    @Delete("delete from article where state = #{state} and mark_del_time <= #{start}")
    Long deleteForRecycle(@Param("state") Integer status, @Param("start") Date start);

    @Select("select distinct article_id from article_tag where tag_id = #{tagId}")
    List<Long> queryByTag(Long tagId);
}