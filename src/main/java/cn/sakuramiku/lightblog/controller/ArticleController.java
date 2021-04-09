package cn.sakuramiku.lightblog.controller;

import cn.sakuramiku.lightblog.annotation.ShiroPass;
import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.RedisUtil;
import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.common.util.ValidateUtil;
import cn.sakuramiku.lightblog.entity.Article;
import cn.sakuramiku.lightblog.entity.Comment;
import cn.sakuramiku.lightblog.service.ArticleService;
import cn.sakuramiku.lightblog.service.CommentService;
import cn.sakuramiku.lightblog.util.BlogHelper;
import cn.sakuramiku.lightblog.util.Constant;
import cn.sakuramiku.lightblog.vo.ArticleView;
import cn.sakuramiku.lightblog.vo.SearchArticleParam;
import cn.sakuramiku.lightblog.vo.SimpleArticleView;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章模块方法集
 *
 * @author lyy
 */
@CrossOrigin("*")
@Api(tags = "文章方法集")
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;
    @Resource
    private CommentService commentService;
    @Resource
    private RedisUtil redisUtil;

    @ShiroPass
    @ApiOperation("获取文章")
    @ApiImplicitParam(name = "id", dataTypeClass = Long.class, value = "文章ID")
    @GetMapping("/{id}")
    public Result<Article> get(@PathVariable(value = "id") Long id) throws ApiException {
        ValidateUtil.isNull(id, "参数异常，文章ID为空");
        Article article =articleService.getArticle(id);
        ValidateUtil.isNull(article, "参数异常，文章ID无效");
        return RespResult.ok(article);
    }

    @ShiroPass
    @ApiOperation("搜索文章")
    @GetMapping("/search")
    public Result<PageInfo<SimpleArticleView>> search(SearchArticleParam param) {
        param.setState(1);
        PageInfo<Article> articles = articleService.searchArticle(param);
        List<SimpleArticleView> views = articles.getList().parallelStream().map(article -> {
            Long id = article.getId();
            PageInfo<Comment> comments = commentService.searchComment(Constant.COMMENT_STATE_NORMAL, id.toString(), null, null, null);
            Object o = redisUtil.get(Constant.PREFIX_ARTICLE_VIEWS + id);
            Long pageViews = null == o ? 0L : Long.parseLong(o.toString());
            SimpleArticleView view = new SimpleArticleView();
            return view.valueOf(article, pageViews, comments.getTotal());
        }).collect(Collectors.toList());
        return RespResult.ok(BlogHelper.toPageInfo(articles,views));
    }

    @RequiresAuthentication
    @ApiOperation("搜索文章")
    @PostMapping("/list")
    public Result<PageInfo<ArticleView>> list(SearchArticleParam param) {
        PageInfo<Article> articles = articleService.searchArticle(param);
        List<ArticleView> views = articles.getList().parallelStream().map(article -> {
            Long id = article.getId();
            PageInfo<Comment> comments = commentService.searchComment(Constant.COMMENT_STATE_NORMAL, id.toString(), null, null, null);
            Object o = redisUtil.get(Constant.PREFIX_ARTICLE_VIEWS + id);
            Long pageViews = null == o ? 0L : Long.parseLong(o.toString());
            return ArticleView.valueOf(article,pageViews,comments.getTotal());
        }).collect(Collectors.toList());
        return RespResult.ok(BlogHelper.toPageInfo(articles,views));
    }

    @RequiresAuthentication
    @ApiOperation("添加文章")
    @PostMapping
    public Result<Long> create(@Validated Article article){
        Long id = articleService.saveArticle(article);
        return RespResult.ok(id);
    }

    @RequiresRoles(Constant.ROLE_ADMIN)
    @ApiOperation("修改文章")
    @ApiResponse(code = 0, message = "ok", examples = @Example(@ExampleProperty(mediaType = "文章ID", value = "文章ID")))
    @PutMapping
    public Result<Boolean> update(@Validated Article article){
        Boolean succ = articleService.updateArticle(article);
        return RespResult.ok(succ);
    }

    @RequiresRoles(Constant.ROLE_ADMIN)
    @ApiOperation("移动到回收站")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",dataTypeClass = Long.class,value = "文章ID",required = true)
    })
    @PutMapping("/{id}")
    public Result<Boolean> remove(@PathVariable("id") Long id) throws ApiException {
        ValidateUtil.isNull(id,"参数异常，文章ID为空");
        Boolean succ = articleService.removeArticle(id);
        return RespResult.ok(succ);
    }

    @RequiresRoles(Constant.ROLE_ADMIN)
    @ApiOperation("删除文章")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",dataTypeClass = Long.class,value = "文章ID",required = true)
    })
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable("id") Long id) throws ApiException {
        ValidateUtil.isNull(id,"参数异常，文章ID为空");
        Boolean succ = articleService.deleteArticle(id);
        return RespResult.ok(succ);
    }


}

