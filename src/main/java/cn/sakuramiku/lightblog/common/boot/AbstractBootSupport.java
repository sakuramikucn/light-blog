package cn.sakuramiku.lightblog.common.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * 启动配置抽象
 *
 * @author lyy
 */
public abstract class AbstractBootSupport {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractBootSupport.class);

    /**
     * 启动之前的工作
     */
    protected void before() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:*-conf.properties");
            Properties properties = new Properties();
            for (Resource resource : resources) {
                properties.load(resource.getInputStream());
            }
            for (Object o : properties.keySet()) {
                String key = String.valueOf(o);
                String value = properties.getProperty(key);
                if (StringUtils.isEmpty(value)) {
                    continue;
                }
                System.setProperty(key, value);
            }
        } catch (IOException e) {
            logger.error("读取配置文件失败");
        }

    }

    /**
     * 启动工作
     */
    abstract void start();

    /**
     * 运行容器
     */
    protected void run() {
        before();
        start();
        destory();
    }

    /**
     * 结束容器
     */
    protected void destory() {

    }
}
