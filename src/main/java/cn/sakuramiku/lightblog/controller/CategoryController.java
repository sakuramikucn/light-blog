package cn.sakuramiku.lightblog.controller;

import cn.sakuramiku.lightblog.annotation.ShiroPass;
import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.common.util.ValidateUtil;
import cn.sakuramiku.lightblog.entity.Category;
import cn.sakuramiku.lightblog.service.CategoryService;
import cn.sakuramiku.lightblog.vo.SearchCategoryParam;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章分类模块方法集
 *
 * @author lyy
 */
@CrossOrigin("*")
@Api(tags = "文章分类模块方法集")
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;


    @ShiroPass
    @ApiOperation("获取分类")
    @GetMapping("/{id}")
    public Result<Category> get(@PathVariable("id") Long id) throws ApiException {
        ValidateUtil.isNull(id, "参数异常，ID为空");
        Category category = categoryService.getCategory(id);
        if (null != category){
            return RespResult.ok(category);
        }
        return RespResult.fail("没有该分类");
    }


    @ShiroPass
    @ApiOperation("搜索分类")
    @PostMapping("/search")
    public Result<PageInfo<Category>> search(@RequestBody SearchCategoryParam param) {
        String keyword = param.getKeyword();
        LocalDateTime begin = param.getBegin();
        LocalDateTime end = param.getEnd();
        Integer page = param.getPage();
        Integer pageSize = param.getPageSize();
        PageInfo<Category> categories = categoryService.search(keyword, begin, end, page, pageSize);
        return RespResult.ok(categories);
    }

    @RequiresPermissions(value = {"category","category:add"},logical = Logical.OR)
    @ApiOperation("添加分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", dataTypeClass = String.class, value = "名称", required = true)
    })
    @PostMapping("/{name}")
    public Result<Category> add(@PathVariable("name") String name) throws ApiException {
        ValidateUtil.isEmpty(name, "参数异常，名称为空");
        Category category = categoryService.saveCategory(name);
        if (null != category){
            return RespResult.ok(category);
        }
        return RespResult.fail("添加失败");
    }

    @RequiresPermissions(value = {"category","category:update"},logical = Logical.OR)
    @ApiOperation("修改分类名称")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataTypeClass = Long.class, value = "分类ID", required = true),
            @ApiImplicitParam(name = "name", dataTypeClass = String.class, value = "名称", required = true)
    })
    @PutMapping("/{id}/{name}")
    public Result<Category> update(@PathVariable("id") Long id, @PathVariable("name") String name) throws ApiException {
        ValidateUtil.isNull(id, "参数异常，分类ID为空");
        ValidateUtil.isEmpty(name, "参数异常，名称为空");
        Category category = categoryService.updateCategory(id, name);
        if (null != category){
            return RespResult.ok(category);
        }
        return RespResult.fail("修改失败");
    }

    @RequiresPermissions(value = {"category","category:delete"},logical = Logical.OR)
    @ApiOperation("删除分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataTypeClass = Long.class, value = "分类ID", required = true)
    })
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable("id") Long id) throws ApiException {
        ValidateUtil.isNull(id, "参数异常，分类ID为空");
        return RespResult.ok(categoryService.removeCategory(id));
    }

    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam("name") String name) throws ApiException {
        ValidateUtil.isEmpty(name,"名称不能为空");
        Category byName = categoryService.getByName(name);
        if (null == byName){
            return RespResult.ok(true);
        }
        return RespResult.ok(false);
    }

    @ShiroPass
    @GetMapping("/hot")
    public Result<List<Category>> hotCategory(){
        List<Category> categories = categoryService.hotCategory();
        return RespResult.ok(categories);
    }
}
