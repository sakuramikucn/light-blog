package cn.sakuramiku.lightblog.controller;

import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.common.util.ValidateUtil;
import cn.sakuramiku.lightblog.entity.Role;
import cn.sakuramiku.lightblog.service.RoleService;
import cn.sakuramiku.lightblog.vo.SearchRoleParam;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 角色模块方法集
 *
 * @author lyy
 */
@Api(tags = "角色模块方法集")
@CrossOrigin("*")
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private RoleService roleService;

//    @RequiresRoles(Constant.ROLE_ADMIN)
    @ApiOperation("添加角色")
    @PostMapping
    public Result<Long> add(@Validated Role role) {
        Long id = roleService.saveRole(role);
        return RespResult.ok(id);
    }

    @RequiresAuthentication
    @ApiOperation("获取角色")
    @GetMapping("/{id}")
    public Result<Role> get(@PathVariable("id") Long id) throws ApiException {
        ValidateUtil.isNull(id, "参数异常，角色ID为空");
        Role role = roleService.getRole(id);
        return RespResult.ok(role);
    }

    @RequiresAuthentication
    @ApiOperation("搜索角色")
    @GetMapping("/search")
    public Result<PageInfo<Role>> search(SearchRoleParam param) {
        Long userId = param.getUserId();
        String keyword = param.getKeyword();
        Integer page = param.getPage();
        Integer pageSize = param.getPageSize();
        PageInfo<Role> roles = roleService.searchRole(userId, keyword, page, pageSize);
        return RespResult.ok(roles);
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable("id") Long id) throws ApiException {
        ValidateUtil.isNull(id, "参数异常，角色ID为空");
        return RespResult.ok(roleService.removeRole(id, null));
    }
}
