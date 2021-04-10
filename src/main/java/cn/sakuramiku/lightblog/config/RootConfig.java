package cn.sakuramiku.lightblog.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * Spring 配置
 *
 * @author lyy
 */
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@MapperScan(basePackages = "cn.sakuramiku.lightblog.mapper")
@EnableCaching
@EnableAspectJAutoProxy
@ComponentScan(value = "cn.sakuramiku.lightblog",
        excludeFilters = {
})
@Configuration
public class RootConfig {

    /**
     * 解决 @Value 注入失败
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     * 文件上传配置
     *
     * @return
     */
    @Bean
    public CommonsMultipartResolver getMultipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        return resolver;
    }

}
