package com.fengdis.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;


/**
 * @version 1.0
 * @Descrittion: 队列任务线程池管理类
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class ThreadPoolManager {

	private static final Logger logger = LoggerFactory.getLogger(ThreadPoolManager.class);
	
	// 及时执行线程池
	private final Map<String, ExecutorService> timelyThreadPoolMap = new ConcurrentHashMap<String, ExecutorService>();
	// 延时执行线程池
	private final Map<String, ExecutorService> delayThreadPoolMap = new ConcurrentHashMap<String, ExecutorService>();

	private ThreadPoolManager() {

	}
	
	private static class TheadPoolHolder {
		public static ThreadPoolManager instance = new ThreadPoolManager();
	}

	public static ThreadPoolManager getInstance() {
		return TheadPoolHolder.instance;
	}

	/**
	 * 以业务模型bid为key，线程池为value加载一组线程池Map中
	 * @param model
	 */
	public void loadThreadPool(Map<String,Object> model) {
		for (String key : model.keySet()) {
			if(key.equals("0")){
				timelyThreadPoolMap.put(key, ThreadPoolUtil.getExcutorService());
				logger.info(key + "对应的及时线程池已加载");
			}else{
				delayThreadPoolMap.put(key, ThreadPoolUtil.getExcutorService());
				logger.info(key + "对应的延时线程池已加载");
			}
		}
	}

	/**
	 * 业务模型编号为key，加线程池到map中
	 * @param key
	 */
	public void put(String key,Byte isInTime) {
		if (!this.timelyThreadPoolMap.containsKey(key) && isInTime.equals("0")) {
			this.timelyThreadPoolMap.put(key, ThreadPoolUtil.getExcutorService());
			logger.info("已启动" + key + "对应及时线程池");
		}
		if (!this.delayThreadPoolMap.containsKey(key) && isInTime.equals("1")) {
			this.delayThreadPoolMap.put(key, ThreadPoolUtil.getExcutorService());
			logger.info("已启动" + key + "对应延时线程池");
		}
	}
	
	public void remove(String key) {
		if (this.timelyThreadPoolMap.containsKey(key)) {
			ExecutorService pool = this.timelyThreadPoolMap.get(key);
			pool.shutdown();
			timelyThreadPoolMap.remove(key);
			logger.info("已移除" + key + "对应的及时线程池");
		}
		if (this.delayThreadPoolMap.containsKey(key)) {
			ExecutorService delaypool = this.delayThreadPoolMap.get(key);
			delaypool.shutdown();
			delayThreadPoolMap.remove(key);
			logger.info("已移除" + key + "对应的延时线程池");
		}
	}

	public ExecutorService getTimely(String key) {
		return this.timelyThreadPoolMap.get(key);
	}

	public ExecutorService getDelay(String key) {
		return this.delayThreadPoolMap.get(key);
	}

	public boolean containsThreadPool(String bid) {
		boolean flag = false;
		if(this.timelyThreadPoolMap.containsKey(bid) || this.delayThreadPoolMap.containsKey(bid)){
			flag = true;
		}
		return flag;
		//return threadPoolMap.containsKey(bid);
	}

}
