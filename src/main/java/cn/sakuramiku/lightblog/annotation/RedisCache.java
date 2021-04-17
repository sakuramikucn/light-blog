package cn.sakuramiku.lightblog.annotation;

import java.lang.annotation.*;

/**
 * 标记为缓存
 * @author LYY
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisCache {

    String key() default "";
}
