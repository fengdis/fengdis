package com.fengdis.service;

import com.alibaba.fastjson.JSONObject;
import com.fengdis.util.AliHttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @Descrittion: 微信公众号服务
 * @author: fengdi
 * @since: 2018/8/10 0010 21:02
 */
@Component
public class WeChatService {

    private static final Logger logger = LoggerFactory.getLogger(WeChatService.class);

    @Value("${wechat.appId}:''")
    private String weChat_appId;

    @Value("${wechat.secret}:''")
    private String weChat_secret;

    /**
     * 自定义token, 用作生成签名,从而验证安全性
     */
    private final String TOKEN = "blog";

    /**
     * 获取access_token的url
     */
    private static final String WECHAT_ACCESSTOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";

    /**
     * 构建菜单的url
     */
    private static final String WECHAT_MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create";

    /**
     * 微信公众号验签
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    public String checkConncet(String signature,String timestamp,String nonce,String echostr){
        logger.info("-----微信公众号开始校验签名-----");
        //将token、timestamp、nonce三个参数进行字典序排序并拼接为一个字符串
        String sortStr = WeChatUtils.sort(TOKEN,timestamp,nonce);
        //字符串进行shal加密
        String mySignature = WeChatUtils.shal(sortStr);
        //校验微信服务器传递过来的签名 和  加密后的字符串是否一致, 若一致则签名通过
        if(!"".equals(signature) && !"".equals(mySignature) && signature.equals(mySignature)){
            logger.info("-----微信公众号签名校验通过-----");
            return echostr;
        }else {
            logger.info("-----微信公众号校验签名失败-----");
        }
        return null;
    }

    /**
     * 微信公众号被动回复消息构建
     * @param request
     * @return
     */
    public String weChatPost(HttpServletRequest request){
        String result = null;
        try {
            //解析请求
            Map<String,String> map = WeChatUtils.parseXml(request);
            //构建响应
            result = WeChatUtils.buildXml(map);
            logger.info("微信被动回复消息构建成功");
        } catch (Exception e) {
            logger.error("微信被动回复消息构建异常",e);
        }
        return result;
    }

    /**
     * 获取access_token
     * @return
     */
    public String getAccessToken(){
        String host = WECHAT_ACCESSTOKEN_URL;
        String path = "";
        String method = "GET";
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("grant_type","client_credential");
        querys.put("appid",weChat_appId);
        querys.put("secret",weChat_secret);

        String accessToken = null;
        try {
            HttpResponse response = AliHttpUtils.doGet(host, path, method, headers, querys);
            logger.info("获取微信公众号access_token成功");
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = JSONObject.parseObject(result);
            accessToken = json.getString("access_token");
            String expiresIn = json.getString("expires_in");
        } catch (Exception e) {
            logger.error("获取微信公众号access_token异常",e);
        }

        return accessToken;

        /*ThreadPoolUtil.getExcutorService().submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String host = "https://api.weixin.qq.com/cgi-bin/token";
                        String path = "";
                        String method = "GET";
                        Map<String, String> headers = new HashMap<String, String>();
                        Map<String, String> querys = new HashMap<String, String>();
                        querys.put("grant_type","client_credential");
                        querys.put("appid",weChat_appId);
                        querys.put("secret",weChat_secret);

                        String accessToken = null;
                        try {
                            HttpResponse response = AliHttpUtils.doGet(host, path, method, headers, querys);
                            logger.info("获取weChat access_token成功！");
                            accessToken = EntityUtils.toString(response.getEntity());
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("获取weChat access_token异常！",e);
                        }
                        //获取成功
                        if (accessToken != null) {
                            //获取到access_token 休眠7000秒,大约2个小时左右
                            Thread.sleep(7000 * 1000);
                        } else {
                            //获取失败
                            Thread.sleep(1000 * 3); //获取的access_token为空 休眠3秒
                        }
                    } catch (Exception e) {
                        System.out.println("发生异常：" + e.getMessage());
                        e.printStackTrace();
                        try {
                            Thread.sleep(1000 * 10); //发生异常休眠1秒
                        } catch (Exception e1) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });*/

    }

    public void createMenu(){
        String accessToken = getAccessToken();

        String host = WECHAT_MENU_CREATE_URL;
        String path = "";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("axxess_token",accessToken);
        bodys.put("bodys",WeChatUtils.buildMenu().toJSONString());

        try {
            HttpResponse response = AliHttpUtils.doPost(host, path, method, headers, querys ,bodys);
            logger.info("微信公众号菜单创建成功");
        } catch (Exception e) {
            logger.error("微信公众号菜单创建异常",e);
        }
    }


    /**
     * 供微信公众平台调用
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @GetMapping(value = "/service/weChat",produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String weChat(String signature,String timestamp,String nonce,String echostr){
        return checkConncet(signature,timestamp,nonce,echostr);
    }

    @PostMapping(value = "/service/weChat",produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String weChat(HttpServletRequest request){
        return weChatPost(request);
    }

    @GetMapping(value = "/service/weChatService",produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String weChatService(){
        return getAccessToken();
    }

}
