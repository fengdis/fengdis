package com.fengdis.config;

import com.fengdis.common.BaseExServiceException;
import com.fengdis.common.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @version 1.0
 * @Descrittion: 全局异常捕获
 * @author: fengdi
 * @since: 2019/08/28 17:26
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(value = BaseExServiceException.class)
	public ResponseEntity<String> ServiceRpcExceptionHandler(HttpServletRequest request, BaseExServiceException e)
			throws Exception {
		logger.warn(e.getMessage());
		if (e instanceof BaseExServiceException) {
			return ResponseUtils.error(e.getMessage());
		} else {
			return ResponseUtils.error("操作失败");
		}
	}

	@ExceptionHandler
	public ResponseEntity<String> ExceptionHandler(Exception e) throws Exception {
		logger.error(e.getMessage());
		return ResponseUtils.error("操作失败");
	}

}