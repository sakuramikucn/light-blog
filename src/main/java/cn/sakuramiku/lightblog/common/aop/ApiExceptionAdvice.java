package cn.sakuramiku.lightblog.common.aop;

import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.RespCode;
import cn.sakuramiku.lightblog.common.util.RespResult;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
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

    @ExceptionHandler({UnauthenticatedException.class, UnknownAccountException.class})
    public Result<Object> handleUnauthenticatedException() {
        return RespResult.build().code(RespCode.NOT_LOGIN);
    }

    @ExceptionHandler({ShiroException.class, UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Object> handleShiroException() {
        return RespResult.build().code(RespCode.UNAUTHORIZED);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public Result<Object> handleTokenExpireException() {
        return RespResult.build().code(RespCode.NOT_LOGIN).msg("登录过期（Token Expired）");
    }

    @ExceptionHandler(JwtException.class)
    public Result<Object> handleJwtException() {
        return RespResult.build().code(RespCode.NOT_LOGIN);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleNoHandlerException(NoHandlerFoundException e) {
        return RespResult.fail(e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public Result<Object> handleBindException(BindException e) {
        return commonResult(e, RespCode.BAD_REQUEST_PARAMETER);
    }

    @ExceptionHandler(java.sql.SQLIntegrityConstraintViolationException.class)
    public Result<Object> handlePrimaryKeyDuplicateException(Exception e) {
        String message = e.getMessage();
        if (message.contains("Duplicate")) {
            String value = message.substring(message.indexOf("entry '") + 8, message.indexOf("' for key"));
            String key = message.substring(message.indexOf("for key '") + 11);
            return RespResult.build().code(RespCode.BAD_REQUEST_PARAMETER).msg("重复的数据，key=" + key + " value=" + value);
        }
        return handleAll(e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus
    public Result<Object> handleAll(Exception e) {
        e.printStackTrace();
        String className = e.getClass().getName();
        Set<String> keywords = new HashSet<>();
        keywords.add("jdbc");
        keywords.add("dao");
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
     * @param code
     * @return
     */
    private Result<Object> commonResult(Exception e, RespCode code) {
        if (null == code) {
            code = RespCode.SERVER_INTERNAL_ERROR;
        }
        e.printStackTrace();
        return new RespResult<>(null, code, e.getMessage());
    }

    /**
     * 公共代码
     *
     * @param e
     * @param code
     * @return
     */
    private Result<Object> simpleCommonResult(Exception e, RespCode code) {
        if (null == code) {
            code = RespCode.SERVER_INTERNAL_ERROR;
        }
        return new RespResult<>(null, code, e.getMessage());
    }
}
