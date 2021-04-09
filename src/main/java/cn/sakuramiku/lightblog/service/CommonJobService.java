package cn.sakuramiku.lightblog.service;

/**
 * 公共服务
 * @author lyy
 */
public interface CommonJobService {

    /**
     * 发送邮件
     */
    void sendEmail();

    /**
     * 定时同步文章浏览数
     */
    void syncPageViews();
    
}
