package com.fengdis.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Descrittion: ResultSet转化工具类
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class ResultSetUtils {
	
	private ResultSetUtils() {

	}
	
	/**
	 * 将resultSet转化为JSONObject对象
	 * 
	 * @param rs
	 * @return JSONObject
	 * @throws SQLException
	 */
	public static JSONObject toJsonObject(ResultSet rs) throws SQLException {

		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();

		if (rs.next()) {
			JSONObject jsonObj = new JSONObject();
			for (int i = 1; i <= columnCount; i++) {
				String columnName = metaData.getColumnLabel(i);
				String value = rs.getString(columnName);
				jsonObj.put(columnName, value);
			}
			return jsonObj;
		} else {
			return null;
		}

	}

	/**
	 * 将resultSet转化为JSONArray数组
	 * 
	 * @param rs
	 * @return JSONArray
	 * @throws SQLException
	 */
	public static JSONArray toJsonArray(ResultSet rs) throws SQLException {

		JSONArray array = new JSONArray();

		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		while (rs.next()) {
			JSONObject jsonObj = new JSONObject();

			for (int i = 1; i <= columnCount; i++) {
				String columnName = metaData.getColumnLabel(i);
				String value = rs.getString(columnName);
				jsonObj.put(columnName, value);
			}
			array.add(jsonObj);

		}
		return array;

	}

	/**
	 * 将单列结果集的列值转为list
	 * 
	 * @param rs
	 * @return List<String>
	 * @throws SQLException
	 */
	public static List<String> toList(ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		if (columnCount > 1) {
			throw new SQLException("Only one column is permited，结果集只允许一列");
		}
		List<String> list = new ArrayList<String>();
		while (rs.next()) {
			String colName = metaData.getColumnLabel(1);
			String value = rs.getString(colName);
			list.add(value);

		}
		return list;

	}
	
	/**
	 * 将结果集的列值转为Map
	 * 
	 * @param rs
	 * @return Map<String,Object>
	 * @throws SQLException
	 */
	public static Map<String,Object> toMap(ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		Map<String,Object> map = new HashMap<String,Object>();
		while (rs.next()) {
			for(int i=1;i<columnCount+1;i++){
				String colName = metaData.getColumnLabel(i);
				String value = rs.getString(colName);
				map.put(colName, value);
			}
			
		}
		return map;
	}
	
	/**
	 * 将结果集的列值转为List<Map<String,Object>>
	 * 
	 * @param rs
	 * @return List<Map<String,Object>>
	 * @throws SQLException
	 */
	public static List<Map<String,Object>> toListMap(ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		while (rs.next()) {
			Map<String,Object> map = new HashMap<String,Object>();
			for(int i=1;i<columnCount+1;i++){
				String colName = metaData.getColumnLabel(i);
				String value = rs.getString(colName);
				map.put(colName, value);
			}
			list.add(map);
		}
		return list;
	}

	/**
	 * 将结果集的列值转为指定对象类型
	 * 
	 * @param rs
	 * @param t
	 * @return T
	 * @throws SQLException
	 */
	public static <T> T convertToObject(ResultSet rs, Class<T> t) throws SQLException {
		JSONObject jsonObj = toJsonObject(rs);
		if (null != jsonObj) {
			return jsonObj.toJavaObject(t);
		} else {
			return null;
		}

	}

	/**
	 * 将结果集的列值转为指定对象类型List
	 * 
	 * @param rs
	 * @param t
	 * @return List<T>
	 * @throws SQLException
	 */
	public static <T> List<T> convertToList(ResultSet rs, Class<T> t) throws SQLException {
		JSONArray arr = toJsonArray(rs);
		List<T> resultList = arr.toJavaList(t);
		return resultList;
	}
	
}
