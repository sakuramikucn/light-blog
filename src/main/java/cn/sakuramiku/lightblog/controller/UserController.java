package cn.sakuramiku.lightblog.controller;

import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.annotation.ShiroPass;
import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.ResultPage;
import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.common.util.SimpleResultPage;
import cn.sakuramiku.lightblog.entity.User;
import cn.sakuramiku.lightblog.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户模块方法集
 *
 * @author lyy
 */
@Api(tags = "用户模块方法集")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @RequiresAuthentication
    @ApiOperation("通过ID获取用户")
    @ApiImplicitParam(name = "id", dataTypeClass = Long.class, required = true, value = "用户ID")
    @GetMapping("/id/{id}")
    public Result<User> user(@PathVariable(value = "id") long id) {
        User user = userService.getUser(id);
        return RespResult.ok(user);
    }

    @RequiresAuthentication
    @ApiOperation("通过用户名获取用户")
    @ApiImplicitParam(name = "username", dataTypeClass = String.class, required = true, value = "用户名")
    @GetMapping("/name/{username}")
    public Result<User> user(@PathVariable(value = "username") String username) {
        User user = userService.getUser(username);
        return RespResult.ok(user);
    }

    @ShiroPass
    @ApiOperation("搜索用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "关键字"),
            @ApiImplicitParam(name = "page", value = "第几页", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页多少项", defaultValue = "20")
    })
    @GetMapping("/search/{keyword}/{page}/{pageSize}")
    public Result<ResultPage<User>> users(@PathVariable("keyword") String keyword, @PathVariable("page") Integer page, @PathVariable("pageSize") Integer pageSize) {
        if (StrUtil.isBlank(keyword)){
            keyword = null;
        }
        List<User> users = userService.searchUser(keyword);
        return RespResult.ok(SimpleResultPage.valueOf(users));
    }
}
