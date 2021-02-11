package cn.sakuramiku.lightblog.common.exception;

import cn.sakuramiku.lightblog.common.util.RespCode;

/**
 * 接口异常类
 *
 * @author lyy
 */
public class ApiException extends Exception {

    private final int code;

    public ApiException(RespCode code, String msg) {
        super(msg);
        this.code = code.getCode();
    }

    public ApiException(RespCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code.getCode();
    }

    public ApiException(RespCode code, Throwable cause) {
        super(cause);
        this.code = code.getCode();
    }

    public int getCode() {
        return code;
    }
}
