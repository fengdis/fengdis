package com.fengdis.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @version 1.0
 * @Descrittion: spring容器启动监听
 * @author: fengdi
 * @since: 2019/08/28 17:26
 */
@Component
public class ContextInitListener implements ApplicationListener<ContextRefreshedEvent>,ServletContextListener{

	private static final Logger logger = LoggerFactory.getLogger(ContextInitListener.class);


	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(event.getApplicationContext().getParent() == null){
			logger.info("===================ApplicationListener onApplicationEvent");
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		logger.info("===================ServletContext Destroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		logger.info("===================ServletContext Initialized");
	}
	
}
