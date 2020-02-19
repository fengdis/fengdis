package com.fengdis.aspect;

import com.fengdis.log.entity.EventLog;
import com.fengdis.log.enums.EventType;
import com.fengdis.log.service.LogService;
import com.fengdis.queue.ThreadPoolUtil;
import com.fengdis.util.IpUtils;
import com.fengdis.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * @version 1.0
 * @Descrittion: 全局异常处理器，可自适应返回错误页面或返回异常信息JSON数据，并且捕捉异常日志入库
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
@Component
public class RecordExceptionLogHandler extends SimpleMappingExceptionResolver {

	private static final Logger logger = LoggerFactory.getLogger(RecordExceptionLogHandler.class);

	@Autowired
	private LogService logService;
	
	@Override
	public ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();
		MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
		recordEventLog(request,method,ex);
		ResponseBody annotation = method.getAnnotation(ResponseBody.class);
		if (annotation != null) {
			PrintWriter out = null;
			try {
				// 设置状态码
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
				// 设置ContentType
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				// 避免乱码
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Cache-Control", "no-cache,must-revalidate");
				out = response.getWriter();
				out.write(ex.getClass().getName());

			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			return null;
		} else {
			return super.doResolveException(request, response, handler, ex);
		}

	}

	private void recordEventLog(HttpServletRequest request,final Method method,final Exception ex) {
		ThreadPoolUtil.getExcutorService().submit(new Runnable() {
			@Override
			public void run() {
				EventLog log = new EventLog();
				log.setName("管理端异常");
				log.setInfo("异常信息：" + ex.getClass().getName());
				log.setAccount("");
				log.setClientIp(IpUtils.getClientIp(request));
				log.setEventType(EventType.ERROR);
				log.setServerIp(WebUtils.INSTANCE.getServerIp());
				log.setMethod(method.getDeclaringClass().getName() +"."+ method.getName() +"()");
				log.setParams("");
				log.setPosition("[class:]" + method.getDeclaringClass().getName() + ";[method:]" + method.getName());
				logService.save(log);
			}
		});
	}

}
