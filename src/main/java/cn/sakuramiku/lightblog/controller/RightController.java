package cn.sakuramiku.lightblog.controller;

import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.common.util.ValidateUtil;
import cn.sakuramiku.lightblog.entity.Right;
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

    @RequiresRoles(Constant.ROLE_ADMIN)
    @ApiOperation("添加权限")
    @PostMapping
    public Result<Long> add(Right right) throws ApiException {
        ValidateUtil.isEmpty(right.getReference(), "参数异常，引用标识为空");
        Long id = rightService.saveRight(right);
        return RespResult.ok(id);
    }

    @RequiresAuthentication
    @ApiOperation("搜索权限")
    @GetMapping("/search")
    public Result<PageInfo<Right>> search(String ref, Integer page, Integer pageSize) {
        PageInfo<Right> rights = rightService.searchRight(ref, page, pageSize);
        return RespResult.ok(rights);
    }

    @RequiresRoles(Constant.ROLE_ADMIN)
    @ApiOperation("删除权限")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable("id") Long id) throws ApiException {
        ValidateUtil.isNull(id, "参数异常，ID为空");
        return RespResult.ok(rightService.removeRight(id, null));
    }
}
