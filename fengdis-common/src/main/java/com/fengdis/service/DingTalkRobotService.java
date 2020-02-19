package com.fengdis.service;

import com.alibaba.fastjson.JSONObject;
import com.fengdis.util.AliHttpUtils;
import org.apache.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Descrittion: 钉钉机器人服务
 * @author: fengdi
 * @since: 2018/8/8 0008 21:21
 */
@Component
public class DingTalkRobotService {

    private static final Logger logger = LoggerFactory.getLogger(DingTalkRobotService.class);

    @Value("${dingtalk.robot.webhookToken:''}")
    private String webhookToken;

    /**
     * 发送钉钉机器人消息
     * @param jsonObject 消息指定json对象
     * @return
     */
    public String sendMsg(JSONObject jsonObject){
        String host = webhookToken;
        String path = "";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
        Map<String, String> querys = new HashMap<String, String>();
        try {
            HttpResponse response = AliHttpUtils.doPost(host, path, method, headers, querys ,jsonObject.toJSONString());
            logger.info(String.format("钉钉机器人发送%s消息成功",jsonObject.getString("msgtype")));
        } catch (Exception e) {
            logger.error(String.format("钉钉机器人发送%s消息异常",jsonObject.getString("msgtype")), e);
        }
        return null;
    }

    /**
     * 消息类型，此时固定为：text
     * @param content 消息内容
     * @param isAtAll 被@人的手机号(在content里添加@人的手机号)
     * @param atMobiles @所有人时：true，否则为：false
     * @return
     */
    public String sendTextMsg(String content,boolean isAtAll,List<String> atMobiles){
        JSONObject textMsg = new JSONObject();

        JSONObject text = new JSONObject();
        text.put("content",content);

        JSONObject at = new JSONObject();
        at.put("atMobiles",atMobiles);
        at.put("isAtAll",isAtAll);

        textMsg.put("msgtype","text");
        textMsg.put("text",text);
        textMsg.put("at",at);

        String host = webhookToken;
        String path = "";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
        Map<String, String> querys = new HashMap<String, String>();
        try {
            HttpResponse response = AliHttpUtils.doPost(host, path, method, headers, querys ,textMsg.toJSONString());
            logger.info("钉钉机器人发送text消息成功");
        } catch (Exception e) {
            logger.error("钉钉机器人发送text消息异常",e);
        }
        return null;
    }

    /**
     * 消息类型，此时固定为：link
     * @param text 消息内容。如果太长只会部分展示
     * @param title 消息标题
     * @param picUrl 图片URL
     * @param messageUrl 点击消息跳转的URL
     * @param isAtAll
     * @param atMobiles
     * @return
     */
    public String sendLinkMsg(String text,String title,String picUrl,String messageUrl,boolean isAtAll,List<String> atMobiles){
        JSONObject linkMsg = new JSONObject();

        JSONObject link = new JSONObject();
        link.put("text",text);
        link.put("title",title);
        link.put("picUrl",picUrl);
        link.put("messageUrl",messageUrl);

        JSONObject at = new JSONObject();
        at.put("atMobiles",atMobiles);
        at.put("isAtAll",isAtAll);

        linkMsg.put("msgtype","link");
        linkMsg.put("link",link);
        linkMsg.put("at",at);

        String host = webhookToken;
        String path = "";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
        Map<String, String> querys = new HashMap<String, String>();

        try {
            HttpResponse response = AliHttpUtils.doPost(host, path, method, headers, querys ,linkMsg.toJSONString());
            logger.info("钉钉机器人发送link消息成功");
        } catch (Exception e) {
            logger.error("钉钉机器人发送link消息异常", e);
        }
        return null;
    }

