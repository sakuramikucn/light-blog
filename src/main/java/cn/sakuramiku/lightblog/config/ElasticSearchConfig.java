package cn.sakuramiku.lightblog.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

/**
 * ElasticSearch配置
 *
 * @author lyy
 */
//@Configuration
@PropertySource("classpath:base-conf.properties")
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

    @Value("${elasticsearch.hostName:localhost}")
    private String hostName;
    @Value("${elasticsearch.port:9200}")
    private Integer port;
    @Value("${elasticsearch.username:elastic}")
    private String userName;
    @Value("${elasticsearch.password:elastic}")
    private String password;

    @Bean(destroyMethod = "close")
    @Override
    public RestHighLevelClient elasticsearchClient() {
        HttpHost httpHost = new HttpHost(hostName, port);
        RestClientBuilder builder = RestClient.builder(httpHost);
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));

        builder.setHttpClientConfigCallback(config -> config.setDefaultCredentialsProvider(credentialsProvider));

        return new RestHighLevelClient(builder);
    }

}
