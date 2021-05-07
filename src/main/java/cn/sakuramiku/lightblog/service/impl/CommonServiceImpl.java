package cn.sakuramiku.lightblog.service.impl;

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

    @Override
    public void sendEmail() {

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
