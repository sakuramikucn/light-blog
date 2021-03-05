package cn.sakuramiku.lightblog.shiro;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.common.util.RedisUtil;
import cn.sakuramiku.lightblog.entity.Right;
import cn.sakuramiku.lightblog.entity.Role;
import cn.sakuramiku.lightblog.entity.User;
import cn.sakuramiku.lightblog.service.UserService;
import cn.sakuramiku.lightblog.util.Constant;
import cn.sakuramiku.lightblog.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 登录认证授权
 *
 * @author lyy
 */
public class UserRealm extends AuthorizingRealm {

    @Resource
    private UserService userService;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 认证
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String jwtToken = (String) token.getPrincipal();
        String userName = JwtUtil.getUserName(jwtToken);

        // 校验Token是否需要刷新
        try {
            Claims claims = JwtUtil.getClaims(jwtToken);
            Boolean hasKey = redisUtil.hasKey(Constant.PREFIX_REFRESH_TOKEN_BAN + claims.getId());
            // 黑名单，Token已注销
            if (hasKey) {
                throw new UnauthenticatedException("Token已失效");
            }
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
                    JwtUtil.logger.info("刷新了Token：{}", newToken);
                }
            }
        }
        if (StrUtil.isEmpty(userName)) {
            throw new UnknownAccountException("无效的用户");
        }
        User user = userService.getUser(userName);
        if (ObjectUtil.isNull(user)) {
            throw new UnknownAccountException("无效的用户");
        }
        return new SimpleAuthenticationInfo(jwtToken, jwtToken, getName());
    }

    /**
     * 授权
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        String userName = JwtUtil.getUserName(principals.toString());
        User user = userService.getUser(userName);
        if (null == user) {
            throw new UnknownAccountException(String.format("授权失败，无此用户(%s)", userName));
        }
        List<Role> userRoles = user.getRoles();
        if (CollectionUtil.isEmpty(userRoles)) {
            return null;
        }
        Set<String> roles = new HashSet<>(userRoles.size());
        Set<String> rights = new HashSet<>();
        userRoles.parallelStream().forEach(role -> {
            roles.add(role.getName());
            for (Right right : role.getRights()) {
                if (right.getRule() != Right.RULE_ALLOW) {
                    continue;
                }
                rights.add(right.getName());
            }
        });
        info.setRoles(roles);
        info.setStringPermissions(rights);
        return info;
    }

    /**
     * 生成Token缓存key
     *
     * @param username
     * @return
     */
    protected String genTokenKey(String username) {
        return Constant.PREFIX_REFRESH_TOKEN_REFRESH + username;
    }

}
