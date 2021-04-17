package cn.sakuramiku.lightblog.annotation;

import java.lang.annotation.*;

/**
 * 缓存配置
 * @author LYY
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RedisCacheConfig {

    String cacheName() default "";
}
