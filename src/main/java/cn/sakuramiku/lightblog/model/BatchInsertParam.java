package cn.sakuramiku.lightblog.model;

/**
 * 通用中间表批量插入参数
 *
 * @author lyy
 */
public class BatchInsertParam {
    /**
     * 源ID
     */
    private Long originId;
    /**
     * 对应ID
     */
    private Long targetId;

    public BatchInsertParam(Long originId, Long targetId) {
        this.originId = originId;
        this.targetId = targetId;
    }

    public static BatchInsertParam valueOf(Long originId, Long targetId) {
        if (null == originId || null == targetId) {
            return null;
        }
        return new BatchInsertParam(originId, targetId);
    }

    public Long getOriginId() {
        return originId;
    }

    public void setOriginId(Long originId) {
        this.originId = originId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    @Override
    public String toString() {
        return "BatchInsertParam{" +
                "originId=" + originId +
                ", targetId=" + targetId +
                '}';
    }
}