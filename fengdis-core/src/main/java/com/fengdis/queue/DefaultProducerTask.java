package com.fengdis.queue;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * @version 1.0
 * @Descrittion: 具体线程任务实现，统一继承ProducerTask，实现其run方法
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class DefaultProducerTask extends ProducerTask {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultProducerTask.class);

	private JdbcTemplate jdbcTemplate;

	public DefaultProducerTask(JdbcTemplate jdbcTemplate, String taskId) {
		super(jdbcTemplate,taskId);
		this.jdbcTemplate = jdbcTemplate;
	}

	public TaskResult run(Map<String,Object> params) {
		//JSONArray resultArray = exeAndResult("");

		TaskResult taskResult = new TaskResult();

		String sql = "select id,title from tb_blog_article where id = ?";
		Map<String, Object> result = this.jdbcTemplate.queryForMap(sql,params.get("articleId").toString());

		taskResult.setInputParams(JSON.toJSONString(params));
		taskResult.setContent(JSON.toJSONString(result));
		return taskResult;
	}

}
