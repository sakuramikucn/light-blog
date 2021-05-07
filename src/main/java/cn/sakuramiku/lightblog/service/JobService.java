package cn.sakuramiku.lightblog.service;

/**
 * 公共服务
 * @author lyy
 */
public interface JobService {



    /**
     * 定时同步文章浏览数
     */
    void syncPageViews();

    /**
     * 定时删除回收站数据
     */
    void recycleDelete();
    
}
