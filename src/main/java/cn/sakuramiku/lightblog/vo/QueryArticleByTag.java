package cn.sakuramiku.lightblog.vo;

/**
 * @author lyy
 */
public class QueryArticleByTag extends PageParam {

    protected Long tagId;
    protected Boolean isPublic;
    protected Boolean isSimple = false;
    protected Integer mask;

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Boolean getSimple() {
        return isSimple;
    }

    public void setSimple(Boolean simple) {
        isSimple = simple;
    }

    public Integer getMask() {
        return mask;
    }

    public void setMask(Integer mask) {
        this.mask = mask;
    }
}
