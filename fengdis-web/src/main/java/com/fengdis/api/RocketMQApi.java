package com.fengdis.api;

import com.fengdis.common.ResponseUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/rocketmq")
public class RocketMQApi {
    
    private static final Logger logger = LoggerFactory.getLogger(RocketMQApi.class);

    /*@Autowired
    private DefaultMQProducer defaultMQProducer;

    @GetMapping("/order")
    public ResponseEntity<String> order(String text) throws MQClientException, RemotingException, MQBrokerException, InterruptedException, UnsupportedEncodingException {
        Message sendMsg = new Message("userTopic","userTag","支付".getBytes());
        SendResult result = defaultMQProducer.send(sendMsg);
        logger.info(String.format("发送结果=%s, msg=%s ", result.getSendStatus(), result.toString()));
        return ResponseUtils.success();
    }


    *//**
     * 发送延迟消息
     * @param text
     * @return
     *//*
    @GetMapping("/delay")
    public ResponseEntity<String> sendDelayMsg(String text) throws MQClientException, RemotingException, InterruptedException{
        Message message = new Message("userTopic", "userTag",("延迟消息").getBytes());
        //"1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h"
        message.setDelayTimeLevel(3);
        defaultMQProducer.send(message, new SendCallback() {
            //消息发送成功回调
            @Override
            public void onSuccess(SendResult sendResult) {
                logger.info(String.format("发送结果=%s, msg=%s ", sendResult.getSendStatus(), sendResult.toString()));
            }

            //消息异常回调
            @Override
            public void onException(Throwable e) {
                e.printStackTrace();
                //补偿机制，根据业务情况进行使用，看是否进行重试
            }
        });
        return ResponseUtils.success();
    }*/

}
