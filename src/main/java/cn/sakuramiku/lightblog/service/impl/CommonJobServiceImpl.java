package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.common.util.RedisUtil;
import cn.sakuramiku.lightblog.entity.Article;
import cn.sakuramiku.lightblog.service.ArticleService;
import cn.sakuramiku.lightblog.service.CommonJobService;
import cn.sakuramiku.lightblog.util.Constant;
import cn.sakuramiku.lightblog.vo.SearchArticleParam;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;

/**
 * @author lyy
 */
@DependsOn({"pageInterceptor", "articleMapper", "tagServiceImpl"})
@Service
public class CommonJobServiceImpl implements CommonJobService {

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ArticleService articleService;

    @Resource
    private ThreadPoolTaskScheduler taskScheduler;

    private static final Logger logger = LoggerFactory.getLogger(CommonJobServiceImpl.class);

    @PostConstruct
    public void init() {
        taskScheduler.schedule(this::syncPageViews, new Date(System.currentTimeMillis() + 5 * 60 * 1000L));
    }

    @Override
    public void sendEmail() {

    }

    @Scheduled(cron = "0 0 0/1 * * *")
    @Override
    public void syncPageViews() {
        PageInfo<Article> info = articleService.searchArticle(new SearchArticleParam());
        logger.info("开始同步文章浏览数,此次任务同步文章总数：{}", info.getTotal());
        long start = System.currentTimeMillis();
        for (Article article : info.getList()) {
            Long id = article.getId();
            Object o = redisUtil.get(Constant.PREFIX_ARTICLE_VIEWS + id);
            if (null == o) {
                continue;
            }
            Integer views = Integer.parseInt(o.toString());
            if (!article.getPageViews().equals(views)) {
                Article params = new Article();
                params.setId(id);
                params.setPageViews(views);
                Boolean aBoolean = this.articleService.updateArticle(params);
                logger.info("id：{}，{} => {}，状态：{}", id, article.getPageViews(), views, aBoolean);
            }
        }
        long end = System.currentTimeMillis();
        logger.info("同步结束，总耗时：{} 毫秒", end - start);
    }

}
