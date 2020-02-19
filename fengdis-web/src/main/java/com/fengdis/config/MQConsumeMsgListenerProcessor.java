package com.fengdis.config;

import com.fengdis.component.rpc.rocketmq.MQConsumeMsgListener;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @version 1.0
 * @Descrittion: RocketMQ消息消费监听
 * @author: fengdi
 * @since: 2019/08/28 17:26
 */
@Component
public class MQConsumeMsgListenerProcessor extends MQConsumeMsgListener/*implements MessageListenerConcurrently */{
	
	private static final Logger logger = LoggerFactory.getLogger(MQConsumeMsgListenerProcessor.class);

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		if(CollectionUtils.isEmpty(msgs)){
			logger.info("接收到的消息为空，不做任何处理");
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		}

		for (MessageExt messageExt : msgs) {
			String topic = messageExt.getTopic();
			String tag = messageExt.getTags();
			String msg = new String(messageExt.getBody());
			logger.info("*********************************");
			logger.info(String.format("消费响应:msgId:%s,msgBody:%s,tag:%s,topic:%s",messageExt.getMsgId(),msg,tag,topic));
			logger.info("*********************************");

			if(messageExt.getTopic().equals("demoTopic")){
				if(messageExt.getTags().equals("demoTag")){
					int reconsumeTimes = messageExt.getReconsumeTimes();
					if(reconsumeTimes == 3){
						return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
					}
					//TODO 处理对应的业务逻辑
				}
			}
		}

		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}
	
}
