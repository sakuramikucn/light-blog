package cn.sakuramiku.lightblog.vo;

import cn.sakuramiku.lightblog.entity.Article;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author lyy
 */
@ApiModel("文章视图")
public class ArticleView extends Article {

    @ApiModelProperty("文章评论数")
    private Long comments;

    public static ArticleView valueOf(Article article,Long views,Long comments){
        ArticleView view = new ArticleView();
        view.setId(article.getId());
        view.setComments(comments);
        view.setAuthor(article.getAuthor());
        view.setCreateTime(article.getCreateTime());
        view.setModifiedTime(article.getModifiedTime());
        view.setMarkDelTime(article.getMarkDelTime());
        view.setState(article.getState());
        view.setCategory(article.getCategory());
        view.setContent(article.getContent());
        view.setCoverUrl(article.getCoverUrl());
        view.setDesc(article.getDesc());
        view.setPageViews(Integer.parseInt(views.toString()));
        view.setTitle(article.getTitle());
        view.setTags(article.getTags());
        view.setMask(article.getMask());
        view.setPublic(article.getPublic());
        view.setAuthorId(article.getAuthorId());
        return view;
    }


    public Long getComments() {
        return comments;
    }

    public void setComments(Long comments) {
        this.comments = comments;
    }
}
