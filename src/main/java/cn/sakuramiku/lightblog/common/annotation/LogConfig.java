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
     * 引用
     * @return
     */
    String reference() default "";

    /**
     * 子引用
     * @return
     */
    String subReference() default "";

    /**
     * 模块名称
     * @return
     */
    String name() default "";
}
