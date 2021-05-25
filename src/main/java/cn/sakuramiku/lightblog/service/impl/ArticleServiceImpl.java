package cn.sakuramiku.lightblog.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.annotation.*;
import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.IdGenerator;
import cn.sakuramiku.lightblog.common.util.SpringContextUtil;
import cn.sakuramiku.lightblog.entity.Article;
import cn.sakuramiku.lightblog.entity.Tag;
import cn.sakuramiku.lightblog.exception.BusinessException;
import cn.sakuramiku.lightblog.mapper.ArticleMapper;
import cn.sakuramiku.lightblog.model.BatchInsertParam;
import cn.sakuramiku.lightblog.service.ArticleService;
import cn.sakuramiku.lightblog.service.TagService;
import cn.sakuramiku.lightblog.util.Constant;
import cn.sakuramiku.lightblog.util.JwtUtil;
import cn.sakuramiku.lightblog.vo.QueryArticleByTagParam;
import cn.sakuramiku.lightblog.vo.SearchArticleParam;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.jsonwebtoken.Claims;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章服务
 *
 * @author lyy
 */
@LogConfig(reference = "#result.id", category = "article", name = "文章")
@RedisCacheConfig(cacheName = "light_blog:article")
@Service
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private TagService tagService;
    @Resource
    @Lazy
    private ArticleService articleService;

    @RedisCachePut(key = "id")
    @WriteLog(action = WriteLog.Action.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Article saveArticle(@NonNull Article article) throws BusinessException {
        long id = IdGenerator.nextId();
        article.setId(id);
        article.setCreateTime(LocalDateTime.now());
        Subject subject = SecurityUtils.getSubject();
        String token = (String) subject.getPrincipal();
        Claims claims = JwtUtil.getClaims(token);
        assert claims != null;
        String username = String.valueOf(claims.get("username"));
        String nickName = String.valueOf(claims.get("nickName"));
        String authorId = String.valueOf(claims.get("id"));
        article.setAuthor(username);
        article.setAuthorId(authorId);
        article.setState(Constant.ARTICLE_STATE_NORMAL);
        if (!StrUtil.isBlank(nickName)) {
            article.setAuthor(nickName);
        }
        // 添加文章
        Boolean succ = articleMapper.insert(article);
        List<Tag> tags = article.getTags();
        // 添加标签
        if (succ && !CollectionUtil.isEmpty(tags)) {
            List<BatchInsertParam> insertParams = tags.parallelStream().map(tag -> BatchInsertParam.valueOf(id, tag.getId())).collect(Collectors.toList());
            Boolean aBoolean = tagService.batchInsert(insertParams);
            if (aBoolean) {
                return articleMapper.get(id);
            } else {
                throw new BusinessException("添加文章失败");
            }
        }
        return null;
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @RedisCachePut(key = "#article.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Article updateArticle(@NonNull Article article) throws BusinessException {
        // 更新mask
        Boolean aPublic = article.isPublic();
        article.setMask(aPublic ? 1 : 0);
        //
        Boolean update = articleMapper.update(article);
        List<Tag> tags = article.getTags();
        if (update) {
            if (null != tags) {
                // 更新标签
                tagService.deleteForArticle(article.getId());
                List<BatchInsertParam> insertParams = article.getTags().parallelStream()
                        .map(tag -> BatchInsertParam.valueOf(article.getId(), tag.getId())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(insertParams)) {
                    Boolean aBoolean = tagService.batchInsert(insertParams);
                    if (!aBoolean) {
                        throw new BusinessException("修改失败");
                    }
                }
            }
            return articleMapper.get(article.getId());
        }
        return null;
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @RedisCacheDelete(key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Article removeArticle(@NonNull Long id) {
        Article article = new Article();
        article.setId(id);
        article.setState(Constant.ARTICLE_STATE_DELETE);
        article.setMarkDelTime(LocalDateTime.now());
        Boolean update = articleMapper.update(article);
        if (update) {
            return articleMapper.get(id);
        }
        return null;
    }

    @RedisCacheDelete(key = "id")
    @WriteLog(action = WriteLog.Action.DELETE)
    @Override
    public Boolean deleteArticle(@NonNull Long id) {
        return articleMapper.delete(id);
    }

    @RedisCache(key = "#id")
    @Override
    public Article getArticle(@NonNull Long id) {
        return articleMapper.get(id);
    }

    @OnCacheChange
    @RedisCache
    @Override
    public PageInfo<Article> searchArticle(@NonNull SearchArticleParam param) {

        if (param.getPage() != null && param.getPageSize() != null) {
            Page<Article> page = PageHelper.startPage(param.getPage(), param.getPageSize(), true);
            String orderby = "";
            Boolean hotOrderBy = param.getHotOrderBy();
            if (hotOrderBy) {
                orderby += "article.page_views DESC";
            }else {
                orderby = "article.create_time " + param.getOrder();
            }
            page.setOrderBy(orderby);
        }
        String keyword = param.getKeyword();
        if (StrUtil.isBlank(keyword)) {
            param.setKeyword(null);
        }
        if (null != param.getPublic()) {
            Boolean isPublic = param.getPublic();
            param.setMask(isPublic ? 1 : 0);
        }
        List<Article> articles = articleMapper.search(param);
        PageInfo<Article> of = PageInfo.of(articles);
//        if (null != param.getPublic()) {
//            articles = articles.stream().filter(val -> param.getPublic().equals(val.getPublic())).collect(Collectors.toList());
//        }
//        long offset = of.getTotal() - articles.size();
//        of.setList(articles);
//        long total = of.getTotal();
//        of.setTotal(total-offset);
        return of;
    }

    @Transactional(rollbackFor = Exception.class)
    @RedisClean
    @WriteLog(action = WriteLog.Action.DELETE, result = true)
    @Override
    public Long deleteForRecycle(Integer status, Date start) {
        return articleMapper.deleteForRecycle(status, start);
    }

    @Transactional(rollbackFor = Exception.class)
    @RedisCachePut(key = "id")
    @Override
    public Boolean restoreForRecycle(Long id) {
        Article article = new Article();
        article.setId(id);
        article.setState(Constant.ARTICLE_STATE_NORMAL);
        return articleMapper.update(article);
    }

    @RedisCache
    @OnCacheChange
    @Override
    public PageInfo<Article> queryByTag(QueryArticleByTagParam param) {
        Integer page = param.getPage();
        Integer pageSize = param.getPageSize();
        Long tagId = param.getTagId();
        Boolean aPublic = param.getPublic();
        Integer mask = aPublic ? 1 : 0;
        if (null != page && null != pageSize) {
            PageHelper.startPage(page, pageSize);
        }
        List<Long> ids = articleMapper.queryByTag(tagId);
        ArticleService bean = SpringContextUtil.getBean(ArticleService.class);
        assert bean != null;
        List<Article> articles = ids.parallelStream().map(bean::getArticle).filter(val -> mask.equals(val.getMask()))
                .sorted(Comparator.comparing(Article::getCreateTime)).collect(Collectors.toList());
        return PageInfo.of(articles);
    }

    @Transactional(rollbackFor = Exception.class)
    @RedisCachePut(key = "id")
    @Override
    public Article changeMask(Article article) {
        // 更新mask
        Boolean aPublic = article.isPublic();
        article.setMask(aPublic ? 1 : 0);
        Boolean update = articleMapper.update(article);
        if (update) {
            return article;
        }
        return null;
    }

}
