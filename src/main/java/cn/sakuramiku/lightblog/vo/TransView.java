package cn.sakuramiku.lightblog.vo;

/**
 * S 原类型
 * T 目标类型
 * @author lyy
 */
public interface TransView<S, T> {

    T valueOf(S origin);
}
