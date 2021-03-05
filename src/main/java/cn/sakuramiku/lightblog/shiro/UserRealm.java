package cn.sakuramiku.lightblog.shiro;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.entity.Right;
import cn.sakuramiku.lightblog.entity.Role;
import cn.sakuramiku.lightblog.entity.User;
import cn.sakuramiku.lightblog.service.UserService;
import cn.sakuramiku.lightblog.util.JwtUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
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


}
