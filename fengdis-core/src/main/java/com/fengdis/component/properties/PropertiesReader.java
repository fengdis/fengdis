package com.fengdis.component.properties;

import com.fengdis.util.ThreadFactoryUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.*;

/**
 * @version 1.0
 * @Descrittion: 60s刷新一次system.properties到本地缓存中，可实现热更新
 * @author: fengdi
 * @since: 2019/09/18 09:46
 */
@Component
public class PropertiesReader {
    private static Logger logger = LoggerFactory.getLogger(PropertiesReader.class);
    private static Map<String, String> propertiesMap = new HashMap<String, String>();
    private ScheduledFuture<?> future;
    private ScheduledExecutorService scheduledExecutorService;
    private long interval = 60000;
    private String intervalKeyInProperty = "system.refesh.properties.interval";
    private String configName = "system.properties";

    CopyOnWriteArrayList<List<String>> listeners = new CopyOnWriteArrayList<>();

    public PropertiesReader() {
        propertiesMap.put(intervalKeyInProperty, String.valueOf(interval));
        startTimmer();
    }

    //RpcPropertiesHelper rpcPropertiesHelper;

    /*public void setRpcPropertiesHelper(RpcPropertiesHelper rpcPropertiesHelper){
        this.rpcPropertiesHelper=rpcPropertiesHelper;
    }*/

    @PostConstruct
    public void doInit(){
        doRefresh();
    }

    void startTimmer() {
        if (null != scheduledExecutorService) {
            destroy();
        }
        scheduledExecutorService = Executors.newScheduledThreadPool(1,new ThreadFactoryUtils("RefreshPropertiesTimmer", true));
        future = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                doRefresh();
            }
        }, interval, interval, TimeUnit.MILLISECONDS);
    }

    public String getStringValue(String key) {
        String value = null;
        /*if (null != rpcPropertiesHelper){
            value= rpcPropertiesHelper.getProperty(key);
        }*/
        if (null == value){
            value = propertiesMap.get(key);
        }
        return value;
    }

    public String getStringValue(String key, String defaultValue) {
        String value = getStringValue(key);
        if (null == value || "".equals(value) || "NULL".equalsIgnoreCase(value)) {
            return defaultValue;
        }
        return value;
    }

    public Boolean getBooleanValue(String key) {
        return Boolean.parseBoolean(getStringValue(key,String.valueOf(true)));
    }

    public Boolean getBooleanValue(String key,Boolean defaultValue) {
        return Boolean.parseBoolean(getStringValue(key,String.valueOf(defaultValue)));
    }

    public Integer getIntegerValue(String key) {
        return Integer.parseInt(getStringValue(key, "0"));
    }

    public Integer getIntegerValue(String key, Integer defaultValue) {
        return Integer.parseInt(getStringValue(key, String.valueOf(defaultValue)));
    }

    public Long getLongValue(String key) {
        return Long.parseLong(getStringValue(key, "0"));
    }

    public Long getLongValue(String key, Long defaultValue) {
        return Long.parseLong(getStringValue(key, String.valueOf(defaultValue)));
    }

    public Double getDoubleValue(String key) {
        return Double.parseDouble(getStringValue(key, "0.0"));
    }

    public Double getDoubleValue(String key, Double defaultValue) {
        return Double.parseDouble(getStringValue(key, String.valueOf(defaultValue)));
    }

    public Float getFloatValue(String key) {
        return Float.parseFloat(getStringValue(key, "0.0"));
    }

    public Float getFloatValue(String key, Float defaultValue) {
        return Float.parseFloat(getStringValue(key, String.valueOf(defaultValue)));
    }

    public Byte getByteValue(String key) {
        return Byte.parseByte(getStringValue(key, "0"));
    }

    public Byte getByteValue(String key, Byte defaultValue) {
        return Byte.parseByte(getStringValue(key, String.valueOf(defaultValue)));
    }

    public Short getShortValue(String key) {
        return Short.parseShort(getStringValue(key, "0"));
    }

    public Short getShortValue(String key, Short defaultValue) {
        return Short.parseShort(getStringValue(key, String.valueOf(defaultValue)));
    }

    public List<String> getListValue(String key, List<String> defaultValue) {
        String str = getStringValue(key, String.valueOf(defaultValue));
        String[] array = str.split(",");
        return Arrays.asList(array);
    }

    public String[] getStringArrayValue(String key, String[] defaultValue) {
        String str = getStringValue(key, String.valueOf(defaultValue));
        return str.split(",");
    }

    @PostConstruct
    public void doRefresh() {
        InputStream inFile = null;
        try {
            Properties properties = new Properties();
            String runConf = System.getProperty("app.run.sysconf");
            if (StringUtils.isNotBlank(runConf)){
                configName = runConf;
            }

            inFile = PropertiesReader.class.getClassLoader().getResourceAsStream(configName);
            properties.load(new InputStreamReader(inFile, "UTF-8"));
            if (null != properties) {
                Iterator<Object> keys = properties.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next().toString();
                    String oldValue = propertiesMap.get(key);
                    String newValue = properties.getProperty(key);
                    if (null != oldValue && !oldValue.equals(newValue)) {
                        logger.info(String.format("Data Key: %s has changed from %s --> %s", key, oldValue, newValue));
                        if (key.equals(intervalKeyInProperty)) {
                            interval = Long.parseLong(newValue);
                            destroy();
                            startTimmer();
                        } else {
                            notifyDataChange(key, oldValue, newValue);
                        }
                    }
                    propertiesMap.put(key, newValue);
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Load file %s error, cause: %s", configName, e.getMessage()), e);
        }finally{
            if(inFile != null){
                try {
                    inFile.close();
                } catch (IOException e) {
                    logger.error("close inputStream Exception: ", e);
                }
            }
        }
    }

    void notifyDataChange(String key, String oldValue, String newValue) {
        Iterator<List<String>> dcls = listeners.iterator();
        while (dcls.hasNext()) {
            List<String> list = dcls.next();
            if (list.contains(key)) {
                try {
                    // TODO: 2019-09-18
                    //notifyChange(key, oldValue, newValue);
                } catch (Exception e) {
                    logger.error(String.format("Do DataChangeListener Error Key : %s",key), e);
                }
            }
        }
    }

    public void registDataChangeListener(List<String> keys) {
        listeners.add(keys);
    }

    @PreDestroy
    public void destroy() {
        try {
            scheduledExecutorService.shutdown();
            future.cancel(true);
        } catch (Throwable t) {
            logger.error(String.format("Destroy error, cause: %s",t.getMessage()), t);
        }
    }

}
