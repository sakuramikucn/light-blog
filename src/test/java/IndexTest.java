import cn.sakuramiku.lightblog.common.util.RespResult;
import cn.sakuramiku.lightblog.config.WebAppInitializer;
import cn.sakuramiku.lightblog.config.WebMvcConfig;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lyy
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppInitializer.class, WebMvcConfig.class})
public class IndexTest {

    private final static Logger logger = LoggerFactory.getLogger(IndexTest.class);

    @Resource
    RedisTemplate<String,Object> redisTemplate;
    @Resource
    RestHighLevelClient elasticsearchClient;
    @Resource
    ElasticsearchRestTemplate elasticsearchRestTemplate;


    @Test
    public void HelloTest() {
        logger.error("Hello light-blog");
    }

    @Test
    public void TimeUtilTest(){

    }


    @Test
    public void connetRedisTest(){
        List clientList = redisTemplate.getClientList();
        assert clientList != null;
        System.out.println(clientList.toString());
    }

    @Test
    public void redisTest(){
//        redisTemplate.opsForList().leftPush("ccc","AAA");
//        redisTemplate.opsForList().leftPush("test","BBB");
//        redisTemplate.opsForList().leftPush("test","CCC");
//        List<Object> test = redisTemplate.opsForList().range("test", 0, 2);
//        System.out.println(Arrays.toString(test.toArray()));
        redisTemplate.opsForValue().set("test","miku");
        Object test = redisTemplate.opsForValue().get("test");
        System.out.println(test.toString());
    }

    @Test
    public void elasticTest(){
        RespResult<Object> build = RespResult.build();
    }


}
