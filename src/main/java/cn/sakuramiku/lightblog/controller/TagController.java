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
    @PostMapping("/{name}")
    public Result<Tag> add(@PathVariable("name") String name) throws ApiException {
        ValidateUtil.isEmpty(name, "参数错误，标签名称为空");
        Tag tag = tagService.saveTag(name);
        if (null == tag){
            return RespResult.fail("添加标签失败");
        }
        return RespResult.ok(tag);
    }

    @PutMapping
    public Result<Tag> update(@RequestBody Tag tag) throws ApiException {
        ValidateUtil.isNull(tag.getId(), "参数错误，标签名称为空");
        ValidateUtil.isEmpty(tag.getName(), "参数错误，标签名称为空");
        Tag tag1 = tagService.updateTag(tag.getId(),tag.getName());
        if (null == tag1){
            return RespResult.fail("修改标签失败");
        }
        return RespResult.ok(tag1);
    }

    @ShiroPass
    @ApiOperation("搜索标签")
    @PostMapping("/search")
    public Result<PageInfo<Tag>> search(Long articleId, String keyword, Integer page, Integer pageSize) {
        PageInfo<Tag> tags = tagService.search(articleId, keyword, page, pageSize);
        return RespResult.ok(tags);
    }

    @GetMapping("/check/{name}")
    public Result<Boolean> check(@PathVariable("name") String name) throws ApiException {
        ValidateUtil.isEmpty(name,"名称不能为空");
        Tag tagByName = tagService.getTagByName(name);
        if (null == tagByName){
            return RespResult.ok(true);
        }
        return RespResult.ok(false);
    }
}
