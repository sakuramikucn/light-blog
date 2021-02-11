package cn.sakuramiku.lightblog.annotation;

import java.lang.annotation.*;

/**
 * 不进行shiro权限认证
 *
 * @author lyy
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ShiroPass {
}
