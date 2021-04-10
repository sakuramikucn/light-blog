package cn.sakuramiku.lightblog.controller;

import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.common.util.ValidateUtil;
import cn.sakuramiku.lightblog.entity.User;
import cn.sakuramiku.lightblog.service.UserService;
import cn.sakuramiku.lightblog.util.Constant;
import cn.sakuramiku.lightblog.util.JwtUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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

    public static final String DEFAULT_PASSWORD = "sakuramiku.cn";

    @RequiresAuthentication
    @ApiOperation("通过ID获取用户")
    @ApiImplicitParam(name = "id", dataTypeClass = Long.class, required = true, value = "用户ID")
    @GetMapping("/{id}")
    public Result<User> user(@PathVariable(value = "id") long id) throws ApiException {
        User user = userService.getUser(id);
        ValidateUtil.isNull(user, "参数错误，用户ID无效！" + id);
        return RespResult.ok(user);
    }

    @RequiresAuthentication
    @ApiOperation("通过Token获取用户")
    @ApiImplicitParam(name = "id", dataTypeClass = Long.class, required = true, value = "用户ID")
    @GetMapping("")
    public Result<User> userInfo() throws ApiException {
        Subject subject = SecurityUtils.getSubject();
        String token = (String) subject.getPrincipal();
        String userName = JwtUtil.getUserName(token);
        assert userName != null;
        User user = userService.getUser(userName);
        ValidateUtil.isNull(user, "无此用户" );
        return RespResult.ok(user);
    }

    @RequiresAuthentication
    @ApiOperation("通过用户名获取用户")
    @ApiImplicitParam(name = "username", dataTypeClass = String.class, required = true, value = "用户名")
    @GetMapping("/name/{username}")
    public Result<User> user(@PathVariable(value = "username") String username) throws ApiException {
        User user = userService.getUser(username);
        ValidateUtil.isNull(user, "参数错误，用户名无效！" + username);
        return RespResult.ok(user);
    }

    @RequiresAuthentication
    @ApiOperation("搜索用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", dataTypeClass = String.class, value = "关键字"),
            @ApiImplicitParam(name = "page", dataTypeClass = Integer.class, value = "第几页", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", dataTypeClass = Integer.class, value = "每页多少项", defaultValue = "20")
    })
    @PostMapping("/search")
    public Result<PageInfo<User>> users(String keyword, Integer state, Integer page, Integer pageSize) {
        if (StrUtil.isBlank(keyword)) {
            keyword = null;
        }
        PageInfo<User> users = userService.searchUser(keyword, state , page, pageSize);
        return RespResult.ok(users);
    }

    @PutMapping
    public Result<User> update(User user) throws ApiException {
        ValidateUtil.isNull(user,"参数错误，参数值为空");
        ValidateUtil.isNull(user.getId(),"参数错误，用户ID为空");
        user.setState(null);
        User user1 = userService.updateUser(user);
        if (null == user1){
            return RespResult.fail("修改失败");
        }
        return RespResult.ok(user1);
    }

    @PostMapping
    public Result<User> add(User user) throws ApiException {
        ValidateUtil.isNull(user,"参数错误，参数值为空");
        String username = user.getUsername();
        ValidateUtil.isEmpty(username,"参数错误，用户名不能为空");
        User register = userService.register(username, DEFAULT_PASSWORD);
        if (null != register){
            user.setId(register.getId());
            User user1 = userService.updateUser(user);
            return RespResult.ok(user1);
        }
        return RespResult.fail("添加失败");
    }

    @PutMapping("/freez/{id}")
    public Result<User> freez(@PathVariable("id") Long id){
        User user = new User();
        user.setId(id);
        user.setState(Constant.USER_STATE_FREEZ);
        User user1 = userService.updateUser(user);
        return RespResult.ok(user1);
    }

    @PutMapping("/unfreez/{id}")
    public Result<User> unfreez(@PathVariable("id") Long id){
        User user = new User();
        user.setId(id);
        user.setState(Constant.USER_STATE_NORMAL);
        User user1 = userService.updateUser(user);
        return RespResult.ok(user1);
    }

    @PutMapping("/{id}")
    public Result<User> remove(@PathVariable("id") Long id){
        User user = new User();
        user.setId(id);
        user.setState(Constant.USER_STATE_DELETE);
        User user1 = userService.updateUser(user);
        return RespResult.ok(user1);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable("id") Long id){
        Boolean aBoolean = userService.delete(id);
        return RespResult.ok(aBoolean);
    }
}
