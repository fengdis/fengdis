package com.fengdis.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.map.CaseInsensitiveMap;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @version 1.0
 * @Descrittion: String工具类
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

	/**
	 * 把中文转成Unicode码
	 * @param str
	 * @return
	 */
	public static String string2Unicode(String str) {
		String result = "";
		for (int i = 0; i < str.length(); i++) {
			int chr1 = (char) str.charAt(i);
			if (chr1 >= 19968 && chr1 <= 171941) {// 汉字范围 \u4e00-\u9fa5 (中文)
				result += "\\u" + Integer.toHexString(chr1);
			} else {
				result += str.charAt(i);
			}
		}
		return result;
	}

	/**
	 * 判断是否为中文字符
	 *
	 * @param c
	 * @return
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}


	/**
	 * Unicode转中文
	 * @param unicode
	 * @return
	 */
	public static String unicode2String(final String unicode) {
		StringBuffer string = new StringBuffer();

		String[] hex = unicode.split("\\\\u");

		for (int i = 0; i < hex.length; i++) {

			try {
				// 汉字范围 \u4e00-\u9fa5 (中文)
				if(hex[i].length()>=4){//取前四个，判断是否是汉字
					String chinese = hex[i].substring(0, 4);
					try {
						int chr = Integer.parseInt(chinese, 16);
						boolean isChinese = isChinese((char) chr);
						//转化成功，判断是否在  汉字范围内
						if (isChinese){//在汉字范围内
							// 追加成string
							string.append((char) chr);
							//并且追加  后面的字符
							String behindString = hex[i].substring(4);
							string.append(behindString);
						}else {
							string.append(hex[i]);
						}
					} catch (NumberFormatException e1) {
						string.append(hex[i]);
					}

				}else{
					string.append(hex[i]);
				}
			} catch (NumberFormatException e) {
				string.append(hex[i]);
			}
		}

		return string.toString();
	}

	public static String pin(String chinese) throws Exception {
		String pinyin = "";
		HanyuPinyinOutputFormat pinyinOutputFormat = new HanyuPinyinOutputFormat();
		pinyinOutputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		pinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		String[] pinyinArray = null;
		for(char ch : chinese.toCharArray()){
			pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch,pinyinOutputFormat);
			pinyin += ComUtils.isEmpty(pinyinArray) ? ch : pinyinArray[0];
		}
		return pinyin;
	}

	/**
	 * 获取方法中指定注解的value值返回
	 * @param method 方法名
	 * @param validationParamValue 注解的类名
	 * @return
	 */
	public static String getMethodAnnotationOne(Method method, String validationParamValue) {
		String retParam =null;
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		for (int i = 0; i < parameterAnnotations.length; i++) {
			for (int j = 0; j < parameterAnnotations[i].length; j++) {
				String str = parameterAnnotations[i][j].toString();
				if(str.indexOf(validationParamValue) >0){
					retParam = str.substring(str.indexOf("=")+1,str.indexOf(")"));
				}
			}
		}
		return retParam;
	}

	/**
	 * 将utf-8编码的汉字转为中文
	 * @author zhaoqiang
	 * @param str
	 * @return
	 */
	public static String utf8Decoding(String str){
		String result = str;
		try
		{
			result = URLDecoder.decode(str, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public static boolean checkURLAddress(String url) {
		String pattern = "([h]|[H])([t]|[T])([t]|[T])([p]|[P])([s]|[S]){0,1}://([^:/]+)(:([0-9]+))?(/\\S*)*";
		return url.matches(pattern);
	}

	public static boolean checkEmail(String email) {
		if (ComUtils.isEmpty(email)) {
			return false;
		}
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}


	/**
	 * 编码文件名 根据request来判断类型返回
	 * 
	 * @param fileName
	 *            文件名字
	 * @param request
	 *            request对象
	 * @return 编码后的文件名
	 */
	public static String encodeFileName(String fileName, HttpServletRequest request) {
		String agent = (String) request.getHeader("USER-AGENT");
		try {
			if (agent != null && agent.indexOf("MSIE") == -1) { // FF
				fileName = "=?UTF-8?B?" + (new String(Base64.encodeBase64(fileName.getBytes("UTF-8")))) + "?=";
			} else {// ie
				fileName = new String(fileName.getBytes("GBK"), "iso-8859-1");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}

	public static Integer emptyConvert(Object obj, int target) {
		return (obj == null) ? target : Integer.valueOf(obj.toString());
	}

	/**
	 * 功能:判断字符串是否为数字
	 * 
	 * @param srcString
	 *            源字符串
	 * @return
	 */
	public static boolean isNumeric(Object srcString) {
		return isNumeric(StringUtils.nvlString(srcString));
	}

	public static boolean isNumeric(String srcString) {
		boolean returnVal = false;
		if (isNotBlank(srcString)) {
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher isNum = pattern.matcher(srcString);
			if (!isNum.matches()) {
				returnVal = false;
			} else {
				returnVal = true;
			}
		} else {
			returnVal = false;
		}
		return returnVal;
	}

	/**
	 * @param srcString
	 *            源字符串
	 * @param replaceMap
	 *            变量对应表
	 * @return
	 */
	public static String replaceVariable(String srcString, Map<String, Object> replaceMap) {
		replaceMap = formatMap(replaceMap);
		// replaceMap = new CaseInsensitiveMap(replaceMap);
		String resultStr = nvlString(srcString).trim();
		// List<String> subColumn = getSubColumn(resultStr);
		for (String name : replaceMap.keySet()) {
			resultStr = resultStr.replace("#{" + name + "}", nvlString(replaceMap.get(name)));
		}
		return resultStr;
	}

	/**
	 *
	 * @param srcString
	 *            待替换的字符串 如: srcString "hello ${name},say ${word}"
	 * @param replaceString
	 *            替换的方式 如: replaceString "name=hyk","word=good" 注意是可变数组
	 * @return
	 */
	public static String replaceVariable(String srcString, String... replaceString) {
		String resultStr = srcString.trim();
		for (String str : replaceString) {
			String name = str.substring(0, str.indexOf("="));
			String value = str.substring(str.indexOf("=") + 1, str.length());
			resultStr = resultStr.replace("#{" + name + "}", value);
		}
		return resultStr;
	}

	/**
	 * @param srcString
	 *            源字符串
	 * @param replaceMap
	 *            变量对应表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String replaceAllVariable(String srcString, Map<String, Object> replaceMap) {
		replaceMap = formatMap(replaceMap);
		replaceMap = new CaseInsensitiveMap(replaceMap);
		String resultStr = nvlString(srcString).trim();
		List<String> subColumn = getSubColumn(resultStr);
		for (String name : subColumn) {
			resultStr = resultStr.replace("#{" + name + "}", nvlString(replaceMap.get(name)));
		}
		return resultStr;
	}

	/**
	 * 将string空值替换成replaceStr
	 * 
	 * @param str
	 * @param replaceStr
	 * @return
	 */
	public static String replaceEmpty(String str, String replaceStr) {
		if (StringUtils.isBlank(str)) {
			str = replaceStr;
		}
		return str;
	}

	/**
	 * Map的value是list类型转为'A','b'格式
	 * 
	 * @param jsonMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> formatMap(Map<String,Object> jsonMap) {
		Map<String,Object> resMap = new HashMap<>();
		Set<String> set = jsonMap.keySet();
		for (String key : set) {
			if (jsonMap.get(key) instanceof String) {
				resMap.put(key, jsonMap.get(key));
			}
			if (jsonMap.get(key) instanceof List) {
				List<String> list = (List<String>) jsonMap.get(key);
				resMap.put(key, "'" + StringUtils.join(list, "','") + "'");
			}
		}
		return resMap;
	}

	public static String emptyConvert(Object obj, String target) {
		return isBlank(nvlString(obj)) ? target : nvlString(obj);
	}

	public static String nvlString(Object obj) {
		return obj == null ? EMPTY : String.valueOf(obj).trim();
	}

	/**
	 * MD5加密方法
	 * 
	 * @param src
	 *            源字符串
	 * @return
	 */
	public static String securityMD5(String src) {
		String resultString = null;
		if (src == null)
			return "";
		resultString = new String(src);
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(src.getBytes());
			byte[] digest = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < digest.length; i++) {
				sb.append(Integer.toHexString(((int) digest[i]) & 0xFF));
			}
			resultString = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return resultString;
	}

	/**
	 * bean 转string 方便调试使用
	 * 
	 * @param obj
	 * @return
	 */
	public static Map<String,String> beanToString(Object obj) {
		try {
			return BeanUtils.describe(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String substring(Object str, int start, int end) {
		return substring(nvlString(str), start, end);
	}

	public static Date convertStrToDate(String strdate) {
		if (strdate == null)
			return null;
		strdate = strdate.replaceAll("\\W", "");
		if (StringUtils.isNumeric(strdate)) {
			List<String> list = new ArrayList<>();
			list.add("yyyyMMddHHmmdd");
			list.add("yyyyMMddHHmm");
			list.add("yyyyMMddHH");
			list.add("yyyyMMdd");
			list.add("yyyyMM");
			list.add("yyyy");
			for (String format : list) {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				try {
					Date parse = sdf.parse(strdate);
					return parse;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static String convertDateToStrUTC(Date date, String target) {
		if (date == null) {
			return target;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String format = sdf.format(date);
		return format;
	}

	public static String findStrByReg(String findStr, String regEx) {
		Pattern pat = Pattern.compile(regEx);
		Matcher mat = pat.matcher(findStr);
		String str = "";
		while (mat.find()) {
			str += mat.group();
		}
		return str;
	}

	public static List<String> getSubColumn(String str) {
		List<String> list = new ArrayList<>();
		int index = 0;
		while (true) {
			if (str.indexOf("#{", index) == -1) {
				break;
			}
			int start = str.indexOf("#{", index);
			int end = str.indexOf("}", index);
			index = end + 1;
			String substring = str.substring(start + 2, end);
			list.add(substring);
		}
		return list;
	}

	public static String replaceSql(String sql, List<String> columnList) {
		for (String field : columnList) {
			sql = sql.replace("#{" + field + "}", "?");
		}
		return sql;
	}

	public static String dateTimeProcessor(String dateStr, boolean isStartTime) {
		String returnDateStr = null;
		if (dateStr.length() == 8) {
			if (isStartTime) {
				dateStr = dateStr + "000000";
			} else {
				dateStr = dateStr + "235959";
			}
		}
		Date date = StringUtils.convertStrToDate(dateStr);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		if (date != null) {
			returnDateStr = sdf.format(date);
		}
		return returnDateStr;
	}

	/**
	 * 压缩字符串
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String compress(String str) throws Exception {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes());
		gzip.close();
		out.toByteArray();
		return out.toString("ISO-8859-1");
	}

	public static byte[] compressToByte(String str) throws Exception {
		if (str == null || str.length() == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes());
		gzip.close();
		return out.toByteArray();
	}

	public static String uncompressToByte(byte[] b) throws Exception {
		if (b == null) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(b);
		GZIPInputStream ip = new GZIPInputStream(in);
		byte[] buffer = new byte[2560];
		int n;
		while ((n = ip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		return new String(out.toByteArray(), "GBK");
	}

	/**
	 * 解压字符串
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String uncompress(String str) throws Exception {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
		GZIPInputStream ip = new GZIPInputStream(in);
		byte[] buffer = new byte[2560];
		int n;
		while ((n = ip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		return out.toString();
	}

	public static int stringToSeconds(String date_time) {
		int second = 0;
		int index_h = date_time.indexOf('时');
		int index_m = date_time.indexOf('分');
		int index_s = date_time.indexOf('秒');
		if (index_h > 0 && index_m > 0 && index_s > 0) {
			second = Integer.parseInt(date_time.substring(0, index_h)) * 3600
					+ Integer.parseInt(date_time.substring(index_h + 1, index_m - index_h + 1)) * 60
					+ Integer.parseInt(date_time.substring(index_m + 1, index_s)) * 1;
		} else if (index_h < 0 && index_m > 0 && index_s > 0) {
			second = Integer.parseInt(date_time.substring(0, index_m)) * 60
					+ Integer.parseInt(date_time.substring(index_m + 1, index_s)) * 1;
		} else if (index_h > 0 && index_m < 0 && index_s > 0) {
			second = Integer.parseInt(date_time.substring(0, index_h)) * 3600
					+ Integer.parseInt(date_time.substring(index_h + 1, index_s)) * 1;
		} else if (index_h > 0 && index_m > 0 && index_s < 0) {
			second = Integer.parseInt(date_time.substring(0, index_h)) * 3600
					+ Integer.parseInt(date_time.substring(index_h + 1, index_m)) * 60;
		} else if (index_h > 0 && index_m < 0 && index_s < 0) {
			second = Integer.parseInt(date_time.substring(0, index_h)) * 3600;
		} else if (index_h < 0 && index_m > 0 && index_s < 0) {
			second = Integer.parseInt(date_time.substring(0, index_m)) * 60;
		} else if (index_m < 0 && index_h < 0 && index_s > 0) {
			second = Integer.parseInt(date_time.substring(0, index_s)) * 1;
		} else {
			second = Integer.parseInt(date_time) * 1;
		}
		return second;
	}

	public static Date stringToDate(String time, boolean isStartTime) {
		if (StringUtils.nvlString(time).length() <= 4) {
			return null;
		}
		SimpleDateFormat formatter;
		time = time.trim();
		formatter = new SimpleDateFormat("yyyy.MM.dd G 'at' hh:mm:ss z");
		//int tempPos = time.indexOf("-");
		if ((time.indexOf("/") > -1) && (time.indexOf(" ") > -1)) {
			formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		} else if ((time.indexOf("-") > -1) && (time.indexOf(" ") > -1)) {
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		} else if ((time.indexOf("/") > -1) && (time.indexOf("am") > -1) || (time.indexOf("pm") > -1)) {
			formatter = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss a");
		} else if ((time.indexOf("-") > -1) && (time.indexOf("am") > -1) || (time.indexOf("pm") > -1)) {
			formatter = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss a");
		} else if ((time.indexOf("/") == 3) && (time.lastIndexOf("/") == 7) || ((time.lastIndexOf("/") == 8))) {
			formatter = new SimpleDateFormat("yyyy/MM/dd");
		} else {
			time = time.replaceAll("\\W", "");
			System.out.println("time::::::::::::::" + time);
			if (time.length() == 10) {
				formatter = new SimpleDateFormat("MMddHHmmss");
			} else if (time.length() == 4) {
				if (isStartTime) {
					time = time + "0101000000";
				} else {
					time = time + "1231235959";
				}
				formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			} else if (time.length() == 6) {
				if (isStartTime) {
					time = time + "01000000";
				} else {
					time = time + "31235959";
				}
				formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			}
			if (time.length() == 8) {
				if (isStartTime) {
					time = time + "000000";
				} else {
					time = time + "235959";
				}
				formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			} else if (time.length() == 10) {
				if (isStartTime) {
					time = time + "0000";
				} else {
					time = time + "5959";
				}
				formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			} else if (time.length() == 12) {
				if (isStartTime) {
					time = time + "00";
				} else {
					time = time + "59";
				}
				formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			} else if (time.length() == 14) {
				formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			}
		}
		ParsePosition pos = new ParsePosition(0);
		Date ctime = formatter.parse(time, pos);

		return ctime;
	}

	/**
	 * 全角转半角 全角空格为12288, 半角空格为32, 其他字符半角(33-126)与全角(65281-65374)的对应关系是:均相差65248
	 * 
	 * @param input
	 * @return
	 */
	public static String toDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375) {
				c[i] = (char) (c[i] - 65248);
			}
		}
		return new String(c);
	}

	public static String cutLastZero(String src) {
		int length = src.length();
		if (length <= 2) {
			src = src.replace("[0]*$", "");
		} else {
			src = src.replaceAll("[0]{2}$", "").replaceAll("[0]{2}$", "").replaceAll("[0]{2}$", "")
					.replaceAll("[0]{2}$", "").replaceAll("[0]{2}$", "");
		}
		return src;
	}

	public static String getBirthdateByAge(String age, boolean isLow) {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String str = null;
		str = StringUtils.nvlString(year - Integer.parseInt(age));
		if (isLow) {
			str += "0101000000";
		} else {
			str += "1231235959";
		}
		return str;
	}

	public static String queryCodeNameSql(String codeTableName, String codes) {
		String[] split = codes.split(",");
		List<String> list = new ArrayList<>();
		for (int i = 0; i < split.length; i++) {
			list.add("'" + split[i] + "'");
		}
		return "SELECT STRAGG(NAME) NAME FROM " + codeTableName + " WHERE CODE IN (" + StringUtils.join(list, ",")
				+ ")";
	}
}
