package cn.sakuramiku.lightblog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * article
 * @author 
 */
@ApiModel(value="cn.sakuramiku.lightblog.entity.Article文章表")
public class Article implements Serializable {
    /**
     * ID，唯一标识
     */
    @ApiModelProperty(value="ID，唯一标识")
    private Long id;

    /**
     * 标题
     */
    @ApiModelProperty(value="标题")
    private String title;

    /**
     * 状态，1=正常，2=待删除
     */
    @ApiModelProperty(value="状态，1=正常，2=待删除")
    private Byte state;

    /**
     * 文章简介
     */
    @ApiModelProperty(value="文章简介")
    private String desc;

    /**
     * 文章内容
     */
    @ApiModelProperty(value="文章内容")
    private String content;

    /**
     * 作者
     */
    @ApiModelProperty(value="作者")
    private String author;

    /**
     * 类别
     */
    @ApiModelProperty(value="类别")
    private String category;

    /**
     * 标记为 ‘待删除’ 状态的时间，用于判断最终删除时间
     */
    @ApiModelProperty(value="标记为 ‘待删除’ 状态的时间，用于判断最终删除时间")
    private LocalDateTime markDelTime;

    /**
     * 创建时间
     */
    @ApiModelProperty(value="创建时间")
    private LocalDateTime createTime;

    /**
     * 文章浏览数，非最新数据，定时持久化
     */
    @ApiModelProperty(value="文章浏览数，非最新数据，定时持久化")
    private Integer pageViews;

    /**
     * 封面图片链接
     */
    @ApiModelProperty(value="封面图片链接")
    private String coverUrl;

    /**
     * 最后修改时间
     */
    @ApiModelProperty(value="最后修改时间")
    private LocalDateTime modifiedTime;

    /**
     * 布尔类型标记
     */
    @ApiModelProperty(value="布尔类型标记")
    private Integer mask;

    private static final long serialVersionUID = 1L;

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

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getMarkDelTime() {
        return markDelTime;
    }

    public void setMarkDelTime(LocalDateTime markDelTime) {
        this.markDelTime = markDelTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getPageViews() {
        return pageViews;
    }

    public void setPageViews(Integer pageViews) {
        this.pageViews = pageViews;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(LocalDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Integer getMask() {
        return mask;
    }

    public void setMask(Integer mask) {
        this.mask = mask;
    }
}