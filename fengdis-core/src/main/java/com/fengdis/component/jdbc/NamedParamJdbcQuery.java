package com.fengdis.component.jdbc;

import com.fengdis.util.ResultSetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @version 1.0
 * @Descrittion: JdbcTemplate模板类
 * @author: fengdi
 * @since: 2018/8/10 0010 21:02
 */
@Component
public class NamedParamJdbcQuery {

	private static final Logger logger = LoggerFactory.getLogger(NamedParamJdbcQuery.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private NamedParamJdbcQuery(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
	    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	public <T> List<T> queryListResult(String sql, Map<String, Object> params, Class<T> type) {
		List<T> list = namedParameterJdbcTemplate.execute(sql, params, new PreparedStatementCallback<List<T>>() {
			@Override
			public List<T> doInPreparedStatement(PreparedStatement stamt) throws SQLException, DataAccessException {
				ResultSet rs = stamt.executeQuery();
				return ResultSetUtils.convertToList(rs, type);
			}
		});
		return list;
	}


	public <T> T queryObjectResult(String sql, Map<String, Object> params, Class<T> type) {
		T object = namedParameterJdbcTemplate.execute(sql, params, new PreparedStatementCallback<T>() {
			@Override
			public T doInPreparedStatement(PreparedStatement stamt) throws SQLException, DataAccessException {
				ResultSet rs = stamt.executeQuery();
				return ResultSetUtils.convertToObject(rs, type);
			}
		});
		return object;
	}

	public <T> T querySingleResultBySimpleConditionMap(String noConditionBasicSql, Map<String, Object> params, Class<T> type) {
		Set<String> keys = params.keySet();
		StringBuilder sb = new StringBuilder();
		if (keys.size() > 0) {
			sb.append(" where");
			for (String key : keys) {
				String str = " " + key + "=:" + key;
				sb.append(str);
			}
		}

		String simpleConditionSql = noConditionBasicSql + sb.toString();
		T result = namedParameterJdbcTemplate.execute(simpleConditionSql, params, new PreparedStatementCallback<T>() {
			@Override
			public T doInPreparedStatement(PreparedStatement stamt) throws SQLException, DataAccessException {
				ResultSet rs = stamt.executeQuery();
				return ResultSetUtils.convertToObject(rs, type);
			}
		});
		return result;
	}

	public <T> List<T> queryResultBySimpleConditionMap(String noConditionBasicSql, Map<String, Object> params, Class<T> type) {
		Set<String> keys = params.keySet();
		StringBuilder sb = new StringBuilder();
		if (keys.size() > 0) {
			sb.append(" where");
			for (String key : keys) {
				String str = " " + key + "=:" + key;
				sb.append(str);
			}
		}

		String simpleConditionSql = noConditionBasicSql + sb.toString();
		List<T> resultList = namedParameterJdbcTemplate.execute(simpleConditionSql, params, new PreparedStatementCallback<List<T>>() {
			@Override
			public List<T> doInPreparedStatement(PreparedStatement stamt) throws SQLException, DataAccessException {
				ResultSet rs = stamt.executeQuery();
				return ResultSetUtils.convertToList(rs, type);
			}

		});
		return resultList;
	}

	/*public <T> Page<T> queryPage(NamedParameterJdbcTemplate template, String querySql, String countSql,
			Pageable pageable, Map<String, Object> params, Class<T> type) {

		List<T> resultList = NamedParamJdbcQuery.queryListResult(template, querySql, params, type);
		Long count = template.queryForObject(countSql, params, Long.class);
		Page<T> p = new PageWrapper<>(resultList, pageable, count);

		return p;
	}*/

}
