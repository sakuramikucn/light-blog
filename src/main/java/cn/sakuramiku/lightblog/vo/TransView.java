package cn.sakuramiku.lightblog.vo;

/**
 * S 原类型
 * T 目标类型
 * @author lyy
 */
public interface TransView<S, T> {

    /**
     * 数据转换
     * @param origin
     * @param params
     * @return
     */
    T valueOf(S origin,Object ...params);
}
