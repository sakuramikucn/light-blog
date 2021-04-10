package cn.sakuramiku.lightblog.aop;

import cn.sakuramiku.lightblog.annotation.OnChange;
import cn.sakuramiku.lightblog.common.util.RedisUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * 缓存操作切面
 *
 * @author lyy
 */
@Component
@Aspect
public class CacheAspect {

    @Resource
    private RedisUtil redisUtil;

    @AfterReturning(value = "@annotation(cachePut)", returning = "result", argNames = "joinPoint,result,cachePut")
    public void removeCacheByCachePut(JoinPoint joinPoint, Object result, CachePut cachePut) {
        if (result instanceof Boolean) {
            if (!Boolean.parseBoolean(result.toString())) {
                return;
            }
        }
        Class<?> aClass = joinPoint.getTarget().getClass();
        CacheConfig declaredAnnotation = aClass.getDeclaredAnnotation(CacheConfig.class);
        if (null != declaredAnnotation) {
            String[] cacheNames = declaredAnnotation.cacheNames();
            String prefix = cacheNames[0];
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                if (null != method.getDeclaredAnnotation(OnChange.class)) {
                    String name = method.getName();
                    redisUtil.deletekeys(prefix + "::" + name + ":*");
                }
            }
        }
    }

    @AfterReturning(value = "@annotation(cacheEvict)", returning = "result", argNames = "joinPoint,result,cacheEvict")
    public void removeCacheByCacheEvict(JoinPoint joinPoint, Object result, CacheEvict cacheEvict) {
        if (result instanceof Boolean) {
            if (!Boolean.parseBoolean(result.toString())) {
                return;
            }
        }
        Class<?> aClass = joinPoint.getTarget().getClass();
        CacheConfig declaredAnnotation = aClass.getDeclaredAnnotation(CacheConfig.class);
        if (null != declaredAnnotation) {
            String[] cacheNames = declaredAnnotation.cacheNames();
            String prefix = cacheNames[0];
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                if (null != method.getDeclaredAnnotation(OnChange.class)) {
                    String name = method.getName();
                    redisUtil.deletekeys(prefix + "::" + name + ":*");
                }
            }
        }
    }
}
