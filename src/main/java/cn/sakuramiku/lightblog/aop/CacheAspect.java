package cn.sakuramiku.lightblog.aop;

import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.annotation.*;
import cn.sakuramiku.lightblog.common.util.RedisUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

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
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                if (null != method.getDeclaredAnnotation(OnCacheChange.class)) {
                    String name = method.getName();
                    Long deletekeys = redisUtil.deletekeys(prefix + ":" + name + ":*");
                    logger.info("移除搜索缓存，生成key：{}，结果：{}", prefix + ":" + name + ":*",deletekeys);
                }
            }
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
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                if (null != method.getDeclaredAnnotation(OnCacheChange.class)) {
                    String name = method.getName();
                    Long deletekeys = redisUtil.deletekeys(prefix + ":" + name + ":*");
                    logger.info("移除搜索缓存，生成key：{}，结果：{}", prefix + ":" + name + ":*",deletekeys);
                }
            }
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
        Map<String, Object> paramsMap = paramsMap(point);
        Object o = paramsMap.get(toParamName(redisCache.key()));
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
            if (result instanceof Collection){
                if (((Collection) result).size() == 0){
                    return result;
                }
            }
            redisUtil.push(key, result);
        }
        return result;
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

    protected Map<String, Object> paramsMap(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Map<String, Object> params = new HashMap<>();
        String[] names = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        if (null == names) {
            return params;
        }
        for (int i = 0; i < names.length; i++) {
            params.put(names[i], args[i]);
        }
        return params;
    }

    protected String toParamName(String el) {
        int lastIndexOf = el.lastIndexOf(".");
        if (lastIndexOf > 0) {
            return el.substring(lastIndexOf + 1);
        }
        if (el.startsWith("#")) {
            return el.replace("#", "");
        }
        return el;
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

        boolean isResultEl = el.startsWith("#result");
        // 具体属性名称
        String paramName = toParamName(el);
        if (!isResultEl) {
            // 从参数上取
            // 属性间隔 ，user.id => user id
            String[] split = el.replace("#", "").split("\\.");
            Map<String, Object> paramsMap = paramsMap(point);

            // 最外层的值
            Object o = paramsMap.get(split[0]);
            if (split.length > 1) {
                for (int i = 1; i < split.length; i++) {
                    o = getFieldValue(o, split[i]);
                }
            }
            return null == o ? null : o.toString();
        } else {
            // 看看值本身是不是key
            if ("result".equalsIgnoreCase(paramName)) {
                return result.toString();
            }
            Object fieldValue = getFieldValue(result, paramName);
            return fieldValue == null ? null : fieldValue.toString();
        }
    }

    protected Object getFieldValue(Object target, String fieldName) {
        // 从返回值取
        try {
            for (Field field : target.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                if (name.equals(fieldName)) {
                    Object o = field.get(target);
                    return null == o ? null : o.toString();
                }
            }
        } catch (Exception e) {
            logger.warn("缓存错误，生成key失败");
        }
        return null;
    }
}
