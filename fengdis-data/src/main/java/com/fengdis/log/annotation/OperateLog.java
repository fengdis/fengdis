package com.fengdis.log.annotation;

import com.fengdis.log.enums.OperateType;

import java.lang.annotation.*;

/**
 * @version 1.0
 * @Descrittion: 操作日志自定义注解
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {
	
	String name() default "";

	String info() default "";

	OperateType type() default OperateType.GENERAL;

}
