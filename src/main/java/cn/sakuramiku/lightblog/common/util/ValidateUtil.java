package cn.sakuramiku.lightblog.common.util;

import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.common.exception.ApiException;

import java.util.List;

/**
 * 参数校验工具
 *
 * @author lyy
 */
public final class ValidateUtil {

    /**
     * 是否为null或长度为0
     *
     * @param value
     * @param tip
     * @throws ApiException
     */
    public static void isEmpty(String value, String tip) throws ApiException {
        checkIllegalArgument(StrUtil.isEmpty(value), tip);
    }

    /**
     * 是否为null或长度为0
     *
     * @param list
     * @param tip
     * @param <E>
     * @throws ApiException
     */
    public static <E> void isEmpty(List<E> list, String tip) throws ApiException {
        checkIllegalArgument(null == list || 0 == list.size(), tip);
    }

    /**
     * 是否为空
     *
     * @param obj 对象
     * @param tip
     * @throws ApiException
     */
    public static void isNull(Object obj, String tip) throws ApiException {
        checkIllegalArgument(null == obj, tip);
    }

    /**
     * 检查参数异常
     *
     * @param condition 条件 为true时返回异常
     * @param tip       错误信息
     * @throws ApiException 接口异常
     */
    public static void checkIllegalArgument(boolean condition, String tip) throws ApiException {
        check(condition, RespCode.VALIDATE_ERROR, tip);
    }

    /**
     * 检查参数
     *
     * @param condition
     * @param code
     * @param message   错误信息
     * @throws ApiException
     */
    public static void check(boolean condition, RespCode code, String message) throws ApiException {
        if (condition) {
            throw new ApiException(code, message);
        }
    }
}
