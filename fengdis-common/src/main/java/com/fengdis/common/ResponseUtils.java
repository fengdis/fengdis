package com.fengdis.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @version 1.0
 * @Descrittion: 返回页面结果工具类
 * @author: fengdi
 * @since: 2019/08/30 17:26
 */
public class ResponseUtils {

	static Logger logger = LoggerFactory.getLogger(ResponseUtils.class);

	public static final String CONTENT_TYPE_FOR_XML = "application/xml";
	public static final String CONTENT_TYPE_FOR_JSON = "application/json";

	public static ResponseEntity<String> jsonResult(String result) {
		HttpHeaders resHeaders = new HttpHeaders();
		resHeaders.set("Content-Type", CONTENT_TYPE_FOR_JSON + ";charset=UTF-8");
		return new ResponseEntity<String>(result, resHeaders, HttpStatus.OK);
	}

	public static ResponseEntity<String> error(String errorMsg) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", false);
		map.put("errorMsg", errorMsg);
		return jsonResult(JSONObject.toJSONString(map));
	}

	public static ResponseEntity<String> success() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", true);
		map.put("errorMsg", "");
		return jsonResult(JSONObject.toJSONString(map));
	}

	public static ResponseEntity<String> success(Object obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", true);
		map.put("errorMsg", "");
		map.put("data", obj);
		return jsonResult(JSONObject.toJSONString(map));
	}
	
	public static ResponseEntity<Object> jsonResult(Object obj, String callback) {
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(obj);
		mappingJacksonValue.setJsonpFunction(callback);
		HttpHeaders resHeaders = new HttpHeaders();
		resHeaders.set("Content-Type", CONTENT_TYPE_FOR_JSON + ";charset=UTF-8");
		return new ResponseEntity<Object>(mappingJacksonValue, resHeaders, HttpStatus.OK);
	}
	
	public static ResponseEntity<Object> errorJsonp(String errorMsg, String callback) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", false);
		map.put("errorMsg", errorMsg);
		return jsonResult(map, callback);
	}
	
	public static ResponseEntity<Object> successJsonp(String callback) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", true);
		map.put("errorMsg", "");
		return jsonResult(map, callback);
	}
	
	public static ResponseEntity<Object> successJsonp(Object obj, String callback) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", true);
		map.put("errorMsg", "");
		map.put("data", obj);
		return jsonResult(map, callback);
	}
	
	public static ResponseEntity<String> successSerializerFeature(Object obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", true);
		map.put("errorMsg", "");
		map.put("data", obj);
		return jsonResult(JSONObject.toJSONString(map,SerializerFeature.DisableCircularReferenceDetect));
	}

	/**
	 * AJAX文本内容返回<br>
	 * 
	 * @param response
	 * @param text
	 */
	public static void ajax4TextResult(HttpServletResponse response, String text) {
		ajaxResult(response, text, null);
	}

	/**
	 * AJAX返回 <br>
	 * 
	 * @param response
	 * @param text
	 * @param type
	 */
	private static void ajaxResult(HttpServletResponse response, String text, String type) {
		PrintWriter printWriter = null;
		try {
			if (null != type)
				response.setContentType(type + ";charset=UTF-8");
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			printWriter = response.getWriter();
			printWriter.print(text);
		} catch (IOException e) {
			logger.error(String.format("AJAX返回失败:%s,返回类型:%s", text, type),e);
		} finally {
			if (null != printWriter) {
				printWriter.close();
			}
		}
	}

	/**
	 * AJAX返回XML<br>
	 * 
	 * @param response
	 * @param text
	 */
	public static void ajax4XMLResult(HttpServletResponse response, String text) {
		ajaxResult(response, text, CONTENT_TYPE_FOR_XML);
	}
}
