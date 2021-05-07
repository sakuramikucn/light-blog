package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.RedisUtil;
import cn.sakuramiku.lightblog.entity.Article;
import cn.sakuramiku.lightblog.mapper.ArticleMapper;
import cn.sakuramiku.lightblog.service.ArticleService;
import cn.sakuramiku.lightblog.service.CommentService;
import cn.sakuramiku.lightblog.service.JobService;
import cn.sakuramiku.lightblog.service.UserService;
import cn.sakuramiku.lightblog.util.Constant;
import cn.sakuramiku.lightblog.vo.SearchArticleParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author lyy
 */
@LogConfig(category = "job", name = "定时任务", reference = "job")
@Service
public class JobServiceImpl implements JobService {

    private final RedisUtil redisUtil;
    private final ArticleMapper articleMapper;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final UserService userService;
    private final CommentService commentService;
    private final ArticleService articleService;

    @Autowired
    public JobServiceImpl(RedisUtil redisUtil,
                          ArticleMapper articleMapper,
                          ThreadPoolTaskScheduler taskScheduler,
                          UserService userService,
                          CommentService commentService,
                          ArticleService articleService) {
        this.redisUtil = redisUtil;
        this.articleMapper = articleMapper;
        this.taskScheduler = taskScheduler;
        this.userService = userService;
        this.commentService = commentService;
        this.articleService = articleService;
    }

    private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    @PostConstruct
    public void init() {
        taskScheduler.initialize();
        // 初始化数据
        taskScheduler.schedule(this::syncPageViews, new Date(System.currentTimeMillis() + 120 * 1000L));
    }

    @Async
    @Scheduled(cron = "0 0/30 * * * *")
    @WriteLog(action = WriteLog.Action.Job, note = "同步文章浏览数，半小时一次")
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

    @Async
    @Scheduled(cron = "0 0/30 * * * *")
    @WriteLog(action = WriteLog.Action.Job,note = "定时清理回收站，半小时一次")
    @Override
    public void recycleDelete() {
        logger.info("清理回收站任务开始");
        long start = System.currentTimeMillis();
        // 7天
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_MONTH,-7);
        Date date = instance.getTime();
        // 文章
        Long aricles = articleService.deleteForRecycle(Constant.ARTICLE_STATE_DELETE, date);
        logger.info("清理文章数：{}", aricles);
        // 用户
        Integer users = userService.deleteForRecycle(Constant.USER_STATE_DELETE, date);
        logger.info("清理用户数：{}", users);
        // 评论
        Long comments = commentService.deleteForRecycle(Constant.COMMENT_STATE_DELETE, date);
        logger.info("清理评论数：{}", comments);
        long end = System.currentTimeMillis();
        logger.info("清理回收站任务结束，总耗时：{} 毫秒", end - start);
    }
}
