package cn.sakuramiku.lightblog.vo;

import cn.sakuramiku.lightblog.entity.Article;
import cn.sakuramiku.lightblog.entity.Category;
import cn.sakuramiku.lightblog.entity.Tag;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lyy
 */
@ApiModel("简单的文章视图")
public class SimpleArticleView implements TransView<Article,SimpleArticleView> {
    /**
     * ID，唯一标识
     */
    @JsonSerialize(using= ToStringSerializer.class)
    @ApiModelProperty(value = "ID，唯一标识")
    private Long id;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 文章简介
     */
    @ApiModelProperty(value = "文章简介")
    private String desc;

    /**
     * 作者
     */
    @ApiModelProperty(value = "作者")
    private String author;

    /**
     * 类别
     */
    @ApiModelProperty(value = "类别")
    private Category category;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 封面图片链接
     */
    @ApiModelProperty(value = "封面图片链接")
    private String coverUrl;

    @ApiModelProperty(value = "标签")
    private List<Tag> tags;

    @ApiModelProperty(value = "文章浏览数")
    private Long views;

    @ApiModelProperty(value = "评论数")
    private Integer commentNum;

    @Override
    public SimpleArticleView valueOf(Article article,Object ...params) {
        if (null == article) {
            return null;
        }
        id = article.getId();
        author = article.getAuthor();
        title = article.getTitle();
        category = article.getCategory();
        desc = article.getDesc();
        tags = article.getTags();
        coverUrl = article.getCoverUrl();
        createTime = article.getCreateTime();
        views = (Long)params[0];
        commentNum = Integer.valueOf(params[1].toString());
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }
}
