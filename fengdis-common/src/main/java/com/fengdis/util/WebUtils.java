package com.fengdis.util;

import org.springframework.util.StringUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * @version 1.0
 * @Descrittion:
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public enum WebUtils {

	INSTANCE;

	public static final String separator = System.getProperty("file.separator");

	public String getWebClassPath() {

		return this.getClass().getClassLoader().getResource("").getPath();

	}

	public String getWebAppPath() {
		WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
		if (StringUtils.isEmpty(ctx)) {

			throw new IllegalStateException("WebApplicationContext还未完成初始化");
		}

		return ctx.getServletContext().getContextPath();
	}

	/*public String getCurrUserAccount() {
		Subject subject = SecurityUtils.getSubject();
		if (StringUtils.isEmpty(subject)) {
			throw new RuntimeException("用户不在线");
		}

		return (String) subject.getPrincipal();
	}*/

	// 在当前请求主线程线程中使用
	public HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	// 在当前请求主线程线程中使用
	public HttpServletResponse getResponse() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
	}

	// 在当前请求主线程线程中使用
	public String getClientIP() {
		HttpServletRequest request = getRequest();
		return IpUtils.getClientIp(request);
	}

	public String getServerIp() {
		return IpUtils.getServerIp();
	}

	private static boolean checkIP(String ip) {
		if (ip == null || ip.length() == 0 || "unkown".equalsIgnoreCase(ip) || ip.split(".").length != 4) {
			return false;
		}
		return true;
	}

}
