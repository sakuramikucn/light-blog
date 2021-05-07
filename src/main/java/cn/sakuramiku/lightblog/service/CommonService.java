package cn.sakuramiku.lightblog.service;

import java.io.InputStream;

/**
 * @author lyy
 */
public interface CommonService {

    /**
     * 发送邮件
     */
    void sendEmail();

    String upload(InputStream inputStream,String type);
}
