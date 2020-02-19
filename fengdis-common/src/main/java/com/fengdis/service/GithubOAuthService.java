package com.fengdis.service;

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
 * @Descrittion: github登录服务
 * @author: fengdi
 * @since: 2018/8/10 0010 21:02
 */
@Component
public class GithubOAuthService {

    private static final Logger logger = LoggerFactory.getLogger(GithubOAuthService.class);

    @Value("${github.clientId:''}")
    private String github_clientId;

    @Value("${github.clientSecret:''}")
    private String github_clientSecret;

    private static final String GITHUB_ACCESSTOKEN = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_USERINFO = "https://api.github.com/user";

    /**
     * 通过code获取access_token
     * @param code
     * @return
     */
    public String getAccessTokenByCode(String code){
        String host = GITHUB_ACCESSTOKEN;
        String path = "";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("code",code);
        bodys.put("client_id",github_clientId);
        bodys.put("client_secret",github_clientSecret);

        try {
            HttpResponse response = AliHttpUtils.doPost(host, path, method, headers, querys ,bodys);
            logger.info("获取github access_token成功");
            String[] split = EntityUtils.toString(response.getEntity()).split("&");
            String[] split1 = split[0].split("=");
            return split1[1];
        } catch (Exception e) {
            logger.error("获取github access_token异常",e);
        }
        return null;
    }

    /**
     * 通过access_token获取用户信息
     * @param accessToken
     * @return
     */
    public String getUserInfo(String accessToken){
        String host = GITHUB_USERINFO;
        String path = "";
        String method = "GET";
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("access_token",accessToken);

        try {
            HttpResponse response = AliHttpUtils.doGet(host, path, method, headers, querys);
            logger.info("获取github用户信息成功");
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            logger.error("获取github用户信息异常");
        }
        return null;
    }

}
