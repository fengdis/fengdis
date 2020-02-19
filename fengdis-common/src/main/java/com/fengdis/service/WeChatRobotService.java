package com.fengdis.service;

import com.alibaba.fastjson.JSONObject;
import com.fengdis.util.AliHttpUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Descrittion: 企业微信机器人服务
 * @author: fengdi
 * @since: 2018/8/8 0008 21:21
 */
@Component
public class WeChatRobotService {

    private static final Logger logger = LoggerFactory.getLogger(WeChatRobotService.class);

    @Value("${wechat.robot.webhookToken:''}")
    private String webhookToken;

    /**
     * 发送企业微信机器人消息
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
            logger.info(String.format("企业微信机器人发送%s消息成功",jsonObject.getString("msgtype")));
        } catch (Exception e) {
            logger.error(String.format("企业微信机器人发送%s消息异常",jsonObject.getString("msgtype")), e);
        }
        return null;
    }

    /**
     * 消息类型，此时固定为：text
     * @param content 文本内容，最长不超过2048个字节，必须是utf8编码
     * @param mentioned_list userid的列表，提醒群中的指定成员(@某个成员)，@all表示提醒所有人，如果开发者获取不到userid，可以使用mentioned_mobile_list
     * @param mentioned_mobile_list 手机号列表，提醒手机号对应的群成员(@某个成员)，@all表示提醒所有人
     * @return
     */
    public String sendTextMsg(String content,List<String> mentioned_list,List<String> mentioned_mobile_list){
        JSONObject textMsg = new JSONObject();

        JSONObject text = new JSONObject();
        text.put("content",content);
        text.put("mentioned_list",mentioned_list);
        text.put("mentioned_mobile_list",mentioned_mobile_list);

        textMsg.put("msgtype","text");
        textMsg.put("text",text);

        String host = webhookToken;
        String path = "";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
        Map<String, String> querys = new HashMap<String, String>();
        try {
            HttpResponse response = AliHttpUtils.doPost(host, path, method, headers, querys ,textMsg.toJSONString());
            logger.info("企业微信机器人发送text消息成功");
        } catch (Exception e) {
            logger.error("企业微信机器人发送text消息异常",e);
        }
        return null;
    }

    /**
     * 此消息类型为固定markdown
     * @param content markdown内容，最长不超过4096个字节，必须是utf8编码
     * @return
     */
    public String sendMarkdownMsg(String content){
        JSONObject markdownMsg = new JSONObject();

        JSONObject markdown = new JSONObject();
        markdown.put("content",content);

        markdownMsg.put("msgtype","markdown");
        markdownMsg.put("markdown",markdown);

        String host = webhookToken;
        String path = "";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
        Map<String, String> querys = new HashMap<String, String>();

        try {
            HttpResponse response = AliHttpUtils.doPost(host, path, method, headers, querys ,markdownMsg.toJSONString());
            logger.info("企业微信机器人发送markdown消息成功");
        } catch (Exception e) {
            logger.error("企业微信机器人发送markdown消息异常", e);
        }
        return null;
    }


    /**
     * 此消息类型为固定image
     * @param base64 图片内容的base64编码
     * @param md5 图片内容（base64编码前）的md5值
     * @return
     */
    public String sendImageMsg(String base64,String md5){
        JSONObject imageMsg = new JSONObject();

        JSONObject image = new JSONObject();
        image.put("base64",base64);
        image.put("md5",md5);

        imageMsg.put("msgtype","image");
        imageMsg.put("image",image);

        String host = webhookToken;
        String path = "";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
        Map<String, String> querys = new HashMap<String, String>();

        try {
            HttpResponse response = AliHttpUtils.doPost(host, path, method, headers, querys ,imageMsg.toJSONString());
            logger.info("企业微信机器人发送image消息成功");
        } catch (Exception e) {
            logger.error("企业微信机器人发送image消息异常", e);
        }
        return null;
    }

    /**
     * 此消息类型为固定news
     * @param articles 图文消息，一个图文消息支持1到8条图文
     * @return
     */
    public String sendNewsMsg(List<Article> articles){
        JSONObject newsMsg = new JSONObject();

        JSONObject news = new JSONObject();
        news.put("articles",articles);

        newsMsg.put("msgtype","news");
        newsMsg.put("news",news);

        String host = webhookToken;
        String path = "";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
        Map<String, String> querys = new HashMap<String, String>();

        try {
            HttpResponse response = AliHttpUtils.doPost(host, path, method, headers, querys ,newsMsg.toJSONString());
            logger.info("企业微信机器人发送news消息成功");
        } catch (Exception e) {
            logger.error("企业微信机器人发送news消息异常", e);
        }
        return null;
    }

    public class Article{
        private String title;//标题，不超过128个字节，超过会自动截断
        private String description;//描述，不超过512个字节，超过会自动截断
        private String url;//点击后跳转的链接。
        private String picurl;//图文消息的图片链接，支持JPG、PNG格式，较好的效果为大图 1068*455，小图150*150。

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPicurl() {
            return picurl;
        }

        public void setPicurl(String picurl) {
            this.picurl = picurl;
        }
    }
}
