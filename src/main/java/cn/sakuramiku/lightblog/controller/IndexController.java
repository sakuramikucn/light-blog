package cn.sakuramiku.lightblog.controller;

import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.RespCode;
import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.common.util.ValidateUtil;
import cn.sakuramiku.lightblog.entity.User;
import cn.sakuramiku.lightblog.service.UserService;
import cn.sakuramiku.lightblog.util.JwtUtil;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 首页相关的方法集
 *
 * @author lyy
 */
@Api(tags = "页面方法集")
@RestController
public class IndexController {

    protected static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Resource
    UserService userService;

    @PostMapping("/login")
    @ApiOperation("登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", dataTypeClass = String.class, value = "用户名"),
            @ApiImplicitParam(name = "password", dataTypeClass = String.class, value = "登录密码"),
    })
    @ApiResponse(code = 0, message = "Token")
    public Result<String> login(String username, String password) throws ApiException {
        ValidateUtil.isEmpty(username, "用户名为空");
        ValidateUtil.isEmpty(password, "登录密码为空");
        if (userService.login(username,password)){
            User user = userService.getUser(username);
            String token = JwtUtil.genToken(user);
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
    public Result<Object> register( String username,  String password) throws ApiException {
        ValidateUtil.isEmpty(username, "用户名为空");
        ValidateUtil.isEmpty(password, "登录密码为空");
        User user1 = new User();
        boolean flag = userService.register(username, password);
        if (!flag) {
            return RespResult.fail("注册失败");
        }
        return RespResult.ok();
    }

    @ApiOperation("未授权")
    @GetMapping("/unauthorized")
    public Result<Object> unauthorized(){
        return RespResult.build().code(RespCode.UNAUTHORIZED);
    }


}
