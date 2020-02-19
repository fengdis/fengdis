package com.fengdis.component.rpc.rocketmq;

import com.fengdis.common.BaseExServiceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @version 1.0
 * @Descrittion: RocketMQ生产端工厂类（注入DefaultMQProducer）
 * @author: fengdi
 * @since: 2019/08/28 17:26
 */
@Configuration
@ConditionalOnExpression("${rocketmq.enabled:false}")
public class MQProducerConfig {

    public static final Logger logger = LoggerFactory.getLogger(MQProducerConfig.class);

    /**
     * 发送同一类消息的设置为同一个group，保证唯一,默认不需要设置，rocketmq会使用ip@pid(pid代表jvm名字)作为唯一标示
     */
    @Value("${rocketmq.producer.groupName:''}")
    private String groupName;

    @Value("${rocketmq.producer.namesrvAddr:''}")
    private String namesrvAddr;

    /**
     * 消息最大大小，默认4M
     */
    @Value("${rocketmq.producer.maxMessageSize:1}")
    private int maxMessageSize ;
    /**
     * 消息发送超时时间，默认3秒
     */
    @Value("${rocketmq.producer.sendMsgTimeout:1}")
    private int sendMsgTimeout;
    /**
     * 消息发送失败重试次数，默认2次
     */
    @Value("${rocketmq.producer.retryTimesWhenSendFailed:1}")
    private int retryTimesWhenSendFailed;

    @Bean
    @ConditionalOnExpression("${rocketmq.producer.enabled:true}")
    public DefaultMQProducer rocketMQProducerFactory() throws BaseExServiceException {

        if(StringUtils.isEmpty(groupName)){
            throw new BaseExServiceException(BaseExServiceException.SERVICE_EXCEPTION,"groupName is null");
        }

        if (StringUtils.isEmpty(namesrvAddr)) {
            throw new BaseExServiceException(BaseExServiceException.SERVICE_EXCEPTION,"nameServerAddr is null");
        }

        DefaultMQProducer producer = new DefaultMQProducer(groupName);

        producer.setNamesrvAddr(namesrvAddr);
        producer.setCreateTopicKey("AUTO_CREATE_TOPIC_KEY");

        //如果需要同一个jvm中不同的producer往不同的mq集群发送消息，需要设置不同的instanceName
        //producer.setInstanceName(instanceName);
            producer.setMaxMessageSize(maxMessageSize);
            producer.setSendMsgTimeout(sendMsgTimeout);
        //如果发送消息失败，设置重试次数，默认为2次
            producer.setRetryTimesWhenSendFailed(retryTimesWhenSendFailed);

        try {
            producer.start();
            logger.info("producer is start,groupName:{},namesrvAddr:{}",groupName,namesrvAddr);
        } catch (MQClientException e) {
            logger.error(String.format("producer is error {}", e.getMessage()));
            throw new BaseExServiceException(BaseExServiceException.SERVICE_EXCEPTION,e.getErrorMessage());
        }
        return producer;

    }
    
	
}

