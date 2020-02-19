package com.fengdis.component.rpc.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @version 1.0
 * @Descrittion: zookeeper工具类（单例模式）
 * @author: fengdi
 * @since: 2019/08/28 17:26
 */
@Configuration
@ConditionalOnExpression("${zookeeper.enabled:false}")
public class ZookeeperConfig {

    @Value("${zookeeper.host:'127.0.0.1:2181'}")
    private String host;

    @Value("${zookeeper.timeout:'5000'}")
    private Integer timeoiut;

    //用来同步等待zkClient连接到了客户端
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Bean
    public ZooKeeper zooKeeperFactory() {
        ZooKeeper zookeeper = null;
        try {
            zookeeper = new ZooKeeper(host, timeoiut, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                        countDownLatch.countDown();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zookeeper;
    }

}
