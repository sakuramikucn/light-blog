package cn.sakuramiku.lightblog.common.util;


import cn.sakuramiku.lightblog.common.Result;
import org.apache.poi.ss.formula.functions.T;

/**
 * 响应结果实现
 *
 * @author lyy
 */
public class RespResult<T> implements Result<T> {

    /**
     * 响应编码
     */
    private int code;
    /**
     * 响应消息
     */
    private String msg;
    /**
     * 响应内容
     */
    private T content;

    public RespResult() {
        code = RespCode.SUCCESS.getCode();
        msg = RespCode.SUCCESS.getMsg();
    }

    public RespResult(RespCode code){
        this.code = code.getCode();
        this.msg = code.getMsg();
    }

    public RespResult(T content) {
        this(content, RespCode.SUCCESS, RespCode.SUCCESS.getMsg());
    }

    public RespResult(T content, RespCode code, String msg) {
        this.content = content;
        this.code = code.getCode();
        this.msg = msg;
    }

    public RespResult(T content, int code, String msg) {
        this.content = content;
        this.code = code;
        this.msg = msg;
    }

    /**
     * 构建一个空的响应对象,响应码为0（成功状态）
     *
     * @param <T>
     * @return
     */
    public static <T> RespResult<T> build() {
        return new RespResult<>();
    }

    /**
     * 构建一个成功状态码的响应对象
     *
     * @param content 响应内容
     * @param <T>
     * @return
     */
    public static <T> RespResult<T> ok(T content) {
        return new RespResult<>(content);
    }

    /**
     * 构建一个成功状态码的响应对象
     *
     * @param <T>
     * @return
     */
    public static <T> RespResult<T> ok() {
        return new RespResult<>();
    }

    /**
     * 构建一个失败状态码的响应对象
     *
     * @param msg 响应信息
     * @return
     */
    public static <T> RespResult<T> fail(String msg) {
        return new RespResult(null, RespCode.SERVER_INTERNAL_ERROR, msg);
    }

    /**
     * 构建一个失败状态码的响应对象
     *
     * @return
     */
    public static <T> RespResult<T> fail() {
        return new RespResult(null, RespCode.SERVER_INTERNAL_ERROR, RespCode.SERVER_INTERNAL_ERROR.getMsg());
    }

    @Override
    public T getContent() {
        return content;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public Result<T> okResp() {
        this.code = RespCode.SUCCESS.getCode();
        this.msg = RespCode.SUCCESS.getMsg();
        return this;
    }

    @Override
    public Result<T> failResp() {
        this.code = RespCode.SERVER_INTERNAL_ERROR.getCode();
        this.msg = RespCode.SERVER_INTERNAL_ERROR.getMsg();
        return this;
    }

    @Override
    public Result<T> msg(String msg) {
        this.msg = msg;
        return this;
    }

    @Override
    public Result<T> code(RespCode code) {
        this.code = code.getCode();
        this.msg = code.getMsg();
        return this;
    }

    @Override
    public Result<T> content(T content) {
        this.content = content;
        return this;
    }
}
