package cn.sakuramiku.lightblog.common.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author lyy
 */
@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> template;

    private static final Long EXPIRE = (long) (30 * 60);

    /**
     * <b>String</b><br/>
     * 添加到缓存，默认过期时间30分钟
     *
     * @param key   键
     * @param value 值
     */
    public Boolean set(String key, Object value) {
        return set(key, value, EXPIRE);
    }

    /**
     * <b>String</b><br/>
     * 添加到缓存
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间，秒
     */
    public Boolean set(String key, Object value, Long timeout) {
        try {
            template.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * <b>String</b><br/>
     * 添加到缓存，并且返回当前key对应的旧值
     *
     * @param key   键
     * @param value 值
     * @return 旧的值
     */
    public Object getAndSet(String key, Object value) {
        return template.opsForValue().getAndSet(key, value);
    }

    /**
     * <b>String</b><br/>
     * 获取key对应的值
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return template.opsForValue().get(key);
    }

    /**
     * <b>String</b><br/>
     * 删除缓存
     *
     * @param key 键
     * @return true=成功，false=失败
     */
    public Boolean delete(String key) {
        return template.delete(key);
    }

    /**
     * <b>String</b><br/>
     * 设置缓存获取时间
     *
     * @param key     键
     * @param timeout 过期时间，秒
     * @return true=成功，false=失败
     */
    public Boolean expire(String key, Long timeout) {
        return template.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * <b>String</b><br/>
     * 获取缓存获取时间
     *
     * @param key 键
     * @return 过期时间，秒
     */
    public Long getExpire(String key) {
        return template.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 是否过期
     *
     * @param key
     * @return
     */
    public Boolean isExpired(String key) {
        Long expire = template.getExpire(key, TimeUnit.MILLISECONDS);
        return expire <= 0;
    }

    /**
     * 是否存在键
     *
     * @param key
     * @return
     */
    public Boolean hasKey(String key) {
        return template.hasKey(key);
    }

    /**
     * 获取符合pattern规则的键
     *
     * @param pattern 例如shiro:**
     * @return key集合
     */
    public Set<String> getKeys(String pattern) {
        return template.keys(pattern);
    }
}
