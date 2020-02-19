package com.fengdis.component.rpc.rocketmq;

import com.fengdis.common.BaseExServiceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @version 1.0
 * @Descrittion: RocketMQ消费端工厂类（注入DefaultMQPushConsumer）
 * @author: fengdi
 * @since: 2019/08/28 17:26
 */
@Configuration
@ConditionalOnExpression("${rocketmq.enabled:false}")
public class MQConsumerConfig {

    public static final Logger logger = LoggerFactory.getLogger(MQConsumerConfig.class);

    @Value("${rocketmq.consumer.namesrvAddr:''}")
    private String namesrvAddr;
    @Value("${rocketmq.consumer.groupName:''}")
    private String groupName;
    @Value("${rocketmq.consumer.consumeThreadMin:1}")
    private int consumeThreadMin;
    @Value("${rocketmq.consumer.consumeThreadMax:1}")
    private int consumeThreadMax;
    @Value("${rocketmq.consumer.topics:''}")
    private String topics;
    @Value("${rocketmq.consumer.consumeMessageBatchMaxSize:1}")
    private int consumeMessageBatchMaxSize;

    @Bean
    @ConditionalOnExpression("${rocketmq.consumer.enabled:true}")
    public DefaultMQPushConsumer rocketMQConsumerFactory() throws BaseExServiceException {
        if (StringUtils.isEmpty(groupName)){
            throw new BaseExServiceException(BaseExServiceException.SERVICE_EXCEPTION,"groupName is null");
        }
        if (StringUtils.isEmpty(namesrvAddr)){
            throw new BaseExServiceException(BaseExServiceException.SERVICE_EXCEPTION,"namesrvAddr is null");
        }
        if(StringUtils.isEmpty(topics)){
            throw new BaseExServiceException(BaseExServiceException.SERVICE_EXCEPTION,"topics is null");
        }

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setConsumeThreadMin(consumeThreadMin);
        consumer.setConsumeThreadMax(consumeThreadMax);
        //consumer.registerMessageListener(mqMessageListenerProcessor);

        /**
         * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
         * 如果非第一次启动，那么按照上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        /**
         * 设置消费模型，集群还是广播，默认为集群
         */
        //consumer.setMessageModel(MessageModel.CLUSTERING);

        /**
         * 设置一次消费消息的条数，默认为1条
         */
        consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);

        try {
            /**
             * 设置该消费者订阅的主题和tag，如果是订阅该主题下的所有tag，
             * 则tag使用*；如果需要指定订阅该主题下的某些tag，则使用||分割，例如tag1||tag2||tag3
             */
            /*String[] topicTagsArr = topics.split(";");
            for (String topicTags : topicTagsArr) {
                String[] topicTag = topicTags.split("~");
                consumer.subscribe(topicTag[0],topicTag[1]);
            }*/
            consumer.subscribe(topics, "*");

            //consumer.start();
            logger.info("consumer is start,groupName:{},topics:{},namesrvAddr:{}",groupName,topics,namesrvAddr);
        } catch (MQClientException e) {
            logger.error(String.format("consumer is error {}", e.getMessage()));
            throw new BaseExServiceException(BaseExServiceException.SERVICE_EXCEPTION,e.getErrorMessage());
        }

        return consumer;
    }
    
}

