package com.fengdis.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @version 1.0
 * @Descrittion: bean的属性拷贝工具类
 * @author: fengdi
 * @since: 2018/8/10 0010 21:02
 */
public class BeanCopyUtils {

	private static final Logger logger = LoggerFactory.getLogger(BeanCopyUtils.class);

	/** bean嵌套 */
	private static final String NESTED = ".";

	/**
	 * 复制bean的属性（支持嵌套属性，以点号分割）
	 * 
	 * @param source
	 *            拷贝属性的源对象
	 * 
	 * @param dest
	 *            拷贝属性的目的地对象
	 * 
	 * @param includeProperties
	 *            拷贝的属性列表
	 * @param source
	 * @param dest
	 * @param includeProperties
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws IntrospectionException
	 */
	public static final void copyIncludeProperties(final Object source, Object dest, final String[] includeProperties)
			throws Exception {
		if (includeProperties == null || includeProperties.length == 0) {
			throw new IllegalArgumentException("未传入要拷贝的属性列表");
		}
		if (source == null) {
			throw new IllegalArgumentException("要拷贝的源对象为空");
		}
		if (dest == null) {
			throw new IllegalArgumentException("要拷贝的目的对象为空");
		}
		// 日志信息
		if (logger.isTraceEnabled()) {
			logger.trace("[source bean: " + source.getClass().getName() + " ]");
			logger.trace("[destination bean: " + dest.getClass().getName() + " ]");
		}
		// 拷贝
		for (String property : includeProperties) {
			PropertyDescriptor sourcePropertyDescriptor = null;
			PropertyDescriptor destPropertyDescriptor = null;
			if (isSimpleProperty(property)) { // 简单属性
				sourcePropertyDescriptor = getProperty(property, source);
				destPropertyDescriptor = getProperty(property, dest);
				if (sourcePropertyDescriptor == null) {
					throw new IllegalArgumentException("要拷贝的源对象不存在该属性");
				}
				if (destPropertyDescriptor == null) {
					throw new IllegalArgumentException("要拷贝到的目标对象不存在该属性");
				}
				copyProperty(source, dest, property);
			} else { // 嵌套bean属性
				Object target = dest;
				Object realSource = source;
				String[] nestedProperty = getNestedProperty(property);
				if (nestedProperty != null && nestedProperty.length > 1) {
					for (int i = 0; i < nestedProperty.length - 1; i++) {
						sourcePropertyDescriptor = getProperty(nestedProperty[i], realSource);
						destPropertyDescriptor = getProperty(nestedProperty[i], target);
						if (sourcePropertyDescriptor == null) {
							throw new IllegalArgumentException("要拷贝的源对象不存在该属性");
						}
						if (destPropertyDescriptor == null) {
							throw new IllegalArgumentException("要拷贝到的目标对象不存在该属性");
						}
						Method readMethod = sourcePropertyDescriptor.getReadMethod();
						realSource = readMethod.invoke(realSource);
						readMethod = destPropertyDescriptor.getReadMethod();
						Method writeMethod = destPropertyDescriptor.getWriteMethod();
						Object value = readMethod.invoke(target);
						if (value == null) {
							value = destPropertyDescriptor.getPropertyType().newInstance();
							writeMethod.invoke(target, value);
						}
						target = value;
					}
					final String prop = nestedProperty[nestedProperty.length - 1];
					sourcePropertyDescriptor = getProperty(prop, realSource);
					destPropertyDescriptor = getProperty(prop, target);
					if (sourcePropertyDescriptor == null) {
						throw new IllegalArgumentException("要拷贝的源对象不存在该属性");
					}
					if (destPropertyDescriptor == null) {
						throw new IllegalArgumentException("要拷贝到的目标对象不存在该属性");
					}
					copyProperty(realSource, target, prop);
				}
			}
		}
	}

