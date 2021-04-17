package cn.sakuramiku.lightblog.annotation;

import java.lang.annotation.*;

/**
 * @author LYY
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisCacheDelete {

    String key();
}
