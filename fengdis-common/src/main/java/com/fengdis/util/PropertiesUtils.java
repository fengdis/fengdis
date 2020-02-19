package com.fengdis.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @version 1.0
 * @Descrittion: properties文件读取工具类
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class PropertiesUtils {

	private PropertiesUtils(){
	}

	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

	/**
	 * 默认把system.properties中的属性放入静态map中
	 */
	public static Map<String, String> propertiesMap = new HashMap<>();

	private static Properties properties;

	private static Configuration configuration;

	static {
		//properties = loadProperties("system.properties");
		/*if (null != properties) {
			Iterator<Object> keys = properties.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next().toString();
				String value = properties.getProperty(key);
				if(!propertiesMap.containsKey(key)){
					propertiesMap.put(key, value);
				}
			}
		}*/
		configuration = getConfig("system.properties");
		if (null != configuration) {
			Iterator keys = configuration.getKeys();
			while (keys.hasNext()) {
				String key = keys.next().toString();
				String value = properties.getProperty(key);
				if(!propertiesMap.containsKey(key)){
					propertiesMap.put(key, value);
				}
			}
		}
	}

	/**
	 * 根据文件路径获取Properties对象（java.util包中Properties）
	 * @param path
	 * @return
	 */
	@Deprecated
	private static Properties loadProperties(String path) {
		Properties properties = new Properties();
		try {
			InputStream inputStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(path);
			properties.load(new InputStreamReader(inputStream,"UTF-8"));
			logger.info(String.format("load %s file success",path));
		} catch (Exception e) {
			logger.error(String.format("load %s file error",path),e);
		}
		return properties;
	}


	/**
	 * 采用apache.common包中的Configuration获取配置信息，放弃使用java.util包中Properties
	 * 根据文件路径获取Configuration对象
	 * @param fileName
	 * @return
	 */
	public static Configuration getConfig(String fileName) {
		try {
			Configuration configuration = new PropertiesConfiguration(fileName);
			logger.info(String.format("load %s file success",fileName));
			return configuration;
		} catch (ConfigurationException e) {
			logger.error(String.format("load %s file error",fileName),e);
		}
		return null;
	}

	/*public static String getString(String key, String defaultValue){
		if(configuration != null){
			return configuration.getString(key, defaultValue);
		}
		return defaultValue;
	}

	public static Boolean getBoolean(String key, Boolean defaultValue){
		if(configuration != null){
			return configuration.getBoolean(key, defaultValue);
		}
		return defaultValue;
	}

	public static Integer getInteger(String key, Integer defaultValue){
		if(configuration != null){
			return configuration.getInteger(key, defaultValue);
		}
		return defaultValue;
	}

	public static Long getLong(String key, Long defaultValue){
		if(configuration != null){
			return configuration.getLong(key, defaultValue);
		}
		return defaultValue;
	}

	public static Double getDouble(String key, Double defaultValue){
		if(configuration != null){
			return configuration.getDouble(key, defaultValue);
		}
		return defaultValue;
	}

	public static Float getFloat(String key, Float defaultValue){
		if(configuration != null){
			return configuration.getFloat(key, defaultValue);
		}
		return defaultValue;
	}

	public static Byte getByte(String key, Byte defaultValue){
		if(configuration != null){
			return configuration.getByte(key, defaultValue);
		}
		return defaultValue;
	}

	public static Short getShort(String key, Short defaultValue){
		if(configuration != null){
			return configuration.getShort(key, defaultValue);
		}
		return defaultValue;
	}

	public static BigDecimal getBigDecimal(String key, BigDecimal defaultValue){
		if(configuration != null){
			return configuration.getBigDecimal(key, defaultValue);
		}
		return defaultValue;
	}

	public static BigInteger getBigInteger(String key, BigInteger defaultValue){
		if(configuration != null){
			return configuration.getBigInteger(key, defaultValue);
		}
		return defaultValue;
	}

	public static List getList(String key, List defaultValue){
		if(configuration != null){
			return configuration.getList(key, defaultValue);
		}
		return defaultValue;
	}

	public static String[] getStringArray(String key, String[] defaultValue){
		if(configuration != null){
			return configuration.getStringArray(key);
		}
		return defaultValue;
	}

	public static Properties getProperties(String key){
		if(configuration != null){
			return configuration.getProperties(key);
		}
		return null;
	}*/

}
