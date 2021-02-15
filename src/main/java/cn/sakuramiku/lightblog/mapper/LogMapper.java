package cn.sakuramiku.lightblog.mapper;

import cn.sakuramiku.lightblog.entity.Log;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日志SQL映射
 *
 * @author LYY
 */
public interface LogMapper {

    /**
     * 删除日志
     *
     * @param id 日志ID
     * @return 是否成功
     */
    Boolean delete(@NonNull Long id);

    /**
     * 添加日志
     *
     * @param record 日志
     * @return 是否成功
     */
    Boolean insert(@NonNull Log record);

    /**
     * 获取日志
     *
     * @param id 日志ID
     * @return 日志
     */
    Log get(@NonNull Long id);

    /**
     * 搜索日志
     *
     * @param ref   引用，一般为被记录对象的ID
     * @param begin 开始时间
     * @param end   截止时间
     * @return 日志列表
     */
    List<Log> search(@Nullable @Param("ref") String ref,
                     @Nullable @Param("begin") LocalDateTime begin,
                     @Nullable @Param("end") LocalDateTime end);
}