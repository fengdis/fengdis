package com.fengdis.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fengdis.util.SpringBeanUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeChatUtils {

    private static final Logger logger = LoggerFactory.getLogger(WeChatUtils.class);

    //普通消息类型
    public static final String WECHAT_MESSAGE_TEXT = "text";
    public static final String WECHAT_MESSAGE_IMAGE = "image";
    public static final String WECHAT_MESSAGE_VOICE = "voice";
    public static final String WECHAT_MESSAGE_MUSIC = "music";
    public static final String WECHAT_MESSAGE_VIDEO = "video";
    public static final String WECHAT_MESSAGE_SHORTVIDEO = "shortvideo";
    public static final String WECHAT_MESSAGE_LOCATION = "location";
    public static final String WECHAT_MESSAGE_LINK = "link";

    public static final String WECHAT_MESSAGE_EVENT = "event";
    //事件推送类型
    public static final String WECHAT_EVENT_SUBSCRIBE = "subscribe";
    public static final String WECHAT_EVENT_UNSUBSCRIBE = "unsubscribe";
    public static final String WECHAT_EVENT_SCAN = "scan";
    public static final String WECHAT_EVENT_LOCATION = "LOCATION";//上报地理位置事件
    public static final String WECHAT_EVENT_CLICK = "CLICK";
    public static final String WECHAT_EVENT_VIEW = "VIEW";

    private WeChatUtils(){
    }

    /**
     * 参数排序
     * @param token
     * @param timestamp
     * @param nonce
     * @return
     */
    public static String sort(String token, String timestamp, String nonce) {
        String[] strArray = {token, timestamp, nonce};
        Arrays.sort(strArray);
        StringBuilder sb = new StringBuilder();
        for (String str : strArray) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 字符串进行shal加密
     * @param str
     * @return
     */
    public static String shal(String str){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解析微信发来的请求（XML）
     * @param request
     * @return map
     * @throws Exception
     */
    public static Map<String,String> parseXml(HttpServletRequest request) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String,String> map = new HashMap();
        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();

        // 遍历所有子节点
        for (Element e : elementList) {
            System.out.println(e.getName() + "|" + e.getText());
            map.put(e.getName(), e.getText());
        }

        // 释放资源
        inputStream.close();
        inputStream = null;
        return map;
    }

    /**
     * 根据消息类型 构造返回消息
     * @param map
     * @return
     */
    public static String buildXml(Map<String,String> map) {
        String result = null;
        String msgType = map.get("MsgType").toString();
        String event = null;
        String eventKey = null;
        if(map.containsKey("Event")){
            event = map.get("Event").toString();
        }
        if(map.containsKey("Event")){
            event = map.get("Event").toString();
        }
        logger.info("微信公众号MsgType：" + msgType);
        if(WECHAT_MESSAGE_TEXT.equals(msgType)){
            String content = map.get("Content").toString();
            if(content.contains("作者")){
                result = buildNewsMessage(map);
            }else if(content.contains("音乐")){
                result = buildMusicMessage(map);
            }else if(content.startsWith("博客#") && content.length() >= 2){
                //为解决用户输入文字就调用查询文章接口的次数过于频繁，增加前缀区分搜索类别
                String keyword = content.substring(content.indexOf("#")+1);
                result = buildBlogNewsMessage(map,keyword);
            }else {
                //非指定内容回复提醒
                result = buildTextMessage(map,"您请问些别的什么吧！如“博客#”+“关键词”的方式搜索博文。");
            }
        }else if(WECHAT_MESSAGE_IMAGE.equals(msgType)){
            result = buildImageMessage(map);
        }else if(WECHAT_MESSAGE_VOICE.equals(msgType)){
            result = buildVoiceMessage(map);
        }/*else if(WECHAT_MESSAGE_MUSIC.equals(msgType)){
            result = buildMusicMessage(map);
        }*/else if(WECHAT_MESSAGE_VIDEO.equals(msgType)){
            result = buildVideoMessage(map);
        }else if(WECHAT_MESSAGE_SHORTVIDEO.equals(msgType)){
            result = buildVideoMessage(map);
        }else if(WECHAT_MESSAGE_LOCATION.equals(msgType)){
            result = buildTextMessage(map,"请问客官想要什么地理位置？");
        }else if(WECHAT_MESSAGE_LINK.equals(msgType)){
            result = buildNewsMessage(map);
        }else if(WECHAT_MESSAGE_EVENT.equals(msgType) && WECHAT_EVENT_SUBSCRIBE.equals(event)){
            result = buildTextMessage(map,"关注");
        }else if(WECHAT_MESSAGE_EVENT.equals(msgType) && WECHAT_EVENT_UNSUBSCRIBE.equals(event)){
            result = buildTextMessage(map,"取消关注");
        }else if(WECHAT_MESSAGE_EVENT.equals(msgType) && WECHAT_EVENT_SUBSCRIBE.equals(event) && eventKey.startsWith("qrscene_")){
            result = buildTextMessage(map,"未关注扫码");
        }else if(WECHAT_MESSAGE_EVENT.equals(msgType) && WECHAT_EVENT_SCAN.equals(event)){
            result = buildTextMessage(map,"已关注扫码");
        }else if(WECHAT_MESSAGE_EVENT.equals(msgType) && WECHAT_EVENT_LOCATION.equals(event)){
            result = buildTextMessage(map,"上报地理位置");
        }else if(WECHAT_MESSAGE_EVENT.equals(msgType) && WECHAT_EVENT_CLICK.equals(event)){
            result = buildTextMessage(map,"菜单点击事件");
        }else if(WECHAT_MESSAGE_EVENT.equals(msgType) && WECHAT_EVENT_VIEW.equals(event)){
            result = buildTextMessage(map,"菜单跳转事件");
        }else{
            String fromUserName = map.get("FromUserName");
            // 开发者微信号
            String toUserName = map.get("ToUserName");
            result = String.format(
                    "<xml>" +
                            "<ToUserName><![CDATA[%s]]></ToUserName>" +
                            "<FromUserName><![CDATA[%s]]></FromUserName>" +
                            "<CreateTime>%s</CreateTime>" +
                            "<MsgType><![CDATA[text]]></MsgType>" +
                            "<Content><![CDATA[%s]]></Content>" +
                            "</xml>",fromUserName, toUserName, getUtcTime(),"请回复如下关键词：\n文本\n图片\n语音\n视频\n音乐\n图文");
        }

        return result;
    }

    /**
     * 自定义回复消息
     * @param map
     * @param keyword
     * @return
     */
    private static String buildBlogNewsMessage(Map<String,String> map,String keyword){
        JdbcTemplate jdbcTemplate = SpringBeanUtils.getBean(JdbcTemplate.class);
        String sql = "select a.id,a.title,a.introduction,a.material,a.html_material,a.picture,a.createdate,a.praise from tb_blog_article a where  (a.material like '%"+ keyword +"%' or a.title like '%"+ keyword +"%') order by createdate";
        List<Map<String, Object>> articles = jdbcTemplate.queryForList(sql);

        String fromUserName = map.get("FromUserName");
        String toUserName = map.get("ToUserName");
        if(articles != null && articles.size() > 0){
            Map<String,Object> article = articles.get(0);
            String title = article.get("title").toString();
            String description = article.get("introduction").toString();
            String picUrl = "https://www.fengdis.com" + article.get("picture").toString();
            String textUrl = "https://www.fengdis.com/blog/blog/web/base/info?articleId=" + article.get("id").toString();
            return String.format(
                    "<xml>" +
                            "<ToUserName><![CDATA[%s]]></ToUserName>" +
                            "<FromUserName><![CDATA[%s]]></FromUserName>" +
                            "<CreateTime>%s</CreateTime>" +
                            "<MsgType><![CDATA[news]]></MsgType>" +
                            "<ArticleCount>1</ArticleCount>" +
                            "<Articles>" +
                            "<item>" +
                            "<Title><![CDATA[%s]]></Title> " +
                            "<Description><![CDATA[%s]]></Description>" +
                            "<PicUrl><![CDATA[%s]]></PicUrl>" +
                            "<Url><![CDATA[%s]]></Url>" +
                            "</item>" +
                            "</Articles>" +
                            "</xml>",
                    fromUserName,toUserName, getUtcTime(),
                    title,description,picUrl,textUrl
            );
        }else {
            return String.format(
                    "<xml>" +
                            "<ToUserName><![CDATA[%s]]></ToUserName>" +
                            "<FromUserName><![CDATA[%s]]></FromUserName>" +
                            "<CreateTime>%s</CreateTime>" +
                            "<MsgType><![CDATA[text]]></MsgType>" +
                            "<Content><![CDATA[%s]]></Content>" +
                            "</xml>",fromUserName, toUserName, getUtcTime(), "没有相关文章");
        }
    }

    /**
     * 构造文本消息
     * @param map
     * @param content
     * @return
     */
    private static String buildTextMessage(Map<String,String> map, String content) {
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        //文本消息XML数据格式
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[text]]></MsgType>" +
                        "<Content><![CDATA[%s]]></Content>" +
                        "</xml>",fromUserName, toUserName, getUtcTime(), content);
    }

    /**
     * 构造图片消息
     * @param map
     * @return
     */
    private static String buildImageMessage(Map<String,String> map) {
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");

        String media_id = map.get("MediaId");
        //消息XML数据格式
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[image]]></MsgType>" +
                        "<Image>" +
                        "   <MediaId><![CDATA[%s]]></MediaId>" +
                        "</Image>" +
                        "</xml>",fromUserName, toUserName, getUtcTime(), media_id);
    }

    /**
     * 构建语音消息
     * @param map
     * @return
     */
    private static String buildVoiceMessage(Map<String, String> map) {
        String fromUserName = map.get("FromUserName");
        String toUserName = map.get("ToUserName");
        /*返回用户发过来的语音*/
        /*String media_id = map.get("MediaId");
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[voice]]></MsgType>" +
                        "<Voice>" +
                        "   <MediaId><![CDATA[%s]]></MediaId>" +
                        "</Voice>" +
                        "</xml>",
                fromUserName,toUserName, getUtcTime(),media_id
        );*/
        /*返回用户语音识别内容1*/
        String recognition = map.get("Recognition").toString();
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[text]]></MsgType>" +
                        "<Content><![CDATA[%s]]></Content>" +
                        "</xml>",fromUserName, toUserName, getUtcTime(),recognition
        );
    }

    /**
     * 构建视频消息
     * @param map
     * @return
     */
    private static String buildVideoMessage(Map<String, String> map) {
        String fromUserName = map.get("FromUserName");
        String toUserName = map.get("ToUserName");
        String title = "客官发过来的视频哟~~";
        String description = "客官您呐,现在肯定很开心,对不啦 嘻嘻��";
        /*返回用户发过来的视频*/
        String media_id = map.get("MediaId");
        //String media_id = "hTl1of-w78xO-0cPnF_Wax1QrTwhnFpG1WBkAWEYRr9Hfwxw8DYKPYFX-22hAwSs";
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[video]]></MsgType>" +
                        "<Video>" +
                        "   <MediaId><![CDATA[%s]]></MediaId>" +
                        "   <Title><![CDATA[%s]]></Title>" +
                        "   <Description><![CDATA[%s]]></Description>" +
                        "</Video>" +
                        "</xml>",
                fromUserName,toUserName, getUtcTime(),media_id,title,description
        );
    }

    /**
     * 构建音乐消息
     * @param map
     * @return
     */
    private static String buildMusicMessage(Map<String, String> map) {
        String fromUserName = map.get("FromUserName");
        String toUserName = map.get("ToUserName");
        String title = "往事只能回味";
        String description = "往事如昨 说时依旧";
        String hqMusicUrl ="https://www.kugou.com/song/#hash=912F1F6AA10D99EE4C85ABBBB024D407&album_id=974669";
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[music]]></MsgType>" +
                        "<Music>" +
                        "   <Title><![CDATA[%s]]></Title>" +
                        "   <Description><![CDATA[%s]]></Description>" +
                        "   <MusicUrl>< ![CDATA[%s] ]></MusicUrl>" +  //非必须项 音乐链接
                        "   <HQMusicUrl><![CDATA[%s]]></HQMusicUrl>"+ //非必须项 高质量音乐链接，WIFI环境优先使用该链接播放音乐
                        "</Music>" +
                        "</xml>",
                fromUserName,toUserName, getUtcTime(),title,description,hqMusicUrl,hqMusicUrl
        );
    }

    /**
     * 构建图文消息
     * @param map
     * @return
     */
    private static String buildNewsMessage(Map<String, String> map) {
        String fromUserName = map.get("FromUserName");
        String toUserName = map.get("ToUserName");
        String title1 = "fengdi个人技术博客";
        String description1 = "一个奋斗在it界的90后搬砖工，像“草根”一样，紧贴着地面，低调的存在，冬去春来，枯荣无恙。一个奋斗在it界的90后搬砖工，像“草根”一样，紧贴着地面，低调的存在，冬去春来，枯荣无恙。";
        String picUrl1 ="https://blog.fengdis.com/img/post-bg-coffee.jpeg";
        String textUrl1 = "https://blog.fengdis.com/";

        String title2 = "KendoUI之Grid的问题详解";
        String description2 = "kendoLov带出的值出现 null和undefined";
        String picUrl2 ="https://demos.telerik.com/kendo-ui/content/shared/images/theme-builder.png";
        String textUrl2 = "http://blog.csdn.net/a1786223749/article/details/78330908";

        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[news]]></MsgType>" +
                        "<ArticleCount>4</ArticleCount>" + //图文消息个数，限制为8条以内
                        "<Articles>" + //多条图文消息信息，默认第一个item为大图,注意，如果图文数超过8，则将会无响应
                        "<item>" +
                        "<Title><![CDATA[%s]]></Title> " +
                        "<Description><![CDATA[%s]]></Description>" +
                        "<PicUrl><![CDATA[%s]]></PicUrl>" + //图片链接，支持JPG、PNG格式，较好的效果为大图360*200，小图200*200
                        "<Url><![CDATA[%s]]></Url>" + //点击图文消息跳转链接
                        "</item>" +
                        "<item>" +
                        "<Title><![CDATA[%s]]></Title>" +
                        "<Description><![CDATA[%s]]></Description>" +
                        "<PicUrl><![CDATA[%s]]]></PicUrl>" +
                        "<Url><![CDATA[%s]]]></Url>" +
                        "</item>" +
                        "<item>" +
                        "<Title><![CDATA[%s]]></Title>" +
                        "<Description><![CDATA[%s]]></Description>" +
                        "<PicUrl><![CDATA[%s]]]></PicUrl>" +
                        "<Url><![CDATA[%s]]]></Url>" +
                        "</item>" +
                        "<item>" +
                        "<Title><![CDATA[%s]]></Title>" +
                        "<Description><![CDATA[%s]]></Description>" +
                        "<PicUrl><![CDATA[%s]]]></PicUrl>" +
                        "<Url><![CDATA[%s]]]></Url>" +
                        "</item>" +
                        "</Articles>" +
                        "</xml>",
                fromUserName,toUserName, getUtcTime(),
                title1,description1,picUrl1,textUrl1,
                title2,description2,picUrl2,textUrl2,
                title2,description2,picUrl2,textUrl2,
                title2,description2,picUrl2,textUrl2
        );
    }

    private static String getUtcTime() {
        Date dt = new Date();// 如果不需要格式,可直接用dt,dt就是当前系统时间
        DateFormat df = new SimpleDateFormat("yyyyMMddhhmm");// 设置显示格式
        String nowTime = df.format(dt);
        long dd = (long) 0;
        try {
            dd = df.parse(nowTime).getTime();
        } catch (Exception e) {

        }
        return String.valueOf(dd);
    }

    /**
     * 显示不可见字符的Unicode
     * @param input
     * @return
     */
    public static String escapeUnicode(String input) {
        StringBuilder sb = new StringBuilder(input.length());
        @SuppressWarnings("resource")
        Formatter format = new Formatter(sb);
        for (char c : input.toCharArray()) {
            if (c < 128) {
                sb.append(c);
            } else {
                format.format("\\u%04x", (int) c);
            }
        }
        return sb.toString();
    }

    /**
     * 将emoji替换为unicode
     * @param source
     * @return
     */
    public String filterEmoji(String source) {
        if (source != null) {
            Pattern emoji = Pattern.compile("[\ue000-\uefff]", Pattern.CASE_INSENSITIVE);
            Matcher emojiMatcher = emoji.matcher(source);
            Map<String, String> tmpMap = new HashMap<>();
            while (emojiMatcher.find()) {
                String key = emojiMatcher.group();
                String value = escapeUnicode(emojiMatcher.group());
                tmpMap.put(key, value);
            }
            if (!tmpMap.isEmpty()) {
                for (Map.Entry<String, String> entry : tmpMap.entrySet()) {
                    String key = entry.getKey().toString();
                    String value = entry.getValue().toString();
                    source = source.replace(key, value);
                }
            }
        }
        return source;
    }

    /**
     * 构建菜单
     * @return
     */
    public static JSONObject buildMenu(){
        JSONObject menu = new JSONObject();

        JSONArray button = new JSONArray();

        JSONArray sub_button1 = new JSONArray();
        JSONObject subMenu1 = new JSONObject();
        subMenu1.put("name","菜单1.1");
        subMenu1.put("type","click");
        subMenu1.put("key","1.1");
        sub_button1.add(subMenu1);
        JSONObject subMenu2 = new JSONObject();
        subMenu2.put("name","菜单1.2");
        subMenu2.put("type","click");
        subMenu2.put("key","1.2");
        sub_button1.add(subMenu2);
        JSONObject subMenu3 = new JSONObject();
        subMenu3.put("name","菜单1.3");
        subMenu3.put("type","click");
        subMenu3.put("key","1.3");
        sub_button1.add(subMenu3);

        JSONObject menu1 = new JSONObject();
        menu1.put("name","菜单1");
        menu1.put("sub_button",sub_button1);
        button.add(menu1);

        JSONArray sub_button2 = new JSONArray();
        JSONObject subMenu4 = new JSONObject();
        subMenu4.put("name","菜单2.1");
        subMenu4.put("type","click");
        subMenu4.put("key","2.1");
        sub_button2.add(subMenu1);
        JSONObject subMenu5 = new JSONObject();
        subMenu5.put("name","菜单2.2");
        subMenu5.put("type","view");
        subMenu5.put("url","https://www.fengdis.com/blog");
        sub_button2.add(subMenu2);

        JSONObject menu2 = new JSONObject();
        menu2.put("name","菜单2");
        menu2.put("sub_button",sub_button2);
        button.add(menu2);

        JSONObject menu3 = new JSONObject();
        menu3.put("name","菜单3");
        menu3.put("type","view");
        button.add(menu3);

        menu.put("button",button);

        return menu;
    }

}
