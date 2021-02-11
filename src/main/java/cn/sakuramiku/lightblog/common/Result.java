package cn.sakuramiku.lightblog.common;

import cn.sakuramiku.lightblog.common.util.RespCode;

import java.io.Serializable;

/**
 * 响应结果接口
 *
 * @author lyy
 */
public interface Result<T> extends Serializable {

    /**
     * 获取响应码
     *
     * @return code
     */
    int getCode();

    /**
     * 获取响应信息
     *
     * @return msg
     */
    String getMsg();

    /**
     * 获取响应内容
     *
     * @return T
     */
    T getContent();

    /**
     * 成功状态
     *
     * @return
     */
    Result<T> okResp();

    /**
     * 失败状态
     *
     * @return
     */
    Result<T> failResp();

    /**
     * 构建响应消息
     *
     * @param msg 响应消息
     * @return
     */
    Result<T> msg(String msg);

    /**
     * 构建响应码
     *
     * @param code
     * @return
     */
    Result<T> code(RespCode code);

    /**
     * 构建响应内容
     *
     * @param content
     * @return
     */
    Result<T> content(T content);
}
