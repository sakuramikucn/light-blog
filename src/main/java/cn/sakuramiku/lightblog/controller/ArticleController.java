package cn.sakuramiku.lightblog.controller;

import cn.sakuramiku.lightblog.annotation.ShiroPass;
import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.RedisUtil;
import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.common.util.ValidateUtil;
import cn.sakuramiku.lightblog.entity.Article;
import cn.sakuramiku.lightblog.entity.Comment;
import cn.sakuramiku.lightblog.exception.BusinessException;
import cn.sakuramiku.lightblog.service.ArticleService;
import cn.sakuramiku.lightblog.service.CommentService;
import cn.sakuramiku.lightblog.util.BlogHelper;
import cn.sakuramiku.lightblog.util.Constant;
import cn.sakuramiku.lightblog.vo.*;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Map;
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
        Article article = articleService.getArticle(id);
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
            Boolean simple = param.getSimple();
            if (simple){
                SimpleArticleView view = new SimpleArticleView();
                return view.valueOf(article, 0L, 0);
            }
            Long id = article.getId();
            PageInfo<Comment> comments = commentService.searchComment(Constant.COMMENT_STATE_NORMAL, id.toString(),
                    null,null, Constant.COMMENT_TYPE_ARTICLE, null, null);
            Object o = redisUtil.get(Constant.PREFIX_ARTICLE_VIEWS + id);
            Long pageViews = null == o ? 0L : Long.parseLong(o.toString());
            SimpleArticleView view = new SimpleArticleView();
            return view.valueOf(article, pageViews, comments.getTotal());
        }).collect(Collectors.toList());
        return RespResult.ok(BlogHelper.toPageInfo(articles, views));
    }

    @RequiresAuthentication
    @ApiOperation("搜索文章")
    @PostMapping("/list")
    public Result<PageInfo<ArticleView>> list(@RequestBody SearchArticleParam param) {
        PageInfo<Article> articles = articleService.searchArticle(param);
        List<ArticleView> views = articles.getList().parallelStream().map(article -> {
            Boolean simple = param.getSimple();
            if (simple){
                return ArticleView.valueOf(article, 0L, 0L);
            }
            Long id = article.getId();
            PageInfo<Comment> comments = commentService.searchComment(Constant.COMMENT_STATE_NORMAL, id.toString(), null, null, Constant.COMMENT_TYPE_ARTICLE , null, null);
            Object o = redisUtil.get(Constant.PREFIX_ARTICLE_VIEWS + id);
            Long pageViews = null == o ? 0L : Long.parseLong(o.toString());
            return ArticleView.valueOf(article, pageViews, comments.getTotal());
        }).collect(Collectors.toList());
        return RespResult.ok(BlogHelper.toPageInfo(articles, views));
    }


    @ShiroPass
    @ApiOperation("搜索文章")
    @PostMapping("/query/tag")
    public Result<PageInfo<ArticleView>> queryByTag(@RequestBody QueryArticleByTag param) {
        PageInfo<Article> articles = articleService.queryByTag(param);
        List<ArticleView> views = articles.getList().parallelStream().map(article -> {
            Boolean simple = param.getSimple();
            if (simple){
                return ArticleView.valueOf(article, 0L, 0L);
            }
            Long id = article.getId();
            PageInfo<Comment> comments = commentService.searchComment(Constant.COMMENT_STATE_NORMAL, id.toString(), null, null, Constant.COMMENT_TYPE_ARTICLE , null, null);
            Object o = redisUtil.get(Constant.PREFIX_ARTICLE_VIEWS + id);
            Long pageViews = null == o ? 0L : Long.parseLong(o.toString());
            return ArticleView.valueOf(article, pageViews, comments.getTotal());
        }).collect(Collectors.toList());
        return RespResult.ok(BlogHelper.toPageInfo(articles, views));
    }


    @RequiresAuthentication
    @ApiOperation("添加文章")
    @PostMapping
    public Result<Article> create(@Validated @RequestBody Article article) throws BusinessException {
        Article article1 = articleService.saveArticle(article);
        if (null == article1){
            return RespResult.fail("添加失败");
        }
        return RespResult.ok(article1);
    }

    @RequiresRoles(Constant.ROLE_ADMIN)
    @ApiOperation("修改文章")
    @PutMapping
    public Result<Article> update(@RequestBody Article article) throws BusinessException {
        Article article1 = articleService.updateArticle(article);
        if (null == article1){
            return RespResult.fail("修改失败");
        }
        return RespResult.ok(article1);
    }

    @RequiresRoles(Constant.ROLE_ADMIN)
    @ApiOperation("移动到回收站")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataTypeClass = Long.class, value = "文章ID", required = true)
    })
    @PutMapping("/{id}")
    public Result<Article> remove(@PathVariable("id") Long id) throws ApiException {
        ValidateUtil.isNull(id, "参数异常，文章ID为空");
        Article article1 = articleService.removeArticle(id);
        if (null == article1){
            return RespResult.fail("删除失败");
        }
        return RespResult.ok(article1);
    }

    @RequiresRoles(Constant.ROLE_ADMIN)
    @ApiOperation("删除文章")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataTypeClass = Long.class, value = "文章ID", required = true)
    })
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable("id") Long id) throws ApiException {
        ValidateUtil.isNull(id, "参数异常，文章ID为空");
        Boolean succ = articleService.deleteArticle(id);
        return RespResult.ok(succ);
    }

    @RequiresRoles(Constant.ROLE_ADMIN)
    @ApiOperation("恢复文章")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataTypeClass = Long.class, value = "文章ID", required = true)
    })
    @PutMapping("/restore/{id}")
    public Result<Boolean> restore(@PathVariable("id") Long id) throws ApiException {
        ValidateUtil.isNull(id, "参数异常，文章ID为空");
        Boolean succ = articleService.restoreForRecycle(id);
        return RespResult.ok(succ);
    }

    @GetMapping("/archives")
    public Result<List<ArticleArchivesView>> archives(){
        SearchArticleParam param = new SearchArticleParam();
        param.setState(Constant.ARTICLE_STATE_NORMAL);
        param.setPublic(true);
        PageInfo<Article> searchArticle = articleService.searchArticle(param);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月");
        Map<String, List<SimpleArticleView>> collect = searchArticle.getList().parallelStream().map(article -> {
            Long id = article.getId();
            PageInfo<Comment> comments = commentService.searchComment(Constant.COMMENT_STATE_NORMAL, id.toString(),
                    null, null, Constant.COMMENT_TYPE_ARTICLE, null, null);
            Object o = redisUtil.get(Constant.PREFIX_ARTICLE_VIEWS + id);
            Long pageViews = null == o ? 0L : Long.parseLong(o.toString());
            SimpleArticleView view = new SimpleArticleView();
            return view.valueOf(article, pageViews, comments.getTotal());
        }).collect(Collectors.groupingBy(o -> formatter.format(o.getCreateTime())));

        List<ArticleArchivesView> articleArchivesViewList = collect.entrySet().parallelStream().map(entry -> {
            ArticleArchivesView view = new ArticleArchivesView();
            view.setArticles(entry.getValue());
            view.setKey(entry.getKey());
            TemporalAccessor parse = formatter.parse(entry.getKey());
            view.setYear(parse.get(ChronoField.YEAR));
            view.setMonth(parse.get(ChronoField.MONTH_OF_YEAR));
            return view;
        }).collect(Collectors.toList());

        return RespResult.ok(articleArchivesViewList);

    }
}

