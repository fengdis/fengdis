package com.fengdis.component.jdbc;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Descrittion: JdbcTemplate模板类
 * @author: fengdi
 * @since: 2018/8/10 0010 21:02
 */
@Component
public class JdbcOperater {
	
	private JdbcTemplate jdbcTemplate;

	public JdbcOperater(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * 更新操作
	 */
	public int update(final String sql) throws DataAccessException {
		return jdbcTemplate.update(sql);
	}

	public int update(PreparedStatementCreator psc, KeyHolder generatedKeyHolder) throws DataAccessException {
		return jdbcTemplate.update(psc, generatedKeyHolder);
	}

	/*public int update(PreparedStatementCreator psc, PreparedStatementSetter pss) throws DataAccessException {
		return jdbcTemplate.update(psc, pss);
	}*/

	public int update(PreparedStatementCreator psc) throws DataAccessException {
		return jdbcTemplate.update(psc);
	}

	public int update(String sql, Object[] args, int[] argTypes) throws DataAccessException {
		return jdbcTemplate.update(sql, args, argTypes);
	}

	public int update(String sql, Object[] args) throws DataAccessException {
		return jdbcTemplate.update(sql, args);
	}

	public int update(String sql, PreparedStatementSetter pss) throws DataAccessException {
		return jdbcTemplate.update(sql, pss);
	}

	/**
	 * 批量更新操作
	 */
	public int[] batchUpdate(final String... sql) throws DataAccessException {
		return jdbcTemplate.batchUpdate(sql);
	}

	public int[] batchUpdate(String sql,List<Object[]> batchArgs) throws DataAccessException {
		return jdbcTemplate.batchUpdate(sql,batchArgs);
	}

	public int[] batchUpdate(String sql,List<Object[]> batchArgs,int[] argTypes) throws DataAccessException {
		return jdbcTemplate.batchUpdate(sql,batchArgs,argTypes);
	}

	public int[] batchUpdate(String sql, BatchPreparedStatementSetter bpss) throws DataAccessException {
		return jdbcTemplate.batchUpdate(sql,bpss);
	}

	public <T> int[][] batchUpdate(String sql, Collection<T> batchArgs, int batchSize, ParameterizedPreparedStatementSetter<T> pss) throws DataAccessException {
		return jdbcTemplate.batchUpdate(sql,batchArgs,batchSize,pss);
	}


	/**
	 * query
	 */
	
	public void query(PreparedStatementCreator psc, RowCallbackHandler rch) throws DataAccessException {
		jdbcTemplate.query(psc, rch);
	}

	
	public <T> List<T> query(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException {
		return jdbcTemplate.query(psc, rowMapper);
	}

	
	public <T> T query(PreparedStatementCreator psc, ResultSetExtractor<T> rse) throws DataAccessException {
		return jdbcTemplate.query(psc, rse);
	}

	
	public <T> T query(PreparedStatementCreator psc, PreparedStatementSetter pss, ResultSetExtractor<T> rse)
			throws DataAccessException {
		return jdbcTemplate.query(psc, pss, rse);
	}

	
	public <T> T query(String sql, Object[] args, int[] argTypes, ResultSetExtractor<T> rse) throws DataAccessException {
		return jdbcTemplate.query(sql, args, argTypes, rse);
	}

	
	public void query(String sql, Object[] args, int[] argTypes, RowCallbackHandler rch) throws DataAccessException {
		jdbcTemplate.query(sql, args, argTypes, rch);
	}

	
	public <T> List<T> query(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
		return jdbcTemplate.query(sql, args, argTypes, rowMapper);
	}

	
	public <T> T query(String sql, Object[] args, ResultSetExtractor<T> rse) throws DataAccessException {
		return jdbcTemplate.query(sql, args, rse);
	}

	
	public void query(String sql, Object[] args, RowCallbackHandler rch) throws DataAccessException {
		jdbcTemplate.query(sql, args, rch);
	}

	
	public void query(String sql, RowCallbackHandler rch, Object... args) throws DataAccessException {
		jdbcTemplate.query(sql, rch, args);
	}

	
	public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
		return jdbcTemplate.query(sql, args, rowMapper);
	}

	
	public <T> T query(String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws DataAccessException {
		return jdbcTemplate.query(sql, pss, rse);
	}

	
	public void query(String sql, PreparedStatementSetter pss, RowCallbackHandler rch) throws DataAccessException {
		jdbcTemplate.query(sql, pss, rch);
	}

	
	public <T> List<T> query(String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException {
		return jdbcTemplate.query(sql, pss, rowMapper);
	}

	
	public <T> T query(String sql, ResultSetExtractor<T> rse) throws DataAccessException {
		return jdbcTemplate.query(sql, rse);
	}

	
	public <T> T query(String sql, ResultSetExtractor<T> rse, Object... args) throws DataAccessException {
		return jdbcTemplate.query(sql, rse, args);
	}

	
	public void query(String sql, RowCallbackHandler rch) throws DataAccessException {
		jdbcTemplate.query(sql, rch);
	}

	
	public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException {
		return jdbcTemplate.query(sql, rowMapper);
	}

	
	public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
		return jdbcTemplate.query(sql, rowMapper, args);
	}

	/**
	 * queryForList
	 */
	
	public List<Map<String,Object>> queryForList(String sql) throws DataAccessException {
		return jdbcTemplate.queryForList(sql);
	}

	
	public <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException {
		return jdbcTemplate.queryForList(sql, elementType);
	}

	
	public List<Map<String,Object>> queryForList(String sql, Object... args) throws DataAccessException {
		return jdbcTemplate.queryForList(sql, args);
	}

	
	public <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) throws DataAccessException {
		return jdbcTemplate.queryForList(sql, elementType, args);
	}

	
	public <T> List<T> queryForList(String sql, Object[] args, Class<T> elementType) throws DataAccessException {
		return jdbcTemplate.queryForList(sql, args, elementType);
	}

	
	public List<Map<String,Object>> queryForList(String sql, Object[] args, int[] argTypes) throws DataAccessException {
		return jdbcTemplate.queryForList(sql, args, argTypes);
	}

	
	public <T> List<T> queryForList(String sql, Object[] args, int[] argTypes, Class<T> elementType) throws DataAccessException {
		return jdbcTemplate.queryForList(sql, args, argTypes, elementType);
	}

	/**
	 * queryForMap
	 */
	
	public Map<String,Object> queryForMap(String sql) throws DataAccessException {
		return jdbcTemplate.queryForMap(sql);
	}

	
	public Map<String,Object> queryForMap(String sql, Object... args) throws DataAccessException {
		return jdbcTemplate.queryForMap(sql, args);
	}

	
	public Map<String,Object> queryForMap(String sql, Object[] args, int[] argTypes) throws DataAccessException {
		return jdbcTemplate.queryForMap(sql, args, argTypes);
	}

	/**
	 * queryForObject
	 */
	
	public <T> T queryForObject(String sql, Class<T> requiredType) throws DataAccessException {
		return jdbcTemplate.queryForObject(sql, requiredType);
	}

	
	public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) throws DataAccessException {
		return jdbcTemplate.queryForObject(sql, requiredType, args);
	}

	
	public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws DataAccessException {
		return jdbcTemplate.queryForObject(sql, args, requiredType);
	}

	
	public <T> T queryForObject(String sql, Object[] args, int[] argTypes, Class<T> requiredType)
			throws DataAccessException {
		return jdbcTemplate.queryForObject(sql, args, argTypes, requiredType);
	}

	
	public <T> T queryForObject(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper)
			throws DataAccessException {
		return jdbcTemplate.queryForObject(sql, args, argTypes, rowMapper);
	}

	
	public <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
		return jdbcTemplate.queryForObject(sql, args, rowMapper);
	}

	
	public <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws DataAccessException {
		return jdbcTemplate.queryForObject(sql, rowMapper);
	}

	
	public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
		return jdbcTemplate.queryForObject(sql, rowMapper, args);
	}

	/**
	 * queryForRowSet
	 */
	
	public SqlRowSet queryForRowSet(String sql) throws DataAccessException {
		return jdbcTemplate.queryForRowSet(sql);
	}

	
	public SqlRowSet queryForRowSet(String sql, Object... args) throws DataAccessException {
		return jdbcTemplate.queryForRowSet(sql, args);
	}

	
	public SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes) throws DataAccessException {
		return jdbcTemplate.queryForRowSet(sql, args, argTypes);
	}

	
	public void execute(String sql) throws DataAccessException {
		jdbcTemplate.execute(sql);
	}
}