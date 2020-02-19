package com.fengdis.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fengdis.util.AliHttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @Descrittion: 新浪微博登录服务
 * @author: fengdi
 * @since: 2018/8/10 0010 21:02
 */
@Component
public class SinaOAuthService {

    private final Logger logger = LoggerFactory.getLogger(SinaOAuthService.class);

    @Value("${sina.appKey:''}")
    private String sina_clientId;

    @Value("${sina.appSecret:''}")
    private String sina_clientSecret;

    private static final String sina_grantType = "authorization_code";

    @Value("${sina.redirectUri:''}")
    private String sina_redirectUri;

    private static final String SINA_ACCESSTOKEN = "https://api.weibo.com/oauth2/access_token";
    private static final String SINA_USERINFO = "https://api.weibo.com/2/users/show.json";
    private static final String SINA_SHARE = "https://api.weibo.com/2/statuses/share.json";

    /**
     * 通过code获取access_token
     * @param code
     * @return
     */
    public JSONObject getAccessTokenByCode(String code){
        String host = SINA_ACCESSTOKEN;
        String path = "";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        querys.put("code",code);
        querys.put("client_id",sina_clientId);
        querys.put("client_secret",sina_clientSecret);
        querys.put("grant_type",sina_grantType);
        querys.put("redirect_uri",sina_redirectUri);

        try {
            HttpResponse response = AliHttpUtils.doPost(host, path, method, headers, querys ,bodys);
            JSONObject result = JSON.parseObject(EntityUtils.toString(response.getEntity()));
            logger.info("获取sina access_token成功");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取sina access_token异常", e);
        }
        return null;
    }

    /**
     * 通过access_token获取用户信息
     * @param accessToken
     * @param uid
     * @return
     */
    public String getUserInfo(String accessToken,String uid){
        String host = SINA_USERINFO;
        String path = "";
        String method = "GET";
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("access_token",accessToken);
        querys.put("uid",uid);

        try {
            HttpResponse response = AliHttpUtils.doGet(host, path, method, headers, querys);
            logger.info("获取sina用户信息成功");
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取sina用户信息异常",e);
        }
        return null;
    }

    /**
     * 分享到新浪微博
     * @param accessToken
     * @param status
     * @return
     */
    public String share2sina(String accessToken,String status){
        String host = SINA_SHARE;
        String path = "";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        querys.put("access_token",accessToken);
        querys.put("status",status);
        try {
            HttpResponse response = AliHttpUtils.doPost(host, path, method, headers, querys ,bodys);
            //JSONObject result = JSON.parseObject(EntityUtils.toString(response.getEntity()));
            logger.info("sina分享成功");
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("sina分享异常", e);
        }
        return null;
    }
}
