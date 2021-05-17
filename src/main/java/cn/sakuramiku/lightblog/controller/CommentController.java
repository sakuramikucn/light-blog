package cn.sakuramiku.lightblog.controller;

import cn.sakuramiku.lightblog.annotation.ShiroPass;
import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.common.util.ValidateUtil;
import cn.sakuramiku.lightblog.entity.Comment;
import cn.sakuramiku.lightblog.service.CommentService;
import cn.sakuramiku.lightblog.vo.CommentWrapView;
import cn.sakuramiku.lightblog.vo.SearchCommentParam;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * 评论模块方法集
 *
 * @author lyy
 */
@Api(tags = "评论模块方法集")
@CrossOrigin("*")
@RequestMapping("/comment")
@RestController
public class CommentController {

    @Resource
    private CommentService commentService;

    @ShiroPass
    @ApiOperation("添加评论")
    @PostMapping
    public Result<Comment> add(@Validated @RequestBody Comment comment) {
        Comment comment1 = commentService.saveComment(comment);
        if (null == comment1){
            return RespResult.fail("添加评论失败");
        }
        return RespResult.ok(comment1);
    }

    @ShiroPass
    @ApiOperation("搜索评论")
    @PostMapping("/search")
    public Result<PageInfo<CommentWrapView>> search(@RequestBody SearchCommentParam param) {
        String ref = param.getRef();
        Long parentId = param.getParentId();
        Integer state = param.getState();
        String keyword = param.getKeyword();
        Integer type = param.getType();
        Integer page = param.getPage();
        Integer pageSize = param.getPageSize();
        PageInfo<CommentWrapView> comments = commentService.searchCommentWrap(state, ref, parentId, keyword, type, page, pageSize);
        if (comments.getTotal() == 0){
            PageInfo<CommentWrapView> info = new PageInfo<>();
            info.setList(new ArrayList<>());
            return RespResult.ok(info);
        }
        return RespResult.ok(comments);
    }

    @RequiresPermissions(value = {"comment","comment:remove"},logical = Logical.OR)
    @ApiOperation("删除评论（逻辑删除）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataTypeClass = Long.class, value = "评论ID", required = true)
    })
    @PutMapping("/{id}")
    public Result<Comment> remove(@PathVariable("id") Long id) throws ApiException {
        ValidateUtil.isNull(id, "参数异常，评论ID为空");
        return RespResult.ok(commentService.removeComment(id));
    }

    @RequiresPermissions(value = {"comment","comment:delete"},logical = Logical.OR)
    @ApiOperation("删除评论（真实删除）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataTypeClass = Long.class, value = "评论ID", required = true)
    })
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable("id") Long id) throws ApiException {
        ValidateUtil.isNull(id, "参数异常，评论ID为空");
        return RespResult.ok(commentService.deleteComment(id));
    }

    @RequiresPermissions(value = {"comment","comment:turnStatus"},logical = Logical.OR)
    @ApiOperation("评论屏蔽操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataTypeClass = Long.class, value = "评论ID", required = true),
            @ApiImplicitParam(name = "isHidden", dataTypeClass = Boolean.class, value = "true=屏蔽，false=正常", required = true)
    })
    @PutMapping("/status/{id}/{isHidden}")
    public Result<Comment> hidden(@PathVariable("id") Long id, @PathVariable("isHidden") Boolean isHidden) throws ApiException {
        ValidateUtil.isNull(isHidden, "参数异常[isHidden]为空");
        ValidateUtil.isNull(id, "参数异常，评论ID为空");
        return RespResult.ok(commentService.hiddenComment(id, isHidden));
    }

    @RequiresPermissions(value = {"comment","comment:turnStatus"},logical = Logical.OR)
    @ApiOperation("评论恢复操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataTypeClass = Long.class, value = "评论ID", required = true),
            @ApiImplicitParam(name = "isHidden", dataTypeClass = Boolean.class, value = "true=屏蔽，false=正常", required = true)
    })
    @PutMapping("/restore/{id}")
    public Result<Boolean> restore(@PathVariable("id") Long id) throws ApiException {
        ValidateUtil.isNull(id, "参数异常，评论ID为空");
        return RespResult.ok(commentService.restoreForRecycle(id));
    }
}
