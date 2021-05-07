package cn.sakuramiku.lightblog.aop;

import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.annotation.*;
import cn.sakuramiku.lightblog.common.util.AspectUtil;
import cn.sakuramiku.lightblog.common.util.RedisUtil;
import com.github.pagehelper.PageSerializable;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * 缓存操作切面
 *
 * @author lyy
 */
@Component
@Aspect
public class CacheAspect {

    public static final Logger logger = LoggerFactory.getLogger(CacheAspect.class);

    @Resource
    private RedisUtil redisUtil;

    private final Boolean openCache = Boolean.parseBoolean(System.getProperty("cache.customize.enable","false"));

    /**
     * 更新缓存
     *
     * @param joinPoint
     * @param result
     * @param redisCachePut
     */
    @Order(1)
    @AfterReturning(value = "@annotation(redisCachePut)", returning = "result", argNames = "joinPoint,result,redisCachePut")
    public void removeCacheByCachePut(JoinPoint joinPoint, Object result, RedisCachePut redisCachePut) {
        if (!openCache){
            return;
        }
        if (result instanceof Boolean) {
            if (!Boolean.parseBoolean(result.toString())) {
                return;
            }
        }
        Class<?> aClass = joinPoint.getTarget().getClass();
        RedisCacheConfig declaredAnnotation = aClass.getDeclaredAnnotation(RedisCacheConfig.class);
        String prefix = "";

        // 处理查询方法的缓存
        if (null != declaredAnnotation) {
            prefix = declaredAnnotation.cacheName();
            cleanQueryCache(joinPoint);
        }

        if (null != result) {
            String suffix = getCacheUpdateKeyValue(redisCachePut, joinPoint, result);
            if (StringUtils.isEmpty(suffix)) {
                return;
            }
            String key = prefix + ":" + suffix;
            logger.info("@RedisCachePut 缓存Key生成：{}", key);
            redisUtil.push(key, result);
        }
    }

    /**
     * 更新缓存
     *
     * @param joinPoint
     * @param result
     * @param redisCacheDelete
     */
    @Order(1)
    @AfterReturning(value = "@annotation(redisCacheDelete)", returning = "result", argNames = "joinPoint,result,redisCacheDelete")
    public void removeCacheByCacheEvict(JoinPoint joinPoint, Object result, RedisCacheDelete redisCacheDelete) {
        if (!openCache){
            return;
        }
        if (result instanceof Boolean) {
            if (!Boolean.parseBoolean(result.toString())) {
                return;
            }
        }
        Class<?> aClass = joinPoint.getTarget().getClass();
        String prefix = "";
        RedisCacheConfig declaredAnnotation = aClass.getDeclaredAnnotation(RedisCacheConfig.class);

        // 处理查询方法的缓存
        if (null != declaredAnnotation) {
            prefix = declaredAnnotation.cacheName();
            cleanQueryCache(joinPoint);
        }

        // 删除缓存
        String suffix = getCacheUpdateKeyValue(redisCacheDelete, joinPoint, result);
        if (StringUtils.isEmpty(suffix)) {
            return;
        }
        String key = prefix + ":" + suffix;
        logger.info("@RedisCacheDelete 缓存Key生成：{}", key);

        redisUtil.delete(key);
    }

