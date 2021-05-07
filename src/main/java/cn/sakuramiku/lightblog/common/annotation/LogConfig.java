package cn.sakuramiku.lightblog.common.annotation;

import java.lang.annotation.*;

/**
 * 日志信息配置
 * @author LYY
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogConfig {

    /**
     * 具体引用
     * @return
     */
    String reference() default "";

    /**
     * 分类
     * @return
     */
    String category() default "";

    /**
     * 分类名称
     * @return
     */
    String name() default "";

}
