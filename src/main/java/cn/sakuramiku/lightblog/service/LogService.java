package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Log;

import java.util.List;

/**
 * 日志服务接口
 * @author lyy
 */
public interface LogService {

    /**
     * 写日志
     * @param log
     * @return
     */
    Log writeLog(Log log);

    /**
     * 获取reference关联的日志
     * @param reference
     * @return
     */
    List<Log> getLogs(String reference,int page,int pageSize);
}
