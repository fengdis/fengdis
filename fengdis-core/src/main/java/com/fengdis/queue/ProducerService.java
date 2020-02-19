package com.fengdis.queue;

import com.fengdis.util.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @version 1.0
 * @Descrittion: 任务主入口
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
@Component
public class ProducerService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);

	public void execute(Map<String, Object> params) throws Exception {
		/*ThreadPoolManager poolMap = ThreadPoolManager.getInstance();
		if (!poolMap.containsThreadPool(key)) {
			logger.warn("未发现" + key + "所对应的线程池");
			return;
		}

		ExecutorService threadPool = null;
		//根据业务决定，key这个任务在哪个线程池中
		if (true) {
			threadPool = poolMap.getTimely(key);
		} else {
			threadPool = poolMap.getDelay(key);
		}*/

		ExecutorService threadPool = ThreadPoolUtil.getExcutorService();

        String taskId = recordTask(params);

        if(params.get("queueName") == null){
			logger.warn("请传队列名参数");
        	return;
		}
        String queueName = params.get("queueName").toString();
		//构建任务，可存在多种任务实现类型
        ProducerTask producerTask = null;
		if("query".equalsIgnoreCase(queueName)){
			producerTask = new DefaultProducerTask(jdbcTemplate,taskId);
        }else{
			logger.warn("没有所传队列");
			return;
		}

		submitTask(taskId,params,threadPool,producerTask);
	}

	/**
	 * 任务线程提交
	 * @param threadPool
	 * @param producerTask
	 */
	private void submitTask(String taskId,Map<String, Object> params,ExecutorService threadPool, ProducerTask producerTask) {
		Producer producer = new Producer(producerTask,params);
		//启动线程
		Future<TaskResult> future = threadPool.submit(producer);
		//获取任务结果Future，构建ResultEntry实体
		TaskResultEntry taskResultEntry = new TaskResultEntry(taskId, future);
		//ResultEntry实体放入队列中，待取
		ResultQueueHolder.INSTANCE.put(taskResultEntry);
	}

    /**
     * 持久化任务数据，返回任务id
     * @param params
     * @return
     */
    private String recordTask(Map<String, Object> params) {
    	//根据业务需要进行任务数据持久化并返回任务Id
        return UUIDUtils.getUUID();
    }

}
