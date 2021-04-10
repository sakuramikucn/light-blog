package cn.sakuramiku.lightblog.controller;

import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.common.util.ValidateUtil;
import cn.sakuramiku.lightblog.entity.Right;
import cn.sakuramiku.lightblog.exception.BusinessException;
import cn.sakuramiku.lightblog.service.RightService;
import cn.sakuramiku.lightblog.util.Constant;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
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

//    @RequiresRoles(Constant.ROLE_ADMIN)
    @ApiOperation("添加权限")
    @PostMapping
    public Result<Right> add(Right right) {
        Right right1 = rightService.saveRight(right);
        if (null == right1){
            return RespResult.fail("添加权限失败");
        }
        return RespResult.ok(right1);
    }

    @RequiresAuthentication
    @ApiOperation("搜索权限")
    @GetMapping("/search")
    public Result<PageInfo<Right>> search(Long roleId, Integer page, Integer pageSize) {
        PageInfo<Right> rights;
        if (null != roleId) {
            rights = rightService.searchRight(roleId, null, page, pageSize);
        } else {
            rights = rightService.findRight(null, page, pageSize);
        }
        return RespResult.ok(rights);
    }

    @RequiresRoles(Constant.ROLE_ADMIN)
    @ApiOperation("删除权限")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable("id") Long id) throws ApiException, BusinessException {
        ValidateUtil.isNull(id, "参数异常，ID为空");
        return RespResult.ok(rightService.removeRight(id));
    }
}
