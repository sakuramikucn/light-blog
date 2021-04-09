package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Article;
import cn.sakuramiku.lightblog.vo.SearchArticleParam;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.NonNull;

/**
 * 文章服务接口
 *
 * @author lyy
 */
public interface ArticleService {

    /**
     * 创建文章
     *
     * @param article 文章
     * @return 文章ID
     */
    Long saveArticle(@NonNull Article article);

    /**
     * 修改文章
     *
     * @param article 参数
     * @return 是否成功
     */
    Boolean updateArticle(@NonNull Article article);

    /**
     * 标记为删除
     *
     * @param id 文章ID
     * @return 是否成功
     */
    Boolean removeArticle(@NonNull Long id);

    /**
     * 删除文章
     * @param id
     * @return
     */
    Boolean deleteArticle(@NonNull Long id);

    /**
     * 获取文章
     *
     * @param id 文章ID
     * @return 文章
     */
    Article getArticle(@NonNull Long id);

    /**
     * 搜索文章
     *
     * @param param 搜索参数
     * @return 文章列表
     */
    PageInfo<Article> searchArticle(@NonNull SearchArticleParam param);
}
