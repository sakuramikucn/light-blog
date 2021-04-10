package cn.sakuramiku.lightblog.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * spring ioc容器工具类
 *
 * @author LYY
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    protected static final Logger logger = LoggerFactory.getLogger(SpringContextUtil.class);

    protected static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.context = applicationContext;
        if (logger.isDebugEnabled()) {
            logger.debug("=========== @Component ==========");
            for (Map.Entry<String, Object> entry : applicationContext.getBeansWithAnnotation(Component.class).entrySet()) {
                logger.debug("Register Bean => {}", entry.getKey());
            }
            System.out.println();
            logger.debug("=========== @Bean ==========");
            for (Map.Entry<String, Object> entry : applicationContext.getBeansWithAnnotation(Bean.class).entrySet()) {
                logger.debug("Register Bean => {}", entry.getKey());
            }
            System.out.println();
            logger.debug("=========== @Service ==========");
            for (Map.Entry<String, Object> entry : applicationContext.getBeansWithAnnotation(Service.class).entrySet()) {
                logger.debug("Register Service => {}", entry.getKey());
            }
        }
    }

    /**
     * 获取一个bean对象
     *
     * @param name 名称
     * @param type 类型
     * @return bean对象
     */
    public static <T> T getBean(String name, Class<T> type) {
        if (null == context) {
            return null;
        }
        return context.getBean(name, type);
    }

    public static ApplicationContext getContext(){
        return context;
    }
}