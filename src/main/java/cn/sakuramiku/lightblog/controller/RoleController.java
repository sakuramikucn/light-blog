package cn.sakuramiku.lightblog.controller;

import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.common.util.ValidateUtil;
import cn.sakuramiku.lightblog.entity.Role;
import cn.sakuramiku.lightblog.exception.BusinessException;
import cn.sakuramiku.lightblog.service.RoleService;
import cn.sakuramiku.lightblog.util.Constant;
import cn.sakuramiku.lightblog.vo.SearchRoleParam;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
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

    @RequiresRoles(Constant.ROLE_ADMIN)
    @ApiOperation("添加角色")
    @PostMapping
    public Result<Role> add(@Validated @RequestBody Role role) {
        Role role1 = roleService.saveRole(role);
        if (null == role1){
            return RespResult.fail("添加角色失败");
        }
        return RespResult.ok(role1);
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
    @PostMapping("/search")
    public Result<PageInfo<Role>> search(@RequestBody SearchRoleParam param) {
        Long userId = param.getUserId();
        String keyword = param.getKeyword();
        Integer page = param.getPage();
        Integer pageSize = param.getPageSize();
        PageInfo<Role> roles = roleService.searchRole(userId, keyword, page, pageSize);
        return RespResult.ok(roles);
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable("id") Long id) throws ApiException, BusinessException {
        ValidateUtil.isNull(id, "参数异常，角色ID为空");
        return RespResult.ok(roleService.removeRole(id));
    }

    @PutMapping
    public Result<Role> edit(@RequestBody Role role) throws BusinessException {
        Role role1 = roleService.updateRole(role);
        if (null == role1){
            return RespResult.fail("修改角色失败");
        }
        return RespResult.ok(role1);
    }

    @ApiOperation("检查权限名称")
    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam("name") String name) throws ApiException {
        ValidateUtil.isEmpty(name,"名称不能为空");
        Role roleByName = roleService.getRoleByName(name);
        if (null != roleByName){
            return RespResult.ok(false);
        }
        return RespResult.ok(true);
    }
}
