package cn.sakuramiku.lightblog.config;

import ch.qos.logback.ext.spring.web.LogbackConfigListener;
import com.alibaba.druid.support.http.StatViewServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.*;

/**
 * web环境配置 (web.xml)
 *
 * @author lyy
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(WebAppInitializer.class);

    /**
     * 父容器
     *
     * @return
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{RootConfig.class};
    }

    /**
     * 子容器
     *
     * @return
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebMvcConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    /**
     * mvc启动
     *
     * @param servletContext
     * @throws ServletException
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        addDruidServlet(servletContext);
        super.onStartup(servletContext);
    }

    @Override
    protected FrameworkServlet createDispatcherServlet(WebApplicationContext servletAppContext) {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(servletAppContext);
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
        return dispatcherServlet;
    }

    @Override
    protected Filter[] getServletFilters() {
        List<Filter> filters = new ArrayList<>();

        // 编码过滤器
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter("utf-8");
        filters.add(encodingFilter);

        // shiro 过滤器代理，代理对象：ShiroConfig#shiroFilterFactoryBean
        if (Boolean.parseBoolean(System.getProperty("shiro.enable", "true"))) {
            DelegatingFilterProxy delegatingFilterProxy = new DelegatingFilterProxy();
            delegatingFilterProxy.setTargetFilterLifecycle(true);
            delegatingFilterProxy.setTargetBeanName("shiroFilterFactoryBean");
            filters.add(delegatingFilterProxy);
        }
        return filters.toArray(new Filter[]{});
    }

    @Override
    protected void registerContextLoaderListener(ServletContext servletContext) {
        servletContext.addListener(LogbackConfigListener.class);
        super.registerContextLoaderListener(servletContext);
    }

    /**
     * 添加Druid监控用的servlet
     *
     * @param servletContext
     */
    private void addDruidServlet(ServletContext servletContext) {
        Properties properties = getDbProperties();
        String enableStr = (String) properties.getOrDefault("dataSource.druid.stat.enable", false);
        boolean enable = Boolean.parseBoolean(enableStr);
        if (enable) {
            ServletRegistration.Dynamic servlet = servletContext.addServlet("DruidServlet", new StatViewServlet());
            setDruidServletParams(servlet, properties);
            // 访问路径
            servlet.addMapping("/druid/*");
        }
    }

    /**
     * 设置Druid监控配置
     *
     * @param servlet
     * @param properties
     */
    private void setDruidServletParams(ServletRegistration.Dynamic servlet, Properties properties) {
        String username = (String) properties.getOrDefault("dataSource.druid.stat.username", "druid");
        String password = (String) properties.getOrDefault("dataSource.druid.stat.password", "druid");
        String allow = (String) properties.getOrDefault("dataSource.druid.stat.allow", "");
        String deny = (String) properties.getOrDefault("dataSource.druid.stat.deny", "");
        Map<String, String> initParameters = new HashMap<>(8);
        initParameters.put("loginUsername", username);
        initParameters.put("loginPassword", password);
        // 允许访问druid后台界面的 ip 地址，留空则允许所有ip访问
        initParameters.put("allow", allow);
        // 拒绝访问druid后台界面的地址
        initParameters.put("deny", deny);
        servlet.setInitParameters(initParameters);
    }

    /**
     * 获取数据库相关的配置
     *
     * @return
     */
    private Properties getDbProperties() {
        return System.getProperties();
    }
}
