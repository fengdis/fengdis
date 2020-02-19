package com.fengdis.queue;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

/**
 * @version 1.0
 * @Descrittion: 结果监听器，持续监听任务队列
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
@Component
public class ResultHandlerListener implements ApplicationListener<ContextRefreshedEvent>, Runnable {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Thread t = new Thread(this, "结果处理监听器线程");
		t.setDaemon(true);
		t.start();
	}


	@Override
	public void run() {
		while (true) {
			//从队列中获取任务结果
            TaskResultEntry resultEntry = ResultQueueHolder.INSTANCE.take();
			String taskId = resultEntry.getKey();

			//根据业务需要加载对应的线程池
			/*ThreadPoolManager poolMap = ThreadPoolManager.getInstance();
			ExecutorService threadPool = null;
			if (true) {
				threadPool = poolMap.getTimely(key);
			} else {
				threadPool = poolMap.getDelay(key);
			}*/

			ExecutorService threadPool = ThreadPoolUtil.getExcutorService();

			ConsumerTask task =  new DefaultConsumerTask(taskId);
			//执行线程任务
			threadPool.execute(new Consumer(task, resultEntry.getFutureResult()));

		}
	}

}
