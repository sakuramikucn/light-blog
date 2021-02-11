package cn.sakuramiku.lightblog.shiro;

import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.util.Constant;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 过滤器 preHandle->isAccessAllowed->isLoginAttempt->executeLogin
 *
 * @author lyy
 */
public class JwtFilter extends BasicHttpAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    /**
     * 未授权请求接口
     */
    private static final String UNAUTHORIZED_URL = "/unauthorized";


    /**
     * 1. 对跨域提供支持</br>
     * -> {@link #isAccessAllowed(ServletRequest, ServletResponse, Object)}
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }


    /**
     * 2. 验证是否可以访问（有权限）</br>
     * true -> {@link #isLoginAttempt(ServletRequest, ServletResponse)}
     * false -> {@link #onAccessDenied(ServletRequest, ServletResponse)}
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        String uri = httpServletRequest.getRequestURI();
        // 处理标记为 @ShiroPass 的方法
        for (String url : Constant.SHIRO_PASS_URL) {
            if (!StrUtil.isEmpty(uri) && uri.startsWith(url)) {
                return true;
            }
        }
        if (isLoginAttempt(request, response)) {
            try {
                return executeLogin(request, response);
            } catch (Exception e) {
                logger.error("认证失败", e);
                response401(request, response);
            }
        }
        return false;
    }

    /**
     * 3.判断用户是否想要登入,检测header里面是否包含Authorization字段即可 </br>
     * true -> {@link #executeLogin(ServletRequest, ServletResponse)}
     * false ->
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        String authzHeader = getAuthzHeader(request);
        return null != authzHeader;
    }

    /**
     * 禁止访问
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        sendChallenge(request, response);
        return false;
    }


    /**
     * 4.可以访问，验证
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        String authzHeader = getAuthzHeader(request);
        JwtToken token = new JwtToken(authzHeader);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(token);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }


    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        return false;
    }

    /**
     * 将非法请求跳转到 /unauthorized
     */
    private void response401(ServletRequest req, ServletResponse resp) {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        try {
            request.getRequestDispatcher(UNAUTHORIZED_URL).forward(request, response);
        } catch (ServletException | IOException e) {
            logger.error("请求转发到[{}]失败", UNAUTHORIZED_URL, e);
        }
    }
}
