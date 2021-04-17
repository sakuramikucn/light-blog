package cn.sakuramiku.lightblog.annotation;

import java.lang.annotation.*;

/**
 * 更新缓存
 * @author LYY
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisCachePut {

    String key();

}
