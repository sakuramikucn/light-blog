package cn.sakuramiku.lightblog.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * JWT Token
 *
 * @author lyy
 */
public class JwtToken implements AuthenticationToken {

    private final String token;

    public JwtToken(String token) {
        this.token = token;
    }

    /**
     * 账号
     *
     * @return
     */
    @Override
    public Object getPrincipal() {
        return token;
    }

    /**
     * 密码
     *
     * @return
     */
    @Override
    public Object getCredentials() {
        return token;
    }
}
