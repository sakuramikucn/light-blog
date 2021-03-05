package cn.sakuramiku.lightblog.controller;

import cn.sakuramiku.lightblog.annotation.ShiroPass;
import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.common.util.ValidateUtil;
import cn.sakuramiku.lightblog.entity.Tag;
import cn.sakuramiku.lightblog.service.TagService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 标签模块方法集
 *
 * @author lyy
 */
@Api(tags = "标签模块方法集")
@CrossOrigin("*")
@RestController
@RequestMapping("/tag")
public class TagController {

    @Resource
    private TagService tagService;

    @RequiresAuthentication
    @ApiOperation("添加标签")
    @PostMapping
    public Result<Long> add(String name) throws ApiException {
        ValidateUtil.isEmpty(name, "参数错误，标签名称为空");
        Long id = tagService.saveTag(name);
        return RespResult.ok(id);
    }

    @ShiroPass
    @ApiOperation("搜索标签")
    @GetMapping("/search")
    public Result<PageInfo<Tag>> search(Long articleId, String keyword, Integer page, Integer pageSize) {
        PageInfo<Tag> tags = tagService.search(articleId, keyword, page, pageSize);
        return RespResult.ok(tags);
    }
}
