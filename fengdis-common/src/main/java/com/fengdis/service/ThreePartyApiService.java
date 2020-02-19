package com.fengdis.service;

import com.fengdis.util.AliHttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @Descrittion: 三方api服务
 * @author: fengdi
 * @since: 2018/9/6 0006 21:26
 */
@Component
public class ThreePartyApiService {

    private static final Logger logger = LoggerFactory.getLogger(ThreePartyApiService.class);

    private static final String GAODE_WEATHER_URL = "https://restapi.amap.com/v3/weather/weatherInfo";

    /**
     * 获取天气信息
     * @param cityCode
     * @return
     */
    public String getWeather(String cityCode){
        String host = GAODE_WEATHER_URL;
        String path = "";
        String method = "GET";
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("key","2ccc3ae95265b4a5f41c64ef0c986888");
        querys.put("city",cityCode);
        querys.put("output","json");

        try {
            HttpResponse response = AliHttpUtils.doGet(host, path, method, headers, querys);
            logger.info("高德天气服务查询成功");
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("高德天气服务查询异常",e);
        }
        return null;
    }

    /**
     * 获取新闻信息
     * @return
     */
    public String getNews(){
        return null;
    }
}