    /**
     * 此消息类型为固定markdown
     * @param text markdown格式的消息
     * @param title 首屏会话透出的展示内容
     * @param isAtAll @所有人时：true，否则为：false
     * @param atMobiles 被@人的手机号(在text内容里要有@手机号)
     * @return
     */
    public String sendMarkdownMsg(String text,String title,boolean isAtAll,List<String> atMobiles){
        JSONObject markdownMsg = new JSONObject();

        JSONObject markdown = new JSONObject();
        markdown.put("text",text);
        markdown.put("title",title);

        JSONObject at = new JSONObject();
        at.put("atMobiles",atMobiles);
        at.put("isAtAll",isAtAll);

        markdownMsg.put("msgtype","markdown");
        markdownMsg.put("markdown",markdown);
        markdownMsg.put("at",at);

        String host = webhookToken;
        String path = "";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
        Map<String, String> querys = new HashMap<String, String>();

        try {
            HttpResponse response = AliHttpUtils.doPost(host, path, method, headers, querys ,markdownMsg.toJSONString());
            logger.info("钉钉机器人发送markdown消息成功");
        } catch (Exception e) {
            logger.error("钉钉机器人发送markdown消息异常", e);
        }
        return null;
    }

    /**
     *
     * @param text
     * @param title
     * @param hideAvatar
     * @param btnOrientation

     * @param btns
     * @return
     */
    /**
     * 此消息类型为固定actionCard
     * @param text markdown格式的消息
     * @param title 首屏会话透出的展示内容
     * @param hideAvatar 0-正常发消息者头像，1-隐藏发消息者头像
     * @param btnOrientation 0-按钮竖直排列，1-按钮横向排列
     * @param singleTitle 单个按钮的方案。(设置此项和singleURL后btns无效)
     * @param singleURL 点击singleTitle按钮触发的URL
     * @param btns 按钮的信息：title-按钮方案，actionURL-点击按钮触发的URL
     * @return
     */
    public String sendActionCardMsg(String text, String title,String hideAvatar,String btnOrientation,String singleTitle,String singleURL, List<Btn> btns){
        JSONObject actionCardMsg = new JSONObject();

        JSONObject actionCard = new JSONObject();
        actionCard.put("text",text);
        actionCard.put("title",title);
        actionCard.put("hideAvatar",hideAvatar);
        actionCard.put("btnOrientation",btnOrientation);
        actionCard.put("singleTitle",singleTitle);
        actionCard.put("singleURL",singleURL);
        actionCard.put("btns",btns);

        actionCardMsg.put("msgtype","actionCard");
        actionCardMsg.put("actionCard",actionCard);

        String host = webhookToken;
        String path = "";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
        Map<String, String> querys = new HashMap<String, String>();

        try {
            HttpResponse response = AliHttpUtils.doPost(host, path, method, headers, querys ,actionCardMsg.toJSONString());
            logger.info("钉钉机器人发送actioncard消息成功");
        } catch (Exception e) {
            logger.error("钉钉机器人发送actioncard消息异常", e);
        }
        return null;
    }

    public class Btn{
        private String title;//按钮方案
        private String actionURL;//点击按钮触发的URL

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getActionURL() {
            return actionURL;
        }

        public void setActionURL(String actionURL) {
            this.actionURL = actionURL;
        }
    }

    public String sendFeedCardMsg(List<Link> links){
        JSONObject feedCardMsg = new JSONObject();

        JSONObject feedCard = new JSONObject();
        feedCard.put("links",links);

        feedCardMsg.put("msgtype","feedCard");
        feedCardMsg.put("feedCard",feedCard);

        String host = webhookToken;
        String path = "";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
        Map<String, String> querys = new HashMap<String, String>();

        try {
            HttpResponse response = AliHttpUtils.doPost(host, path, method, headers, querys ,feedCardMsg.toJSONString());
            logger.info("钉钉机器人发送markdown消息成功");
        } catch (Exception e) {
            logger.error("钉钉机器人发送markdown消息异常", e);
        }
        return null;
    }

    public class Link{
        private String title;//单条信息文本
        private String messageURL;//点击单条信息到跳转链接
        private String picURL;//单条信息后面图片的URL

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessageURL() {
            return messageURL;
        }

        public void setMessageURL(String messageURL) {
            this.messageURL = messageURL;
        }

        public String getPicURL() {
            return picURL;
        }

        public void setPicURL(String picURL) {
            this.picURL = picURL;
        }
    }

}
