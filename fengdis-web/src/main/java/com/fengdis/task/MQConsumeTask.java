package com.fengdis.task;

import com.fengdis.config.MQConsumeMsgListenerProcessor;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @version 1.0
 * @Descrittion: RocketMQ消费开启
 * @author: fengdi
 * @since: 2019/08/28 18:17
 */
@Component
public class MQConsumeTask {

    private static final Logger logger = LoggerFactory.getLogger(MQConsumeTask.class);

    @Autowired
    private DefaultMQPushConsumer defaultMQPushConsumer;

    @Autowired
    private MQConsumeMsgListenerProcessor mqConsumeMsgListenerProcessor;

    @PostConstruct
    public void initConsume() {
        defaultMQPushConsumer.registerMessageListener(mqConsumeMsgListenerProcessor);
        try {
            defaultMQPushConsumer.start();
            logger.info("****************启动队列消费程序成功*****************");
        } catch (MQClientException e) {
            logger.error("*****************启动队列消费程序异常****************");
        }
    }

    @PreDestroy
    public void shutdown(){
        defaultMQPushConsumer.shutdown();
    }
}
