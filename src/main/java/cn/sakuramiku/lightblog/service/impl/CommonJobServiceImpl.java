package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.common.util.RedisUtil;
import cn.sakuramiku.lightblog.entity.Article;
import cn.sakuramiku.lightblog.mapper.ArticleMapper;
import cn.sakuramiku.lightblog.service.CommonJobService;
import cn.sakuramiku.lightblog.util.Constant;
import cn.sakuramiku.lightblog.vo.SearchArticleParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author lyy
 */
@Service
public class CommonJobServiceImpl implements CommonJobService{

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ThreadPoolTaskScheduler taskScheduler;

    private static final Logger logger = LoggerFactory.getLogger(CommonJobServiceImpl.class);

    @PostConstruct
    public void init() {
        taskScheduler.initialize();
        // 初始化数据
        taskScheduler.schedule(this::syncPageViews, new Date(System.currentTimeMillis() + 120 * 1000L));
    }

    @Override
    public void sendEmail() {

    }

    @Async
    @Scheduled(cron = "0 0 0/1 * * *")
    @Override
    public void syncPageViews() {
        List<Article> info = articleMapper.search(new SearchArticleParam());
        logger.info("开始同步文章浏览数,此次任务同步文章总数：{}", info.size());
        long start = System.currentTimeMillis();
        for (Article article : info) {
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
                Boolean aBoolean = articleMapper.update(params);
                logger.info("id：{}，{} => {}，状态：{}", id, article.getPageViews(), views, aBoolean);
            }
        }
        long end = System.currentTimeMillis();
        logger.info("同步结束，总耗时：{} 毫秒", end - start);
    }


}