	/**
	 * 复制bean的属性（支持嵌套属性，以点号分割）
	 * 
	 * @param source
	 *            拷贝属性的源对象
	 * 
	 * @param dest
	 *            拷贝属性的目的地对象
	 * 
	 * @param excludeProperties
	 *            拷贝的属性列表
	 * @param source
	 * @param dest
	 * @param excludeProperties
	 * @throws IntrospectionException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void copyExcludeProperties(final Object source, final Object dest, final String... excludeProperties)
			throws Exception {
		final Object backupSource = clone(dest);
		if (source == null) {
			throw new IllegalArgumentException("要拷贝的源对象为空");
		}
		if (dest == null) {
			throw new IllegalArgumentException("要拷贝的目的对象为空");
		}

		BeanCopyUtils.copyExcludeProperties(dest, source);
		// 还原排除的属性值
		revertProperties(backupSource, dest, excludeProperties);
	}

	/**
	 * * 从备份对象中还原属性
	 * 
	 * @param backup
	 *            备份bean
	 * 
	 * @param target
	 *            目标bean
	 * 
	 * @param properties
	 *            属性列表
	 * @param backup
	 * @param target
	 * @param properties
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IntrospectionException
	 * @throws InstantiationException
	 */
	private static void revertProperties(final Object backup, Object target, final String... properties)
			throws Exception {
		if (properties == null || properties.length == 0) {
			return;
		}
		if (backup == null) {
			throw new IllegalArgumentException("备份对象为空");
		}
		if (target == null) {
			throw new IllegalArgumentException("目的对象为空");
		}
		// 日志信息
		if (logger.isTraceEnabled()) {
			logger.trace("[source bean: " + backup.getClass().getName() + " ]");
			logger.trace("[destination bean: " + target.getClass().getName() + " ]");
		}
		// 拷贝
		for (String property : properties) {
			PropertyDescriptor sourcePropertyDescriptor = null;
			PropertyDescriptor destPropertyDescriptor = null;
			if (isSimpleProperty(property)) { // 简单属性
				sourcePropertyDescriptor = getProperty(property, backup);
				destPropertyDescriptor = getProperty(property, target);
				if (sourcePropertyDescriptor == null) {
					throw new IllegalArgumentException("要拷贝的源对象不存在该属性");
				}
				if (destPropertyDescriptor == null) {
					throw new IllegalArgumentException("要拷贝到的目标对象不存在该属性");
				}
				copyProperty(backup, target, property);
			} else { // 嵌套bean属性
				Object targetObj = target;
				Object realBackup = backup;
				String[] nestedProperty = getNestedProperty(property);
				if (nestedProperty != null && nestedProperty.length > 1) {
					for (int i = 0; i < nestedProperty.length - 1; i++) {
						sourcePropertyDescriptor = getProperty(nestedProperty[i], realBackup);
						destPropertyDescriptor = getProperty(nestedProperty[i], targetObj);
						if (sourcePropertyDescriptor == null) {
							throw new IllegalArgumentException("要拷贝的源对象不存在该属性");
						}
						if (destPropertyDescriptor == null) {
							throw new IllegalArgumentException("要拷贝到的目标对象不存在该属性");
						}
						Method readMethod = sourcePropertyDescriptor.getReadMethod();
						realBackup = readMethod.invoke(realBackup);
						if (realBackup == null) { // 判断备份对象嵌套属性是否为空
							realBackup = sourcePropertyDescriptor.getPropertyType().newInstance();
						}
						Method writeMethod = destPropertyDescriptor.getWriteMethod();
						readMethod = destPropertyDescriptor.getReadMethod();
						Object value = readMethod.invoke(targetObj);
						if (value == null) {
							value = destPropertyDescriptor.getPropertyType().newInstance();
							writeMethod.invoke(targetObj, value);
						}
						targetObj = value;
					}
					final String prop = nestedProperty[nestedProperty.length - 1];
					sourcePropertyDescriptor = getProperty(prop, realBackup);
					destPropertyDescriptor = getProperty(prop, targetObj);
					if (sourcePropertyDescriptor == null) {
						throw new IllegalArgumentException("要拷贝的源对象不存在该属性");
					}
					if (destPropertyDescriptor == null) {
						throw new IllegalArgumentException("要拷贝到的目标对象不存在该属性");
					}
					copyProperty(realBackup, targetObj, prop);
				}
			}
		}
	}

	/**
	 * 对象克隆
	 * 
	 * @param value
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static Object clone(final Object value) throws IOException, ClassNotFoundException {
		// 字节数组输出流，暂存到内存中
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 序列化
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(value);
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bis);
		// 反序列化
		return ois.readObject();
	}

	/**
	 * 判断是否为简单属性，是，返回ture
	 * 
	 * @param property
	 * @return
	 */
	private static boolean isSimpleProperty(final String property) {
		return !property.contains(NESTED);
	}

	/**
	 * 获取目标bean的属性
	 * 
	 * @param propertyName
	 * @param target
	 * @return
	 */
	private static PropertyDescriptor getProperty(final String propertyName, final Object target) {
		if (target == null) {
			new IllegalArgumentException("查询属性的对象为空");
		}
		if (propertyName == null || "".equals(propertyName)) {
			new IllegalArgumentException("查询属性不能为空值");
		}
		PropertyDescriptor propertyDescriptor = null;
		try {
			propertyDescriptor = new PropertyDescriptor(propertyName, target.getClass());
		} catch (IntrospectionException e) {
			logger.info("不存在该属性");
			return null;
		}
		return propertyDescriptor;
	}

	/**
	 * 单个属性复制--原数据源和目的数据源必须要有该属性方可
	 * 
	 * @param propertyName
	 * @param source
	 * @param dest
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IntrospectionException
	 */
	public static void copyProperty(final Object source, Object dest, final String propertyName) throws Exception {
		PropertyDescriptor property;
		property = new PropertyDescriptor(propertyName, source.getClass());
		Method getMethod = property.getReadMethod();
		Object value = getMethod.invoke(source);
		property = new PropertyDescriptor(propertyName, dest.getClass());
		Method setMethod = property.getWriteMethod();
		setMethod.invoke(dest, value);
	}

	/**
	 * 获取嵌套Bean的属性
	 * 
	 * @param nestedProperty
	 * @return
	 */
	public static String[] getNestedProperty(final String nestedProperty) {
		if (nestedProperty == null || "".equals(nestedProperty)) {
			new IllegalArgumentException("参数为空值");
		}
		return nestedProperty.split("\\" + NESTED);
	}

}