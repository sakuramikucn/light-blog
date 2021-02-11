package cn.sakuramiku.lightblog.common.aop;

import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.RespCode;
import cn.sakuramiku.lightblog.common.util.RespResult;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashSet;
import java.util.Set;

/**
 * 异常统一处理
 *
 * @author lyy
 */
@RestControllerAdvice
public class ApiExceptionAdvice {

    @ExceptionHandler(ApiException.class)
    @ResponseStatus
    public Result<Object> handleApiException(ApiException e) {
        return new RespResult<>(null, e.getCode(), e.getMessage());
    }

    @ExceptionHandler({ShiroException.class, UnauthorizedException.class, UnknownAccountException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Object> handleShiroException(Exception e) {
        return commonResult(e, RespCode.UNAUTHORIZED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleNohandlerException(NoHandlerFoundException e) {
        return RespResult.fail(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus
    public Result<Object> handleAll(Exception e) {
        e.printStackTrace();
        String className = e.getClass().getName();
        Set<String> keywords = new HashSet<>();
        keywords.add("jdbc");
        for (String word : keywords) {
            if (className.contains(word)) {
                return new RespResult<>(RespCode.SERVER_INTERNAL_ERROR);
            }
        }
        return new RespResult<>(null, RespCode.SERVER_INTERNAL_ERROR, className + ": " + e.getMessage());
    }

    /**
     * 公共代码
     *
     * @param e
     * @return
     */
    private Result<Object> commonResult(Exception e, RespCode code) {
        if (null == code) {
            code = RespCode.SERVER_INTERNAL_ERROR;
        }
        e.printStackTrace();
        return new RespResult<>(null, code,  e.getMessage());
    }
}
