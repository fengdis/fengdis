package com.fengdis.queue;

import com.alibaba.fastjson.JSONArray;

import java.util.Map;

/**
 * @version 1.0
 * @Descrittion: 线程执行任务基类，提供run方法待扩展，默认实现参见DefaultProducerTask
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public abstract class ProducerTask {

	//服务Bean
	private Object service;
	//任务编号
	private String taskId;

	public ProducerTask(Object service, String taskId) {
		this.service = service;
		this.taskId = taskId;
	}

	protected Object getService() {
		return service;
	}

	protected String getTaskId() {
		return taskId;
	}

	/**
	 * 任务执行中通用方法
	 * @param string
	 * @return
	 */
	protected JSONArray exeAndResult(String string) {
		return new JSONArray();
	}

	/**
	 * 开发者可自定义run中的业务逻辑: 返回结果
	 * 参见DefaultBDTask实现
	 * @return
	 */
	abstract TaskResult run(Map<String,Object> params);

}
