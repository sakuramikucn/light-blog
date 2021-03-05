package cn.sakuramiku.lightblog.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;


/**
 * SpringMVC配置
 *
 * @author lyy
 */
@ComponentScan("cn.sakuramiku.lightblog")
@EnableWebMvc
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html", "doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 消息转换器，用于转换请求和响应的数据格式
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Object转换器
        MappingJackson2HttpMessageConverter jacksonMessageConverter = new MappingJackson2HttpMessageConverter();
        jacksonMessageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        jacksonMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        // 过滤null的属性
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jacksonMessageConverter.setObjectMapper(objectMapper);

        // String转换器
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringHttpMessageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_PLAIN));

        converters.add(stringHttpMessageConverter);
        converters.add(jacksonMessageConverter);
    }

}