    /**
     * 用自己的缓存
     *
     * @param point
     * @param redisCache
     */
    @Order(2)
    @Around(value = "@annotation(redisCache)")
    public Object cache(ProceedingJoinPoint point, RedisCache redisCache) throws Throwable {
        String property = System.getProperty("cache.customize.enable", "false");
        boolean enable = Boolean.parseBoolean(property);

        if (!enable) {
            return point.proceed();
        }

        Class<?> aClass = point.getTarget().getClass();
        RedisCacheConfig declaredAnnotation = aClass.getDeclaredAnnotation(RedisCacheConfig.class);
        String prefix = "";
        if (null != declaredAnnotation) {
            prefix = declaredAnnotation.cacheName();
        }
        Map<String, Object> paramsMap = AspectUtil.paramsMap(point);
        Object o = paramsMap.get(AspectUtil.toParamName(redisCache.key()));
        String suffix = null == o ? "" : o.toString();
        String key = "";
        if (!StringUtils.isEmpty(suffix)) {
            key = prefix + ":" + suffix;
        }

        if (StringUtils.isEmpty(suffix)) {
            key = prefix + ":" + genKey(point.getSignature().getName(), point.getArgs());
        }

        logger.info("@RedisCache 缓存Key生成：{}", key);

        // 1、获取缓存
        Object val = redisUtil.get(key);
        if (null != val) {
            return val;
        }

        // 2 、执行方法
        Object result = point.proceed();

        if (null != result) {
            if (result instanceof Collection) {
                if (((Collection) result).size() == 0) {
                    return result;
                }
            }
            if (result instanceof PageSerializable) {
                if (((PageSerializable) result).getTotal() == 0) {
                    return result;
                }
            }
            redisUtil.push(key, result);
        }
        return result;
    }


    @Order(4)
    @AfterReturning(value = "@annotation(redisCleanQuery)", returning = "result", argNames = "joinPoint,result,redisCleanQuery")
    public void cleanQuery(JoinPoint joinPoint, Object result, RedisCleanQuery redisCleanQuery) {
        // 删除指定key的缓存
        String key = redisCleanQuery.key();
        if (!StringUtils.isEmpty(key)) {
            Class<?> aClass = joinPoint.getTarget().getClass();
            RedisCacheConfig declaredAnnotation = aClass.getDeclaredAnnotation(RedisCacheConfig.class);
            if (null != declaredAnnotation) {
                String prefix = declaredAnnotation.cacheName();
                Long deletekeys = redisUtil.deletekeys(prefix + ":" + key);
                logger.info("移除搜索缓存，生成key：{}，结果：{}", prefix + ":" + key, deletekeys);
                return;
            }
        }

        cleanQueryCache(joinPoint);
    }

    /**
     * 清除 @OnCacheChange 标记方法的缓存
     *
     * @param joinPoint
     */
    protected void cleanQueryCache(JoinPoint joinPoint) {
        Class<?> aClass = joinPoint.getTarget().getClass();
        RedisCacheConfig declaredAnnotation = aClass.getDeclaredAnnotation(RedisCacheConfig.class);
        // 处理查询方法的缓存
        if (null != declaredAnnotation) {
            String prefix = declaredAnnotation.cacheName();
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                if (null != method.getDeclaredAnnotation(OnCacheChange.class)) {
                    String name = method.getName();
                    Long deletekeys = redisUtil.deletekeys(prefix + ":" + name + ":*");
                    logger.info("移除搜索缓存，生成key：{}，结果：{}", prefix + ":" + name + ":*", deletekeys);
                }
            }
        }
    }


    /**
     * 获取更新缓存的Key 值
     *
     * @return
     */
    protected String getCacheUpdateKeyValue(Annotation annotation, JoinPoint point, Object result) {
        String el = "";
        if (annotation instanceof RedisCachePut) {
            el = ((RedisCachePut) annotation).key();
        } else if (annotation instanceof RedisCacheDelete) {
            el = ((RedisCacheDelete) annotation).key();
        }

        if (StringUtils.isEmpty(el)) {
            throw new NullPointerException("更新缓存必须指定Key");
        }

        return AspectUtil.parseElValueString(el, point, result);
    }

    /**
     * 生成 缓存key suffix
     *
     * @param methodName
     * @param args
     * @return
     */
    protected String genKey(String methodName, Object... args) {
        StringBuilder sb = new StringBuilder();
        for (Object param : args) {
            if (null == param) {
                continue;
            }
            if (StrUtil.isBlank(param.toString())) {
                continue;
            }
            sb.append(param).append(":");
        }
        String params = sb.toString();
        if (!StringUtils.isEmpty(params) && ':' == sb.charAt(params.length() - 1)) {
            params = sb.deleteCharAt(sb.lastIndexOf(":")).toString();
        }
        return methodName + ":" + params;
    }

}
