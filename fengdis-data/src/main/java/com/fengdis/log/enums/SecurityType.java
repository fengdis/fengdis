package com.fengdis.log.enums;

import com.fengdis.enums.IEnum;

/**
 * @version 1.0
 * @Descrittion: 安全操作级别枚举
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public enum SecurityType implements IEnum {
	ALL("全部", "-1"), LOGIN("登录", "0"), LOGOUT("退出", "1");

	private String text;
	private String code;

	private SecurityType(String text, String code) {
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
