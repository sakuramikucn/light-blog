package cn.sakuramiku.lightblog.controller;

import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.util.RespCode;
import cn.sakuramiku.lightblog.common.util.RespResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author lyy
 */
@ApiIgnore
@RestController
@RequestMapping("/error")
public class ErrorController {

    @RequestMapping("/unauthorized")
    public Result<Object> unauthorized() {
        return RespResult.build().code(RespCode.UNAUTHORIZED);
    }

    @RequestMapping("/not_login")
    public Result<Object> notLogin() {
        return RespResult.build().code(RespCode.NOT_LOGIN);
    }

    @RequestMapping("/login_expired")
    public Result<Object> loginExpired() {
        return RespResult.build().code(RespCode.NOT_LOGIN).msg("登录过期（Token Expired）");
    }
}
