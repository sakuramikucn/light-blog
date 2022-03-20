package cn.sakuramiku.lightblog.controller;

import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.common.util.ValidateUtil;
import cn.sakuramiku.lightblog.entity.Right;
import cn.sakuramiku.lightblog.exception.BusinessException;
import cn.sakuramiku.lightblog.service.RightService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 权限模块方法集
 *
 * @author lyy
 */
@Api(tags = "权限模块方法集")
@CrossOrigin("*")
@RestController
@RequestMapping("/right")
public class RightController {

    @Resource
    private RightService rightService;

    @RequiresPermissions(value = {"right","right:add"},logical = Logical.OR)
    @ApiOperation("添加权限")
    @PostMapping
    public Result<Right> add(@RequestBody Right right) {
        Right right1 = rightService.saveRight(right);
        if (null == right1){
            return RespResult.fail("添加权限失败");
        }
        return RespResult.ok(right1);
    }

    @RequiresAuthentication
    @ApiOperation("搜索权限")
    @GetMapping("/search")
    public Result<PageInfo<Right>> search(Long roleId,String keyword, Integer page, Integer pageSize) {
        PageInfo<Right> rights;
        if (null != roleId) {
            rights = rightService.searchRight(roleId, null, page, pageSize);
        } else {
            rights = rightService.findRight(keyword, page, pageSize);
        }
        return RespResult.ok(rights);
    }

    @RequiresPermissions(value = {"right","right:delete"},logical = Logical.OR)
    @ApiOperation("删除权限")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable("id") Long id) throws ApiException, BusinessException {
        ValidateUtil.isNull(id, "参数异常，ID为空");
        return RespResult.ok(rightService.removeRight(id));
    }

    @RequiresPermissions(value = {"right","right:update"},logical = Logical.OR)
    @ApiOperation("修改权限")
    @PutMapping
    public Result<Right> update(@RequestBody Right right){
        Right right1 = rightService.updateRight(right);
        if (null == right1){
            return RespResult.fail("修改权限失败");
        }
        return RespResult.ok(right1);
    }

    @RequiresAuthentication
    @ApiOperation("检查权限名称")
    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam("name") String name) throws ApiException {
        ValidateUtil.isEmpty(name,"名称不能为空");
        Right right1 = rightService.getRightByName(name.trim());
        if (null != right1){
            return RespResult.ok(false);
        }
        return RespResult.ok(true);
    }
}
