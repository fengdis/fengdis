package com.fengdis.util;
 
import org.apache.commons.lang3.StringUtils;
 
/**
 * @version 1.0
 * @Descrittion: 数据脱敏工具类
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class DesensitizeUtils {

    private static final String CIPHERTEXT = "*";

    /**
     * 保留前面n位 例如：姓名 张**
     * @param str
     * @param index
     * @return
     */
    public static String left(String str,int index) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        String name = StringUtils.left(str, index);
        return StringUtils.rightPad(name, StringUtils.length(str), CIPHERTEXT);
    }

    /**
     * 前面保留n位明文，后面保留n位明文 例如：身份证号 110****58，前面保留3位明文，后面保留2位明文
     * @param str
     * @param index
     * @param end
     * @return
     */
    public static String around(String str, int index, int end) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        StringBuilder sb = new StringBuilder(CIPHERTEXT);
        for(int i = 1;i < index;i++){
            sb.append(CIPHERTEXT);
        }
        return StringUtils.left(str, index).concat(
                StringUtils.removeStart(
                        StringUtils.leftPad(StringUtils.right(str, end), StringUtils.length(str), CIPHERTEXT), sb.toString()));
    }

    /**
     * 保留后面n位明文 例如：手机号 *******5678
     * @param str
     * @param end
     * @return
     */
    public static String right(String str,int end) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        return StringUtils.leftPad(StringUtils.right(str, end), StringUtils.length(str), CIPHERTEXT);
    }
}