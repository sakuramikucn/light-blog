package cn.sakuramiku.lightblog.controller;

import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.RedisUtil;
import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.common.util.ValidateUtil;
import cn.sakuramiku.lightblog.entity.User;
import cn.sakuramiku.lightblog.service.UserService;
import cn.sakuramiku.lightblog.util.Constant;
import cn.sakuramiku.lightblog.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 首页相关的方法集
 *
 * @author lyy
 */
@Api(tags = "页面方法集")
@CrossOrigin("*")
@RestController
public class IndexController {

    @Resource
    private UserService userService;
    @Resource
    private RedisUtil redisUtil;

    @PostMapping("/login")
    @ApiOperation("登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", dataTypeClass = String.class, value = "用户名"),
            @ApiImplicitParam(name = "password", dataTypeClass = String.class, value = "登录密码"),
    })
    public @ApiResponse(code = 0, message = "Token")
    Result<String> login(String username, String password) throws ApiException {
        ValidateUtil.isEmpty(username, "用户名为空");
        ValidateUtil.isEmpty(password, "登录密码为空");
        Boolean login = userService.login(username, password);
        if (login) {
            User user = userService.getUser(username);
            String token = JwtUtil.genToken(user);
            // 用于Token刷新
            redisUtil.set(Constant.PREFIX_REFRESH_TOKEN_REFRESH + username, token, 15 * 60L);
            return RespResult.ok(token);
        }
        return RespResult.fail("用户名或密码错误");
    }

    @PostMapping("/register")
    @ApiOperation("注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", dataTypeClass = String.class, value = "用户名"),
            @ApiImplicitParam(name = "password", dataTypeClass = String.class, value = "登录密码")
    })
    public Result<String> register(String username, String password) throws ApiException {
        ValidateUtil.isEmpty(username, "用户名为空");
        ValidateUtil.isEmpty(password, "登录密码为空");
        User user = userService.getUser(username);
        if (null != user) {
            return RespResult.fail("用户名重复");
        }
        boolean flag = userService.register(username, password);
        if (!flag) {
            return RespResult.fail("注册失败");
        }
        return RespResult.ok();
    }

    @RequiresAuthentication
    @ApiOperation("注销")
    @GetMapping("logout")
    public Result<Object> logout() {
        String token = (String) SecurityUtils.getSubject().getPrincipal();
        Claims claims = JwtUtil.getClaims(token);
        String id = claims.getId();
        Date expiration = claims.getExpiration();
        // 黑名单
        redisUtil.set(Constant.PREFIX_REFRESH_TOKEN_BAN + id, token, expiration.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        return RespResult.ok();
    }

    @GetMapping("/check/{username}")
    @ApiOperation("检查用户名是否可用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", dataTypeClass = String.class, value = "用户名", required = true)
    })
    public Result<Boolean> check(@PathVariable("username") String username) {
        User user = userService.getUser(username);
        return RespResult.ok(null == user);
    }
}
