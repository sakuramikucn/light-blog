package cn.sakuramiku.lightblog.annotation;

import java.lang.annotation.*;

/**
 * @author LYY
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisClean {

    String[] key() default {};
}
