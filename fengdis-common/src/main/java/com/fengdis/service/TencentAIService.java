package com.fengdis.service;

import com.fengdis.util.AliHttpUtils;
import com.fengdis.util.DateUtils;
import com.fengdis.util.UUIDUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @version 1.0
 * @Descrittion: 腾讯AI服务
 * @author: fengdi
 * @since: 2018/8/10 0010 21:02
 */
@Component
public class TencentAIService {

    private static final Logger logger = LoggerFactory.getLogger(TencentAIService.class);

    @Value("${tencent.ai.appId:''}")
    private String appId;

    @Value("${tencent.ai.appKey:''}")
    private String appKey;

    /**
     * 图片鉴黄
     */
    private static final String vision_porn = "https://api.ai.qq.com/fcgi-bin/vision/vision_porn";
    /**
     * 暴恐识别
     */
    private static final String image_terrorism = "https://api.ai.qq.com/fcgi-bin/image/image_terrorism";

    /**
     * 生成签名（签名信息由接口请求参数和应用密钥根据官方提供的签名算法生成）
     * @param params
     * @throws IOException
     */
    public String getSignature(Map<String, String> params) {
        /**
         * 1,key字典排序-升序
         * 2,非空value url编码,大写
         * 3,拼接所有非空 key=value&.. ,最后再拼接app_key= ,生成字符串s
         * 4,md5加密s, 转大写,生成sign
         */
        try {
            Map<String, String> sortedParams = new TreeMap<>(params);
            Set<Map.Entry<String, String>> entrys = sortedParams.entrySet();
            StringBuilder baseString = new StringBuilder();
            for (Map.Entry<String, String> param : entrys) {
                if (param.getValue() != null && !"".equals(param.getKey().trim()) &&
                        !"sign".equals(param.getKey().trim()) && !"".equals(param.getValue())) {
                    baseString.append(param.getKey().trim()).append("=")
                            .append(URLEncoder.encode(param.getValue().toString(), "UTF-8")).append("&");
                }
            }
            if (baseString.length() > 0) {
                StringBuilder append = baseString.deleteCharAt(baseString.length() - 1).append("&app_key=")
                        .append(appKey);
            }

            String sign = DigestUtils.md5Hex(baseString.toString());
            return sign.toUpperCase();
        } catch (Exception e) {
            return null;
        }
    }

    public static final int imageBase64 = 1;
    public static final int imageUrl = 2;

    /**
     * 图片鉴黄
     * @param imageType 待鉴别图片类型
     * @param image
     * @return
     */
    public String porn(int imageType,String image){
        Map<String,String> params = new HashMap<>();
        params.put("app_id",appId);
        params.put("time_stamp", DateUtils.getSecondTimestamp(new Date()));
        params.put("nonce_str", UUIDUtils.getUUID());
        if(imageBase64 == imageType){
            params.put("image",image);
        }else if(imageUrl == imageType){
            params.put("image_url",image);
        }
        params.put("sign","");
        params.put("sign",getSignature(params));

        String host = vision_porn;
        String path = "";
        String method = "POST";
        Map<String, String> headers = new HashMap<>();
        Map<String, String> querys = new HashMap<>();
        Map<String, String> bodys = new HashMap<>();
        bodys.putAll(params);

        try {
            HttpResponse response = AliHttpUtils.doPost(host, path, method, headers, querys ,bodys);
            logger.info("腾讯AI图片鉴黄成功");
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            logger.error("腾讯AI图片鉴黄异常",e);
        }
        return null;
    }

    /**
     * 图片暴恐识别
     * @param imageType 待鉴别图片类型
     * @param image
     * @return
     */
    public String terrorism(int imageType,String image){
        Map<String,String> params = new HashMap<>();
        params.put("app_id",appId);
        params.put("time_stamp", DateUtils.getSecondTimestamp(new Date()));
        params.put("nonce_str", UUIDUtils.getUUID());
        if(imageBase64 == imageType){
            params.put("image",image);
        }else if(imageUrl == imageType){
            params.put("image_url",image);
        }
        params.put("sign","");
        params.put("sign",getSignature(params));

        String host = image_terrorism;
        String path = "";
        String method = "POST";
        Map<String, String> headers = new HashMap<>();
        Map<String, String> querys = new HashMap<>();
        Map<String, String> bodys = new HashMap<>();
        bodys.putAll(params);

        try {
            HttpResponse response = AliHttpUtils.doPost(host, path, method, headers, querys ,bodys);
            logger.info("腾讯AI图片暴恐识别成功");
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            logger.error("腾讯AI图片暴恐识别异常",e);
        }
        return null;
    }

}
