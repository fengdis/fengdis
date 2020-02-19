package com.fengdis.log.enums;

import com.fengdis.enums.IEnum;

/**
 * @version 1.0
 * @Descrittion: 操作结果枚举
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public enum ResultType implements IEnum {
	SUCCESS("成功", "0"), FAIL("失败", "1");
	private String text;
	private String code;

	private ResultType(String text, String code) {
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
