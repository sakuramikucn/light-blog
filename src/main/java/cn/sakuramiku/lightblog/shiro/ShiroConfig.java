package cn.sakuramiku.lightblog.shiro;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.annotation.ShiroPass;
import cn.sakuramiku.lightblog.util.BlogHelper;
import cn.sakuramiku.lightblog.util.Constant;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.Filter;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Shiro配置
 *
 * @author lyy
 */
@Configuration
public class ShiroConfig {

    @Bean
    public UserRealm userRealm() {
        return new UserRealm();
    }

    @Bean
    public SecurityManager securityManager(UserRealm shiroRealm) {
        DefaultWebSecurityManager defaultSecurityManager = new DefaultWebSecurityManager();
        defaultSecurityManager.setRealm(shiroRealm);
        // 关闭Shiro自带的session
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        defaultSecurityManager.setSubjectDAO(subjectDAO);
        defaultSecurityManager.setCacheManager(new CustomCacheManager());
        return defaultSecurityManager;
    }

    @Bean
    public AuthenticatingFilter jwtFilter() {
        return new JwtFilter();
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, AuthenticatingFilter jwtFilter) throws Exception {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("jwt", jwtFilter);
        shiroFilterFactoryBean.setFilters(filterMap);
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/user/**", "jwt");
        filterChainDefinitionMap.put("/role/**", "jwt");
        filterChainDefinitionMap.put("/right/**", "jwt");
        filterChainDefinitionMap.put("/article/**", "jwt");
        filterChainDefinitionMap.put("/comment/**", "jwt");
        filterChainDefinitionMap.put("/log/**", "jwt");
        filterChainDefinitionMap.put("/tag/**", "jwt");
        filterChainDefinitionMap.put("/link/**", "jwt");
        filterChainDefinitionMap.put("/category/**", "jwt");
        filterChainDefinitionMap.put("/logout", "jwt");
        if (Boolean.parseBoolean(System.getProperty("shiro.enable", "true"))) {
            filterChainDefinitionMap.putAll(getPassUrls());
        }
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");
        return shiroFilterFactoryBean;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;

    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }



    /**
     * 获取标记为不校验权限的url集合
     *
     * @return
     */
    protected static Map<String, String> getPassUrls() throws Exception {

        String contextPath = System.getProperty("tomcat.contextPath", "/");
        Set<Class<?>> classesList = getControllerClass("cn.sakuramiku.lightblog.controller");
        Map<String, String> filterRuleMap = new LinkedHashMap<>();
        for (Class<?> clazz : classesList) {
            String baseUrl;
            String[] classUrl = {};
            if (!ObjectUtil.isNull(clazz.getAnnotation(RequestMapping.class))) {
                classUrl = clazz.getAnnotation(RequestMapping.class).value();
            }
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(ShiroPass.class)) {
                    String[] methodUrl;
                    StringBuilder sb = new StringBuilder();
                    if (!ObjectUtil.isNull(method.getAnnotation(PostMapping.class))) {
                        methodUrl = method.getAnnotation(PostMapping.class).value();
                        if (ArrayUtil.isEmpty(methodUrl)) {
                            methodUrl = method.getAnnotation(PostMapping.class).path();
                        }
                        baseUrl = getRequestUrl(classUrl, methodUrl, sb, contextPath);
                        baseUrl = BlogHelper.genReqUrl("POST", baseUrl);
                    } else if (!ObjectUtil.isNull(method.getAnnotation(GetMapping.class))) {
                        methodUrl = method.getAnnotation(GetMapping.class).value();
                        if (ArrayUtil.isEmpty(methodUrl)) {
                            methodUrl = method.getAnnotation(GetMapping.class).path();
                        }
                        baseUrl = getRequestUrl(classUrl, methodUrl, sb, contextPath);
                        baseUrl = BlogHelper.genReqUrl("GET", baseUrl);
                    } else if (!ObjectUtil.isNull(method.getAnnotation(DeleteMapping.class))) {
                        methodUrl = method.getAnnotation(DeleteMapping.class).value();
                        if (ArrayUtil.isEmpty(methodUrl)) {
                            methodUrl = method.getAnnotation(DeleteMapping.class).path();
                        }
                        baseUrl = getRequestUrl(classUrl, methodUrl, sb, contextPath);
                        baseUrl = BlogHelper.genReqUrl("DELETE", baseUrl);
                    } else if (!ObjectUtil.isNull(method.getAnnotation(PutMapping.class))) {
                        methodUrl = method.getAnnotation(PutMapping.class).value();
                        if (ArrayUtil.isEmpty(methodUrl)) {
                            methodUrl = method.getAnnotation(PutMapping.class).path();
                        }
                        baseUrl = getRequestUrl(classUrl, methodUrl, sb, contextPath);
                        baseUrl = BlogHelper.genReqUrl("PUT", baseUrl);
                    } else {
                        methodUrl = method.getAnnotation(RequestMapping.class).value();
                        baseUrl = getRequestUrl(classUrl, methodUrl, sb, contextPath);
                    }
                    if (!StrUtil.isEmpty(baseUrl)) {
                        // 路径变量处理
                        if (baseUrl.contains("{")) {
                            baseUrl = baseUrl.substring(0, baseUrl.indexOf("{") - 1);
                        }
                        filterRuleMap.put(baseUrl, "anon");
                    }
                }
            }
        }
        Constant.SHIRO_PASS_URL.addAll(filterRuleMap.keySet());
        return filterRuleMap;
    }

    private static String getRequestUrl(String[] classUrl, String[] methodUrl, StringBuilder sb, String contextPath) {
        if (!StrUtil.isEmpty(contextPath)) {
            sb.append(contextPath);
        }
        if (!ArrayUtil.isEmpty(classUrl)) {
            for (String url : classUrl) {
                sb.append(url).append("/");
            }
        }
        for (String url : methodUrl) {
            sb.append(url);
        }
        if (sb.toString().endsWith("/")) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString().replaceAll("/+", "/");
    }


    public static Set<Class<?>> getControllerClass(String packagePath) throws Exception {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        Resource[] resources = resourcePatternResolver.getResources("classpath*:" + packagePath.replaceAll("[.]", "/") + "/**/*.class");
        Set<Class<?>> list = new HashSet<>();
        for (Resource r : resources) {
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(r);
            Class<?> clazz = ClassUtils.forName(metadataReader.getClassMetadata().getClassName(), null);
            if (clazz.isAnnotationPresent(RestController.class) || clazz.isAnnotationPresent(Controller.class)) {
                list.add(clazz);
            }
        }
        return list;
    }
}
