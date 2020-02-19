package com.fengdis.component.jdbc;

import com.fengdis.util.ResultSetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Descrittion: JdbcTemplate查询模板类
 * @author: fengdi
 * @since: 2018/8/10 0010 21:02
 */
@Component
public class JdbcTemplateQuery {
	
	private static final Logger logger = LoggerFactory.getLogger(JdbcTemplateQuery.class);

	private JdbcTemplate jdbcTemplate;

	private JdbcTemplateQuery(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * 查询结果为指定实体对象
	 * @param sql
	 * @param args
	 * @param type
	 * @param <T>
	 * @return
	 */
	public <T> T queryObjectResult(String sql,Object[] args, Class<T> type) {
		T object = jdbcTemplate.query(sql,args, new ResultSetExtractor<T>() {
			@Override
			public T extractData(ResultSet rs) throws SQLException, DataAccessException {
				return ResultSetUtils.convertToObject(rs, type);
			}
		});
		return object;
	}

	/**
	 * 查询结果为指定实体List
	 * @param sql
	 * @param args
	 * @param type
	 * @param <T>
	 * @return
	 */
	public <T> List<T> queryListResult(String sql,Object[] args, Class<T> type) {
		List<T> list = jdbcTemplate.query(sql,args, new ResultSetExtractor<List<T>>() {
			@Override
			public List<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
				return ResultSetUtils.convertToList(rs, type);
			}
		});
		return list;
	}

	/**
	 * 查询结果为Map
	 * @param sql
	 * @param args
	 * @return
	 */
	public Map<String,Object> queryMapResult(String sql,Object[] args) {
		Map<String,Object> map = jdbcTemplate.query(sql, args, new ResultSetExtractor<Map<String,Object>>() {
			@Override
			public Map<String,Object> extractData(ResultSet rs) throws SQLException, DataAccessException {
				return ResultSetUtils.toMap(rs);
			}
		});
		return map;
	}

	/**
	 * 查询结果为List<Map<String,Object>>
	 * @param sql
	 * @param args
	 * @return
	 */
	public List<Map<String,Object>> queryListMapResult(String sql,Object[] args) {
		List<Map<String,Object>> listMap = jdbcTemplate.query(sql,args, new ResultSetExtractor<List<Map<String,Object>>>() {
			@Override
			public List<Map<String,Object>> extractData(ResultSet rs) throws SQLException, DataAccessException {

				return ResultSetUtils.toListMap(rs);
			}
		});
		return listMap;
	}

}
