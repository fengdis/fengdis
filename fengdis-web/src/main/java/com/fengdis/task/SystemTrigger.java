package com.fengdis.task;

import com.fengdis.util.DateUtils;
import com.fengdis.util.IpUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @version 1.0
 * @Descrittion: 系统初始化、销毁任务
 * @author: fengdi
 * @since: 2019/08/28 17:26
 */
@Component
public class SystemTrigger {

	private static String ip = IpUtils.getServerIp();
	
	@PostConstruct
	public void init(){
		String sysStartTime = DateUtils.getSysStartTime();
		System.out.println("==============================");
		System.out.println(String.format("IP地址：%s",ip));
		System.out.println(String.format("启动时间：%s",sysStartTime));
		System.out.println("==============================");
	}

	@PreDestroy
	public void destory(){
		String sysEndTime = DateUtils.getSysStartTime();
		System.out.println("==============================");
		System.out.println(String.format("IP地址：%s",ip));
		System.out.println(String.format("关闭时间：%s",sysEndTime));
		System.out.println("==============================");
	}

}
