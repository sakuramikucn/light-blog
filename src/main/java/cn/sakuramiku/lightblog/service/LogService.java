package cn.sakuramiku.lightblog.service;

import cn.sakuramiku.lightblog.entity.Log;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

/**
 * 日志服务接口
 *
 * @author lyy
 */
public interface LogService {

    /**
     * 写日志
     *
     * @param log 日志
     * @return 日志ID
     */
    Long writeLog(@NonNull Log log);

    /**
     * 写日志
     *
     * @param ref      关联标识
     * @param operator 操作人
     * @param action   行为
     * @param what     做了什么
     * @return 日志ID
     */
    Long writeLog(@NonNull String ref, @NonNull String operator, @NonNull String action, @Nullable String what);

    /**
     * 写日志
     *
     * @param ref      关联标识
     * @param operator 操作人
     * @param action   行为
     * @param what     做了什么
     * @param note     备注
     * @return 日志ID
     */
    Long writeLog(@NonNull String ref, @NonNull String operator, @NonNull String action, @Nullable String what, @Nullable String note);

    /**
     * 获取reference关联的日志
     *
     * @param ref      引用，一般为被记录的对象ID
     * @param page     第几页
     * @param pageSize 每页项数
     * @return 日志列表
     */
    PageInfo<Log> getLogs(@NonNull String ref, @Nullable Integer page, @Nullable Integer pageSize);

    /**
     * 搜索日志
     *
     * @param ref      关联标识
     * @param begin    开始时间
     * @param end      结束时间
     * @param page     第几页
     * @param pageSize 每页项数
     * @return 日志列表
     */
    PageInfo<Log> searchLog(@Nullable String ref, @Nullable LocalDateTime begin, @Nullable LocalDateTime end,
                            @Nullable Integer page, @Nullable Integer pageSize);
}
