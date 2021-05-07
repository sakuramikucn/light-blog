package cn.sakuramiku.lightblog.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Spring 配置
 *
 * @author lyy
 */
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@MapperScan(basePackages = "cn.sakuramiku.lightblog.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(value = "cn.sakuramiku.lightblog",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class))
@Configuration
public class RootConfig {

    /**
     * 解决 @Value 注入失败
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
