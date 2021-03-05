package cn.sakuramiku.lightblog.shiro;

import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.common.util.RedisUtil;
import cn.sakuramiku.lightblog.entity.User;
import cn.sakuramiku.lightblog.service.UserService;
import cn.sakuramiku.lightblog.util.BlogHelper;
import cn.sakuramiku.lightblog.util.Constant;
import cn.sakuramiku.lightblog.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
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
@Component
public class JwtFilter extends BasicHttpAuthenticationFilter {

    @Resource
    private UserService userService;
    @Resource
    private RedisUtil redisUtil;

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

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
        String method = httpServletRequest.getMethod();
        uri = BlogHelper.genReqUrl(method, uri);
        // 处理标记为 @ShiroPass 的方法
        for (String url : Constant.SHIRO_PASS_URL) {
            if (!StrUtil.isEmpty(uri) && uri.startsWith(url)) {
                return true;
            }
        }
        if (isLoginAttempt(request, response)) {
            try {
                return executeLogin(request, response);
            } catch (JwtException e) {
                forward(request, response, Constant.REQUEST_LOGIN_EXPIRED);
            } catch (AuthenticationException e2) {
                forward(request, response, Constant.REQUEST_NOT_LOGIN);
            } catch (Exception e4) {
                forward(request, response, Constant.REQUEST_UNAUTHORIZED);
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
        // 校验Token是否需要刷新
        Boolean isRefresh = false;
        try {
            JwtUtil.getClaims(authzHeader);
        } catch (ExpiredJwtException e) {
            Claims claims = e.getClaims();
            String username = (String) claims.get("username");
            // Token过期了
            String tokenKey = genTokenKey(username);
            // 有缓存
            if (redisUtil.hasKey(tokenKey)) {
                // 缓存中Token未过期
                if (!redisUtil.isExpired(tokenKey)) {
                    // 刷新Token
                    User user = userService.getUser(username);
                    String newToken = JwtUtil.genToken(user);
                    token = new JwtToken(newToken);
                    redisUtil.delete(tokenKey);
                    // 刷新缓存
                    redisUtil.set(tokenKey, newToken, 30 * 60L);
                    isRefresh = true;
                    JwtUtil.logger.info("刷新了Token：{}", newToken);
                }
            }
        }
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(token);
        if (isRefresh) {
            // 返回新的Token
            HttpServletResponse servletResponse = (HttpServletResponse) response;
            servletResponse.setHeader(AUTHORIZATION_HEADER, (String) token.getPrincipal());
        }
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }


    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        return false;
    }

    /**
     * 转发到指定请求路径
     */
    private void forward(ServletRequest req, ServletResponse resp, String url) {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        try {
            request.getRequestDispatcher(url).forward(req, response);
        } catch (ServletException | IOException e) {
            logger.error("请求转发到[{}]失败", url, e);
        }
    }

    /**
     * 生成Token缓存key
     *
     * @param username
     * @return
     */
    protected String genTokenKey(String username) {
        return Constant.PREFIX_REFRESH_TOKEN + username;
    }
}
