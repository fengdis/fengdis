package com.fengdis.util;

import org.apache.commons.beanutils.ConvertUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version 1.0
 * @Descrittion: 字符串转集合工具类
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public interface ConverterUtils {

	/**
	 * 将数字逗号组成的字符串（"1,2,3..."）转为List<T>
	 * @param ids
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T> List<T> convert2List(String ids, Class<T> clazz) {
		String idArr[] = ids.split(",");
		T[] idArray = (T[]) ConvertUtils.convert(idArr, clazz);
		return Arrays.asList(idArray);
	}

	/**
	 * 正则表达式
	 * @param source
	 * @param regex
	 * @return
	 */
	static boolean isMatcher(String source, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		return matcher.find();
	}

	static <T> T setParams(Class<T> clazz,Object[] args) throws Exception{
		if(clazz == null || args == null){
			throw new IllegalArgumentException();
		}
		T t = clazz.newInstance();
		Field[] fields = clazz.getDeclaredFields();
		if(fields == null || fields.length > args.length){
			throw new IndexOutOfBoundsException();
		}
		for(int i=0;i<fields.length;i++){
			fields[i].setAccessible(true);
			fields[i].set(t,args[i]);
		}
		return t;
	}

	static <T> List<Object> getParam(T t) throws Exception{
		List<Object> params = new ArrayList<>();
		Class<?> clazz = t.getClass();
		Field[] fields = t.getClass().getDeclaredFields();
		for(Field field : fields){
			PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(),clazz);
			Method method = propertyDescriptor.getReadMethod();
			params.add(method.invoke(t));
		}
		return params;
	}
	
}
