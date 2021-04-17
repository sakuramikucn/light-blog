package cn.sakuramiku.lightblog.annotation;

import java.lang.annotation.*;

/**
 * 标记缓存更改后清除缓存
 *
 * @author lyy
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnCacheChange {
}
