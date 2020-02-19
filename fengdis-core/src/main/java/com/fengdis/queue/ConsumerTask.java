package com.fengdis.queue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @version 1.0
 * @Descrittion: 结果处理任务基类 ，提供handle方法待扩展，默认实现参见DefaultConsumerTask
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public abstract class ConsumerTask {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerTask.class);

	private String taskId;

	public ConsumerTask(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * 任务结果解析中通用方法
	 * @param result
	 * @return
	 */
	public final void execute(Future<TaskResult> result) {

		TaskResult availableResult = null;
		try {
			// 获取真实结果
			availableResult = result.get();
		} catch (InterruptedException | ExecutionException e) {
			logger.error("获取真实结果异常",e);
		}

		if (null != availableResult) {
			// 存储结果

			// 处理消息
			handle(availableResult);

			// 删除已完成的比对任务
		}

	}

	/**
	 * 开发者可自定义run中的业务逻辑: 返回结果
	 * 参见DefaultBDTask实现
	 * @return
	 */
	abstract String handle(TaskResult result);



	/**
	 * 将结果中输入参数inputParams（json格式）转换成map对象格式，便于java使用
	 * @param result
	 * @return
	 */
	protected Map<String, Object> getParams(TaskResult result) {
		return JSON.parseObject(result.getInputParams(), new TypeReference<Map<String, Object>>() {});
	}

	/**
	 * 将结果中结果数据（json格式）转换成map对象格式，便于java使用
	 * @param result
	 * @return
	 */
	protected Map<String, Object> getResult(TaskResult result) {
		return JSON.parseObject(result.getContent(), new TypeReference<Map<String, Object>>() {});
	}

	protected List<Object> convert(List<Map<String, Object>> listMap) {
		List<Object> listObj = new ArrayList<>();
		for (Map<String, Object> m : listMap) {
			listObj.addAll(m.values());
		}
		return listObj;
	}

}
