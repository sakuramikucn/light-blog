package cn.sakuramiku.lightblog.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.sakuramiku.lightblog.service.CommonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * @author lyy
 */
@Service
public class CommonServiceImpl implements CommonService {

    public static final String AK = System.getProperty("qiniu.ak");
    public static final String SK = System.getProperty("qiniu.sk");
    public static final String BUCKET = System.getProperty("qiniu.bucket");
    public static final String PREFIX_URL = "https://cdn.sakuramiku.cn/";

    public static final MailAccount MAIL_ACCOUNT ;

    static {
        String host = System.getProperty("mail.qq.host");
        String port = System.getProperty("mail.qq.port");
        String from = System.getProperty("mail.qq.from");
        String user = System.getProperty("mail.qq.user");
        String password = System.getProperty("mail.qq.password");
        MAIL_ACCOUNT = new MailAccount();
        MAIL_ACCOUNT.setHost(host);
        MAIL_ACCOUNT.setPort(Integer.parseInt(port));
        MAIL_ACCOUNT.setFrom(from);
        MAIL_ACCOUNT.setUser(user);
        MAIL_ACCOUNT.setPass(password);
        MAIL_ACCOUNT.setAuth(true);
    }

    @Async
    @Override
    public void sendEmail(String to, String subject, String content) {
        if (StrUtil.isNotBlank(to) && to.contains("@")){
            MailUtil.send(MAIL_ACCOUNT, to, subject, content, true);
        }
    }

    @Override
    public String upload(InputStream inputStream, String type) {
        Configuration configuration = new Configuration(Region.huanan());
        UploadManager uploadManager = new UploadManager(configuration);
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;
        Auth auth = Auth.create(AK, SK);
        String upToken = auth.uploadToken(BUCKET);
        try {
            Response response = uploadManager.put(inputStream,key,upToken,null, type);
            //解析上传成功的结果
            DefaultPutRet putRet = new ObjectMapper().readValue(response.bodyString(), DefaultPutRet.class);
            return PREFIX_URL+putRet.key;
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
