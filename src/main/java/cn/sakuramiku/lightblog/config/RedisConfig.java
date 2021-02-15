package cn.sakuramiku.lightblog.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

/**
 * redis配置类
 *
 * @author lyy
 */
@Configuration
@EnableCaching
@PropertySource("classpath:base-conf.properties")
public class RedisConfig {

    @Value("${redis.hostName:localhost}")
    private String hostName;
    @Value("${redis.port:6379}")
    private Integer port;
    @Value("${redis.password}")
    private String password;
    @Value("${redis.dataBase:0}")
    private Integer dataBase;

    /**
     * 最小空闲链接数
     */
    @Value("${redis.pool.minIdle:8}")
    private Integer minIdle;
    /**
     * 最大连接数
     */
    @Value("${redis.pool.maxActive:16}")
    private Integer maxTotal;
    /**
     * 当池内没有可用的连接时，最大等待时间
     */
    @Value("${redis.pool.maxWaitMillis:3000}")
    private Integer maxWaitMillis;
    /**
     * 在获取连接的时候检查有效性
     */
    @Value("${redis.pool.testOnBorrow:false}")
    private Boolean testOnBorrow;
    /**
     * 在空闲时检查有效性
     */
    @Value("${redis.pool.testWhileIdle:false}")
    private Boolean testWhileIdle;
    /**
     * 是否使用缓存
     */
    @Value("${redis.useCache:true}")
    private Boolean useCache;
    /**
     * 过期时间(秒)，默认30分钟
     */
    @Value("${redis.ttl:1800}")
    private Long ttl;

    /**
     * 管理缓存管理器
     *
     * @param rediscacheManager
     * @return
     */
    @Bean
    @Primary
    public CompositeCacheManager cacheManager(RedisCacheManager rediscacheManager) {
        CompositeCacheManager manager = new CompositeCacheManager();
        manager.setCacheManagers(Collections.singletonList(rediscacheManager));
        manager.setFallbackToNoOpCache(useCache);
        return manager;
    }

    /**
     * Redis缓存管理器
     *
     * @param connectionFactory
     * @param objectRedisSerializer
     * @param stringRedisSerializer
     * @return
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory, RedisSerializer<Object> objectRedisSerializer, RedisSerializer<String> stringRedisSerializer) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        RedisSerializationContext.SerializationPair<String> keyPair = RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer);
        RedisSerializationContext.SerializationPair<Object> valuePair = RedisSerializationContext.SerializationPair.fromSerializer(objectRedisSerializer);
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig().serializeKeysWith(keyPair).serializeValuesWith(valuePair);
        defaultCacheConfig.entryTtl(Duration.ofSeconds(ttl));
        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
    }

    @Bean
    public KeyGenerator simpleKeyGenerator() {
        return (Object o, Method method, Object... objects) -> {
            StringBuilder sb = new StringBuilder();
            for (Object param : objects) {
                if (null == param) {
                    continue;
                }
                sb.append(param).append(":");
            }
            String params = sb.toString();
            if (!StringUtils.isEmpty(params) && ':' == sb.charAt(params.length() - 1)) {
                params = sb.deleteCharAt(sb.lastIndexOf(":")).toString();
            }
            return method.getName() + ":" + params;
        };
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, RedisSerializer<Object> objectSerializer, RedisSerializer<String> stringSerializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(objectSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(objectSerializer);
        template.setDefaultSerializer(objectSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Object序列化器
     *
     * @return
     */
    @Bean
    public RedisSerializer<Object> objectSerializer() {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //必须设置，否则无法将JSON转化为对象，会转化成Map类型
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        // datetime 序列化、反序列化
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(localDateTimeSerializer);
        javaTimeModule.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);

        objectMapper.registerModule(javaTimeModule);
        serializer.setObjectMapper(objectMapper);
        return serializer;
    }

    /**
     * String序列化器
     *
     * @return
     */
    @Bean
    public RedisSerializer<String> stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    /**
     * jedis连接池配置
     *
     * @return
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMinIdle(minIdle);
        config.setMaxTotal(maxTotal);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestWhileIdle(testWhileIdle);
        return config;
    }

    /**
     * reids连接工厂
     *
     * @param jedisPoolConfig
     * @return
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        // 单机配置
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
        standaloneConfiguration.setHostName(hostName);
        standaloneConfiguration.setPort(port);
        standaloneConfiguration.setPassword(RedisPassword.of(password));
        standaloneConfiguration.setDatabase(dataBase);

        //jedis客户端配置
        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder builder =
                (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
        builder.poolConfig(jedisPoolConfig);
        JedisClientConfiguration clientConfiguration = builder.build();

        return new JedisConnectionFactory(standaloneConfiguration, clientConfiguration);
    }


}
