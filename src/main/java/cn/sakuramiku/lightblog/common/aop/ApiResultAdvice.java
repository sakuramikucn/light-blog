package cn.sakuramiku.lightblog.common.aop;

import cn.sakuramiku.lightblog.common.util.RespResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Controller返回结果处理
 *
 * @author lyy
 */
@RestControllerAdvice(basePackages = {"**.controller"})
public class ApiResultAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        // 方法返回的对象不是 RespResult 时执行 beforeBodyWrite() 方法
        return !methodParameter.getParameterType().isAssignableFrom(RespResult.class);
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        return new RespResult<>(o);
    }

}
