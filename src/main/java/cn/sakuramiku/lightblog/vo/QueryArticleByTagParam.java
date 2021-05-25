package cn.sakuramiku.lightblog.vo;

import java.util.StringJoiner;

/**
 * @author lyy
 */
public class QueryArticleByTagParam extends PageParam {

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

    @Override
    public String toString() {
        return new StringJoiner(", ", QueryArticleByTagParam.class.getSimpleName() + "[", "]")
                .add("page=" + page)
                .add("pageSize=" + pageSize)
                .add("tagId=" + tagId)
                .add("isPublic=" + isPublic)
                .add("isSimple=" + isSimple)
                .add("mask=" + mask)
                .toString();
    }
}
