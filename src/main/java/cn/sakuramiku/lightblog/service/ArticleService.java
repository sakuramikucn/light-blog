package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Article;

import java.util.List;

/**
 * 文章服务接口
 *
 * @author lyy
 */
public interface ArticleService {

    /**
     * 创建文章
     *
     * @param article
     * @return
     */
    Article saveArticle(Article article);

    /**
     * 修改文章
     *
     * @param article
     * @return
     */
    Article updateArticle(Article article);

    /**
     * 删除文章
     *
     * @param id
     * @return
     */
    boolean removeArticle(String id);

    /**
     * 获取文章
     *
     * @param id
     * @return
     */
    Article getArticle(String id);

    /**
     * 搜索文章
     *
     * @param condition
     * @return
     */
    List<Article> searchArticle(Article condition);

    /**
     * 状态扭转
     *
     * @param state
     * @return
     */
    Article opsStatus(int state);
}
