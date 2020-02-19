package com.fengdis.queue;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @version 1.0
 * @Descrittion: 任务执行类，构造时传入具体的任务，返回结果类型json串
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class Producer implements Callable<TaskResult> {

	private ProducerTask producerTask;
	private Map<String,Object> params;

	public Producer(ProducerTask producerTask,Map<String,Object> params) {
		this.producerTask = producerTask;
		this.params = params;
	}

	@Override
	public TaskResult call() throws Exception {
		return producerTask.run(params);
	}

}
