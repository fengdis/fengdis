package com.fengdis.log.enums;

import com.fengdis.enums.IEnum;

/**
 * @version 1.0
 * @Descrittion: 异常日志级别枚举
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public enum EventType implements IEnum {
	ALL("全部", "-1"), INFO("信息", "0"), ERROR("错误", "1"), WARN("警告", "2");
	private String text;
	private String code;

	private EventType(String text, String code) {
		this.text = text;
		this.code = code;
	}

	@Override
	public String text() {
		return this.text;
	}

	@Override
	public String code() {
		return this.code;
	}

}
