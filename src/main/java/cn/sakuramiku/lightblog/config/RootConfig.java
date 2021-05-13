package cn.sakuramiku.lightblog.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring 配置
 *
 * @author lyy
 */
@Configuration
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@MapperScan(basePackages = "cn.sakuramiku.lightblog.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(value = "cn.sakuramiku.lightblog")
public class RootConfig {

    /**
     * 解决 @Value 注入失败
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
