package com.fengdis.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @version 1.0
 * @Descrittion: 比对原始结果容器，利用并发阻塞队列，存放原始结果
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public enum ResultQueueHolder {
	INSTANCE;

	private static final Logger logger = LoggerFactory.getLogger(ResultQueueHolder.class);

	private final LinkedBlockingQueue<TaskResultEntry> resultQueue = new LinkedBlockingQueue<TaskResultEntry>();

	//部署任务
	public void put(TaskResultEntry result) {
		try {
			resultQueue.put(result);
		} catch (InterruptedException e) {
			logger.error("result put error",e);
		}
	}

	//获取任务
	public TaskResultEntry take() {
        TaskResultEntry result = null;
		try {
			result = resultQueue.take();
		} catch (InterruptedException e) {
			logger.error("result take error",e);
		}
		return result;
	}

}
