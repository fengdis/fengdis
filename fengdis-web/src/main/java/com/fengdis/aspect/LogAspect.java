package com.fengdis.aspect;

import com.fengdis.log.entity.EventLog;
import com.fengdis.log.entity.OperateLog;
import com.fengdis.log.entity.SecurityLog;
import com.fengdis.log.enums.EventType;
import com.fengdis.log.enums.OperateType;
import com.fengdis.log.enums.ResultType;
import com.fengdis.log.enums.SecurityType;
import com.fengdis.log.service.LogService;
import com.fengdis.queue.ThreadPoolUtil;
import com.fengdis.util.IpUtils;
import com.fengdis.util.WebUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

/**
 * @version 1.0
 * @Descrittion: 日志切入
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
@Aspect
@Component
public class LogAspect {

	@Autowired
	private LogService logService;

	@Pointcut("@annotation(com.fengdis.log.annotation.LoginLog)")
	public void loginCut() {
	}

	@Pointcut("@annotation(com.fengdis.log.annotation.LogoutLog)")
	public void logoutCut() {
	}

	@Pointcut("@annotation(com.fengdis.log.annotation.OperateLog)")
	public void operateLogCut() {
	}

	@Pointcut("@annotation(com.fengdis.log.annotation.EventLog)")
	public void eventLogCut() {
	}

	private long currentTime = 0L;

	/**
	 * 登录日志切入
	 */
	@AfterReturning("loginCut()")
	public void loginCutReturn(JoinPoint joinPoint) {
		String ip = WebUtils.INSTANCE.getClientIP();
		//String account = WebUtils.INSTANCE.getCurrUserAccount();
		String account = "";
		ThreadPoolUtil.getExcutorService().submit(new Runnable() {
			@Override
			public void run() {
				recordLoginLog(ip, account);
			}
		});
	}

	/**
	 * 登出日志切入
	 * @param joinPoint
	 */
	@Before("logoutCut()")
	public void logoutCutReturn(JoinPoint joinPoint) {
		String ip = WebUtils.INSTANCE.getClientIP();
		//String account = WebUtils.INSTANCE.getCurrUserAccount();
		String account = "";
		ThreadPoolUtil.getExcutorService().submit(new Runnable() {
			@Override
			public void run() {
				recordLogoutLog(ip, account);
			}
		});
	}

	/**
	 * 操作日志切入（环绕）
	 */
	@Around("operateLogCut()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result;
		currentTime = System.currentTimeMillis();
		String clientIp = WebUtils.INSTANCE.getClientIP();
		result = joinPoint.proceed();

		ThreadPoolUtil.getExcutorService().submit(new Runnable() {
			@Override
			public void run() {
				recordOperateLog(joinPoint, clientIp,null, ResultType.SUCCESS);
			}
		});
		return result;
	}

	/**
	 * 操作日志切入（执行结束时）
	 */
	/*@AfterReturning("operateLogCut()")
	public void operateLogCutReturn(JoinPoint joinPoint) {
		currentTime = System.currentTimeMillis();
		String ip = WebUtils.INSTANCE.getClientIP();
		ThreadPoolUtil.getExcutorService().submit(new Runnable() {
			@Override
			public void run() {
				recordOperateLog(joinPoint, ip, ResultType.SUCCESS);
			}
		});
	}*/

	/**
	 * 操作日志切入（捕获异常时）
	 */
	@AfterThrowing(pointcut = "operateLogCut()", throwing = "e")
	public void operateLogCutThrow(JoinPoint joinPoint,Throwable e) {
		currentTime = System.currentTimeMillis();
		String clientIp = WebUtils.INSTANCE.getClientIP();
		ThreadPoolUtil.getExcutorService().submit(new Runnable() {
			@Override
			public void run() {
				recordOperateLog(joinPoint,clientIp,e,ResultType.FAIL);
			}
		});
	}

	/**
	 * 异常日志切入
	 */
	@AfterReturning("eventLogCut()")
	public void eventLogCutReturn(JoinPoint joinPoint) {
		ThreadPoolUtil.getExcutorService().submit(new Runnable() {
			@Override
			public void run() {
				recordEventLog(joinPoint);
			}
		});
	}

	private void recordLoginLog(String clientIp, String account) {
		SecurityLog log = new SecurityLog();
		log.setAccount(account);
		log.setClientIp(clientIp);
		log.setSecurityType(SecurityType.LOGIN);
		log.setInfo("登录");
		logService.save(log);
	}

	private void recordLogoutLog(String clientIp, String account) {
		SecurityLog log = new SecurityLog();
		log.setAccount(account);
		log.setClientIp(clientIp);
		log.setSecurityType(SecurityType.LOGOUT);
		log.setInfo("退出");
		logService.save(log);
	}

	private void recordOperateLog(JoinPoint joinPoint, String clientIp,Throwable exception, ResultType resultType) {
		String targetName = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();

		/*MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();*/

		Method[] methods = null;
		try {
			methods = Class.forName(targetName).getMethods();
		} catch (SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		String name = "";
		String info = "";
		OperateType type = null;
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				if (method.getParameterTypes().length == arguments.length) {
					com.fengdis.log.annotation.OperateLog anntationCls = method.getAnnotation(com.fengdis.log.annotation.OperateLog.class);
					info = anntationCls.info();
					name = anntationCls.name();
					type = anntationCls.type();
					break;
				}
			}
		}

		String methodname = joinPoint.getTarget().getClass().getName()+"."+ methodName +"()";

		StringBuilder params = new StringBuilder("{");
		//参数值
		Object[] argValues = joinPoint.getArgs();
		//参数名称
		String[] argNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();
		if(argValues != null){
			for (int i = 0; i < argValues.length; i++) {
				//params += " " + argNames[i] + ": " + argValues[i];
				params.append(" " + argNames[i] + ": " + argValues[i]);
			}
		}
		params.append("}");


		OperateLog log = new OperateLog();
		log.setName(name);
		log.setInfo(info);
		log.setAccount("");
		log.setClientIp(clientIp);
		log.setServerIp(WebUtils.INSTANCE.getServerIp());
		log.setMethod(methodname);
		log.setParams(params.toString());
		if(exception != null){
			log.setException(getStackTrace(exception).getBytes());
		}
		log.setOperateType(type);
		log.setResultType(resultType);
		log.setElapsedTime(System.currentTimeMillis() - currentTime);


		/*currentTime = System.currentTimeMillis();
		String ip = WebUtils.INSTANCE.getClientIP();
		result = joinPoint.proceed();

		OperateLog log = new OperateLog();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		com.fengdis.log.annotation.OperateLog aopLog = method.getAnnotation(com.fengdis.log.annotation.OperateLog.class);

		log.setName(aopLog.name());
		log.setInfo(aopLog.info());
		log.setOperateType(aopLog.type());

		log.setClientIp(ip);
		log.setAccount("");

		String methodName = joinPoint.getTarget().getClass().getName()+"."+signature.getName()+"()";

		String params = "{";
		//参数值
		Object[] argValues = joinPoint.getArgs();
		//参数名称
		String[] argNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();
		if(argValues != null){
			for (int i = 0; i < argValues.length; i++) {
				params += " " + argNames[i] + ": " + argValues[i];
			}
		}

		log.setMethod(methodName);
		log.setParams(params + " }");
		log.setElapsedTime(System.currentTimeMillis() - currentTime);*/

		logService.save(log);
	}

	private void recordEventLog(JoinPoint joinPoint) {
		String targetName = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();

		Method[] methods = null;
		try {
			methods = Class.forName(targetName).getMethods();
		} catch (SecurityException | ClassNotFoundException e) {

			e.printStackTrace();
		}
		String name = "";
		String info = "";
		EventType type = null;
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				if (method.getParameterTypes().length == arguments.length) {

					com.fengdis.log.annotation.EventLog anntationCls = method.getAnnotation(com.fengdis.log.annotation.EventLog.class);
					info = anntationCls.info();
					name = anntationCls.name();
					type = anntationCls.type();
					break;
				}
			}
		}

		String position = "[class:]" + joinPoint.getTarget().getClass().getName() + "[method:]" + joinPoint.getSignature().getName();
		EventLog log = new EventLog();
		log.setName(name);
		log.setInfo(info);
		log.setEventType(type);
		log.setPosition(position);
		log.setServerIp(IpUtils.getServerIp());
		logService.save(log);
	}

	public String getStackTrace(Throwable throwable){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			throwable.printStackTrace(pw);
			return sw.toString();
		} finally {
			pw.close();
		}
	}

}
