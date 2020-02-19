package com.fengdis.log.annotation;

import com.fengdis.log.enums.EventType;

import java.lang.annotation.*;

/**
 * @version 1.0
 * @Descrittion: 异常日志自定义注解
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventLog {
	String name() default "";

	String info() default "";

	EventType type() default EventType.INFO;

}
