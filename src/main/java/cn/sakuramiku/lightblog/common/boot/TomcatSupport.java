package cn.sakuramiku.lightblog.common.boot;

import cn.sakuramiku.lightblog.LightBlogApplication;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Properties;

/**
 * Tomcat 容器应用支持
 *
 * @author lyy
 */
public class TomcatSupport extends AbstractBootSupport{

    protected static final Logger logger = LoggerFactory.getLogger(LightBlogApplication.class);

    protected int port;
    protected String contextPath;
    protected String encoding;

    /**
     * 初始化Tomcat参数
     */
    private void init() {
        try {
            ClassPathResource resource = new ClassPathResource("base-conf.properties");
            Properties properties = new Properties();
            properties.load(resource.getInputStream());
            port = Integer.parseInt(properties.getProperty("tomcat.port", "8080"));
            contextPath = properties.getProperty("tomcat.contextPath", "/");
            encoding = properties.getProperty("tomcat.encoding", "UTF-8");
        } catch (IOException e) {
            logger.error("加载Tomcat启动参数失败！", e);
        }
    }
    
    @Override
    protected void start() {
        long start = System.currentTimeMillis();
        init();
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        Context context = tomcat.addWebapp(contextPath, System.getProperty("java.io.tmpdir"));
        context.setRequestCharacterEncoding(encoding);
        context.setResponseCharacterEncoding(encoding);
        context.setDisplayName("tomcat" + port);
        Connector connector = new Connector();
        connector.setPort(port);
        connector.setURIEncoding(encoding);
        tomcat.setConnector(connector);
        tomcat.enableNaming();
        try {
            tomcat.start();
        } catch (Exception e) {
            logger.error("应用启动失败", e);
        }
        long offset = System.currentTimeMillis() - start;
        logger.info("启动耗时：{}分{}秒", offset / 1000 / 60, offset / 1000 % 60);
        tomcat.getServer().await();
    }

}
