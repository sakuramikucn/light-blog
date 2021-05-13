package cn.sakuramiku.lightblog.vo;

import java.util.List;

/**
 * @author lyy
 */
public class ArticleArchivesView {

    protected Integer year;
    protected  Integer month;
    protected String key;
    protected List<SimpleArticleView> articles;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<SimpleArticleView> getArticles() {
        return articles;
    }

    public void setArticles(List<SimpleArticleView> articles) {
        this.articles = articles;
    }
}
