package cn.sakuramiku.lightblog.common.util;

import cn.sakuramiku.lightblog.common.StatusCode;

/**
 * 响应码
 *
 * @author LYY
 */
public enum RespCode implements StatusCode {

    /**
     * code:0 msg: 请求成功
     */
    SUCCESS(0, "请求成功"),

    /**
     * code:1001 msg: 参数校验失败
     */
    VALIDATE_ERROR(1001, "参数校验失败"),
    /**
     * code:1002 msg: 错误的请求参数
     */
    BAD_REQUEST_PARAMETER(1002, "错误的请求参数"),

    /**
     * code:2001 msg: 无权限
     */
    UNAUTHORIZED(2001, "无权限"),
    /**
     * code:2002 msg: 未登录
     */
    NOT_LOGIN(2002,"未登录"),

    /**
     * code:3001 msg: 服务内部错误
     */
    SERVER_INTERNAL_ERROR(3001, "服务内部错误");

    private final int code;
    private final String msg;

    RespCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
