package cn.sakuramiku.lightblog.service;

import java.io.InputStream;

/**
 * @author lyy
 */
public interface CommonService {

    /**
     * 发送邮件，用于评论通知
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
    void sendEmail(String to,String subject,String content);

    String upload(InputStream inputStream,String type);
}
