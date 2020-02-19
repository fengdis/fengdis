package com.fengdis.util;

/**
 * @version 1.0
 * @Descrittion: SM4加密够工具类
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class SMS4Utils {
	
	private static final String KEY = "0123456789abcdef";

	/**
	 * 加密
	 * @param value
	 * @return
	 */
	public static String encrypt(String value) {
		byte[] usernameout = SMS4.encodeSMS4(value, getKeyByte());
		return SMS4.printBit(value, usernameout);
	}

	/**
	 * 解密
	 * @param value
	 * @return
	 */
	public static String decrypt(String value) {
		return SMS4.DecryptString(value, getKeyString());
	}

	/**
	 * 获取加密的私钥key
	 * @return
	 */
	private static byte[] getKeyByte() {
		return KEY.getBytes();
	}

	/**
	 * 获取加密的私钥key
	 * @return
	 */
	private static String getKeyString() {
		return KEY;
	}

}
