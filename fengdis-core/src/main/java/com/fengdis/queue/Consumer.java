package com.fengdis.queue;

import java.util.concurrent.Future;

/**
 * @version 1.0
 * @Descrittion: 结果处理类
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class Consumer implements Runnable {

	private ConsumerTask consumerTask;
	private Future<TaskResult> result;

	public Consumer(ConsumerTask consumerTask, Future<TaskResult> result) {
		this.consumerTask = consumerTask;
		this.result = result;
	}

	@Override
	public void run() {
		consumerTask.execute(result);
	}

}
