package cn.sakuramiku.lightblog.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.common.util.IdUtil;
import cn.sakuramiku.lightblog.entity.Article;
import cn.sakuramiku.lightblog.mapper.ArticleMapper;
import cn.sakuramiku.lightblog.service.ArticleService;
import cn.sakuramiku.lightblog.util.Constant;
import cn.sakuramiku.lightblog.util.JwtUtil;
import cn.sakuramiku.lightblog.vo.SearchArticleParam;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.jsonwebtoken.Claims;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章服务
 *
 * @author lyy
 */
@CacheConfig(cacheNames = "light_blog:article", keyGenerator = "simpleKeyGenerator")
@Service
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveArticle(@NonNull Article article) {
        long id = IdUtil.nextId();
        article.setId(id);
        article.setCreateTime(LocalDateTime.now());
        Subject subject = SecurityUtils.getSubject();
        String token = (String) subject.getPrincipal();
        Claims claims = JwtUtil.getClaims(token);
        assert claims != null;
        String username = String.valueOf(claims.get("username"));
        String nickName = String.valueOf(claims.get("nickName"));
        article.setAuthor(username);
        if (!StrUtil.isBlank(nickName)) {
            article.setAuthor(nickName);
        }
        articleMapper.insert(article);
        return id;
    }

    @CachePut(key = "#article.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateArticle(@NonNull Article article) {
        return articleMapper.update(article);
    }

    @CachePut(key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean removeArticle(@NonNull Long id) {
        Article article = new Article();
        article.setId(id);
        article.setState(Constant.ARTICLE_STATE_DELETE);
        article.setMarkDelTime(LocalDateTime.now());
        return articleMapper.update(article);
    }

    @Cacheable(key = "#id", unless = "null == #result")
    @Override
    public Article getArticle(@NonNull Long id) {
        return articleMapper.get(id);
    }

    @Cacheable(unless = "null == #result.list || 0 == #result.list.size()")
    @Override
    public PageInfo<Article> searchArticle(@NonNull SearchArticleParam param) {
        Page<Article> page = PageHelper.startPage(param.getPage(), param.getPageSize(), true);
        page.setOrderBy("article.create_time " + param.getOrder());
        List<Article> articles = articleMapper.search(param);
        return new PageInfo<>(articles);
    }

}
