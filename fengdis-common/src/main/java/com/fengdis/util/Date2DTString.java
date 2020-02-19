package com.fengdis.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @version 1.0
 * @Descrittion: 将服务端Date类型转化为客户端字符串格式yyyy-MM-dd HH:mm:ss
 * @author: fengdi
 * @since: 2018/8/10 0010 21:02
 */
public class Date2DTString extends JsonSerializer<Date> {

	public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException, JsonProcessingException {
		jsonGenerator.writeString(format.format(date));
	}
}