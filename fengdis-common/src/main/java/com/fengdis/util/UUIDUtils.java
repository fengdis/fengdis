package com.fengdis.util;

import java.util.UUID;

/**
 * @version 1.0
 * @Descrittion: UUID工具类
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class UUIDUtils {

	private UUIDUtils() {
	}

	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("\\-", "");
	}

	public static String getUpperUUID() {
		return UUID.randomUUID().toString().replaceAll("\\-", "").toUpperCase();
	}

	public static String getLowerUUID() {
		return UUID.randomUUID().toString().replaceAll("\\-", "").toLowerCase();
	}

}
