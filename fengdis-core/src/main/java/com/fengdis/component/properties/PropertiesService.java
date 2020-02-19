package com.fengdis.component.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @version 1.0
 * @Descrittion: 读取properties（此组件可读取最新properties数据，60s刷新一次system.properties文件数据到本地静态map中）
 * @author: fengdi
 * @since: 2019/09/18 10:50
 */
@Component
public class PropertiesService {

    @Autowired
    private PropertiesReader propertiesReader;

    public String getString(String key, String defaultValue){
        return propertiesReader.getStringValue(key, defaultValue);
    }

    public Boolean getBoolean(String key, Boolean defaultValue){
        return propertiesReader.getBooleanValue(key, defaultValue);
    }

    public Integer getInteger(String key, Integer defaultValue){
        return propertiesReader.getIntegerValue(key, defaultValue);
    }

    public Long getLong(String key, Long defaultValue){
        return propertiesReader.getLongValue(key, defaultValue);
    }

    public Double getDouble(String key, Double defaultValue){
        return propertiesReader.getDoubleValue(key, defaultValue);
    }

    public Float getFloat(String key, Float defaultValue){
        return propertiesReader.getFloatValue(key, defaultValue);
    }

    public Byte getByte(String key, Byte defaultValue){
        return propertiesReader.getByteValue(key, defaultValue);
    }

    public Short getShort(String key, Short defaultValue){
        return propertiesReader.getShortValue(key, defaultValue);
    }

    public List<String> getList(String key, List<String> defaultValue){
        return propertiesReader.getListValue(key, defaultValue);
    }

    public String[] getStringArray(String key,String[] defaultValue){
        return propertiesReader.getStringArrayValue(key, defaultValue);
    }

}
