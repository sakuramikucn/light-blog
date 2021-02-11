package cn.sakuramiku.lightblog.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageInterceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据访问配置
 *
 * @author lyy
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "cn.sakuramiku.lightblog.mapper")
@PropertySource("classpath:db-conf.properties")
public class DataBaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataBaseConfig.class);

    @Value("${mysql.url}")
    private String url;

    @Value("${mysql.driver}")
    private String driver;

    @Value("${mysql.username}")
    private String username;

    @Value("${mysql.password}")
    private String password;

    @Value("${dataSource.druid.initialSize:10}")
    private int initialSize;

    @Value("${dataSource.druid.minIdle:5}")
    private int minIdle;

    @Value("${dataSource.druid.maxActive}")
    private int maxActive;

    @Value("${dataSource.druid.maxWait}")
    private int maxWait;

    @Value("${dataSource.druid.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${dataSource.druid.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${dataSource.druid.validationQuery}")
    private String validationQuery;

    @Value("${dataSource.druid.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${dataSource.druid.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${dataSource.druid.testOnReturn}")
    private boolean testOnReturn;

    @Value("${dataSource.druid.poolPreparedStatements}")
    private boolean poolPreparedStatements;

    @Value("${dataSource.druid.maxPoolPreparedStatementPerConnectionSize}")
    private int maxPoolPreparedStatementPerConnectionSize;

    @Value("${dataSource.druid.filters}")
    private String filters;

    @Value("${dataSource.druid.connectionProperties}")
    private String connectionProperties;


    @Bean
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        //configuration
        dataSource.setInitialSize(initialSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        dataSource.setPoolPreparedStatements(poolPreparedStatements);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        try {
            dataSource.setFilters(filters);
        } catch (SQLException e) {
            logger.warn("配置Druid监控统计拦截的filters失败!",e);
        }
        dataSource.setConnectionProperties(connectionProperties);
        return dataSource;
    }

    /**
     * MyBatis的配置
     *
     * @return
     */
    private org.apache.ibatis.session.Configuration mybatisConfiguration() {
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis
                .session.Configuration();
        configuration.setDefaultStatementTimeout(3000);
        configuration.setMapUnderscoreToCamelCase(true);
        return configuration;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource, PageInterceptor pageInterceptor) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        try {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            sqlSessionFactoryBean.setDataSource(dataSource);
            sqlSessionFactoryBean.setConfiguration(mybatisConfiguration());
            sqlSessionFactoryBean.setMapperLocations(resourcePatternResolver
                    .getResources("classpath*:mapper/*Mapper.xml"));
            sqlSessionFactoryBean.setTypeAliasesPackage("cn.sakuramiku.lightblog.entity");
            sqlSessionFactoryBean.setPlugins(pageInterceptor);
        } catch (IOException e) {
            logger.error("SqlSessionFactoryBean 配置错误！",e);
        }
        return sqlSessionFactoryBean;
    }

    /**
     * 分页插件
     *
     * @return
     */
    @Bean
    public PageInterceptor pageInterceptor() {
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mysql");
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        interceptor.setProperties(properties);
        return interceptor;
    }

    /**
     * 事务控制配置
     *
     * @return
     */
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

//    /**
//     * 事务拦截器
//     *
//     * @return
//     */
//    @Bean(name = "transactionInterceptor")
//    public TransactionInterceptor interceptor(TransactionManager transactionManager) {
//        TransactionInterceptor interceptor = new TransactionInterceptor();
//        interceptor.setTransactionManager(transactionManager);
//
//        Properties transactionAttributes = new Properties();
//        transactionAttributes.setProperty("*", "PROPAGATION_REQUIRED");
//
//        interceptor.setTransactionAttributes(transactionAttributes);
//        return interceptor;
//    }
}
