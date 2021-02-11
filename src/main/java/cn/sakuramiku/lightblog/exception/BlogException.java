package cn.sakuramiku.lightblog.exception;

/**
 * 异常类
 *
 * @author lyy
 */
public class BlogException extends Exception {

    public BlogException(String message) {
        super(message);
    }

    public BlogException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlogException(Throwable cause) {
        super(cause);
    }
}
