package cn.sakuramiku.lightblog.controller;

import cn.sakuramiku.lightblog.annotation.ShiroPass;
import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.common.util.ValidateUtil;
import cn.sakuramiku.lightblog.entity.FriendLink;
import cn.sakuramiku.lightblog.service.FriendLinkService;
import cn.sakuramiku.lightblog.util.Constant;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 友链模块方法集
 *
 * @author lyy
 */
@Api(tags = "友链模块方法集")
@CrossOrigin("*")
@RequestMapping("/link")
@RestController
public class LinkController {

    @Resource
    private FriendLinkService linkService;

    @RequiresAuthentication
    @ApiOperation("添加友链")
    @PostMapping
    public Result<Long> add(@Validated FriendLink link) {
        Long id = linkService.saveLink(link);
        return RespResult.ok(id);
    }

    @ShiroPass
    @ApiOperation("搜索友链")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", dataTypeClass = String.class, value = "关键字")
    })
    @GetMapping("/search")
    public Result<PageInfo<FriendLink>> search(String keyword, Integer page, Integer pageSize) {
        PageInfo<FriendLink> links = linkService.searchLink(keyword, page, pageSize);
        return RespResult.ok(links);
    }

    @RequiresRoles(Constant.ROLE_ADMIN)
    @ApiOperation("删除友链")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataTypeClass = Long.class, value = "友链ID", required = true)
    })
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable("id") Long id) throws ApiException {
        ValidateUtil.isNull(id, "参数异常，ID为空");
        return RespResult.ok(linkService.removeLink(id));
    }

}
