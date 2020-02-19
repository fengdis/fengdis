package com.fengdis.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version 1.0
 * @Descrittion: 线程池工厂类，继承自ThreadFactory，通过此工厂构建的线程池可标记线程池名称，便于管理和调试
 *	               一般用于Executors和ThreadPoolExecutor所构建的线程池
 * @author: fengdi
 * @since: 2018/9/6 0006 21:26
 */
public class ThreadFactoryUtils implements ThreadFactory{

	private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

	private String threadName;

	private boolean isDameo;

	private ThreadGroup threadGroup;

	private AtomicInteger atomicInteger = new AtomicInteger(0);

	private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);

	public ThreadFactoryUtils() {
		this("threadPool-" + POOL_SEQ.getAndIncrement(), false);
	}

	public ThreadFactoryUtils(String threadName) {
		this(threadName,false);
	}

	/**
	 * threadName为需要标记的名称，一般为类名或者应用名
	 * @param threadName
	 */
	public ThreadFactoryUtils(String threadName,boolean isDameo) {
		this.threadName = threadName;
		this.isDameo = isDameo;
		SecurityManager securityManager = System.getSecurityManager();
		this.threadGroup = securityManager == null ? Thread.currentThread().getThreadGroup() : securityManager.getThreadGroup();
	}

	@Override
    public Thread newThread(final Runnable runnable) {
		int seq = atomicInteger.incrementAndGet();
		Thread thread = defaultFactory.newThread(runnable);
        thread.setName(threadName+ "-" + atomicInteger + "-" + thread.getName());
        thread.setDaemon(isDameo);
        return thread;
    }

	public ThreadGroup getThreadGroup() {
		return this.threadGroup;
	}

	public static ExecutorService getExcutorService() {
		return new ThreadPoolExecutor(4, 8, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

}