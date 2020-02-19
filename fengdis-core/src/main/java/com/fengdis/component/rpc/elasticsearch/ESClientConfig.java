package com.fengdis.component.rpc.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

/**
 * @version 1.0
 * @Descrittion: es工具类 transportClient将会在7.0版本上过时，并在8.0版本上移除掉，建议使用Java High Level REST Client
 * @author: fengdi
 * @since: 2019/08/28 17:26
 */
@Configuration
@ConditionalOnExpression("${elasticsearch.enabled:false}")
public class ESClientConfig {

    @Value("${elasticsearch.cluster-name:''}")
    private String cluster_name;

    @Value("${elasticsearch.cluster-ip:''}")
    private String cluster_ip;

    @Value("${elasticsearch.cluster-port:''}")
    private Integer cluster_port;

    @Bean
    public TransportClient transportClientFactory() {
        TransportClient client = null;

        try {
            Settings settings = Settings.builder()
                    .put("client.transport.sniff", true)
                    .put("cluster.name", cluster_name).build();
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(
                            new InetSocketAddress(cluster_ip, cluster_port)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }

    @Bean
    public RestHighLevelClient restHighLevelClientFactory(){
        RestHighLevelClient client = null;
        try {
            client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(cluster_ip, cluster_port, "http")
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }

    @Bean
    public RestClient restClientFactory(){
        RestClient restClient = null;
        try {
            restClient = RestClient.builder(
                    new HttpHost(cluster_ip, cluster_port, "http")
            ).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restClient;
    }

}