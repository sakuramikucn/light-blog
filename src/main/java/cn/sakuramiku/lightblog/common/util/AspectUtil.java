package cn.sakuramiku.lightblog.common.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 切面辅助工具
 * @author lyy
 */
public class AspectUtil {

    public static final Logger logger = LoggerFactory.getLogger(AspectUtil.class);

    /**
     * 简单解析El表达式
     * @param el
     * @param joinPoint
     * @param result
     * @return
     */
    public static String parseElValueString(String el,JoinPoint joinPoint,Object result){
        Object value = parseElValue(el, joinPoint, result);
        return null == value ? null : value.toString();
    }

    /**
     * 简单解析El表达式
     * @param el
     * @param joinPoint
     * @param result
     * @return
     */
    public static Object parseElValue(String el,JoinPoint joinPoint,Object result){

        boolean isResultEl = el.startsWith("#result");
        // 具体属性名称
        String paramName = toParamName(el);
        if (!isResultEl) {
            // 从参数上取
            // 属性间隔 ，user.id => user id
            String[] split = el.replace("#", "").split("\\.");
            Map<String, Object> paramsMap = paramsMap(joinPoint);

            // 最外层的值
            Object o = paramsMap.get(split[0]);
            if (split.length > 1) {
                for (int i = 1; i < split.length; i++) {
                    o = getFieldValue(o, split[i]);
                }
            }
            return o;
        } else {
            // 看看值本身是不是key
            if ("result".equalsIgnoreCase(paramName)) {
                return result.toString();
            }
            return getFieldValue(result, paramName);
        }
    }

    public static Object getFieldValue(Object target, String fieldName) {
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

    public static Map<String, Object> paramsMap(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Map<String, Object> params = new HashMap<>(16);
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

    public static String toParamName(String el) {
        int lastIndexOf = el.lastIndexOf(".");
        if (lastIndexOf > 0) {
            return el.substring(lastIndexOf + 1);
        }
        if (el.startsWith("#")) {
            return el.replace("#", "");
        }
        return el;
    }
}
