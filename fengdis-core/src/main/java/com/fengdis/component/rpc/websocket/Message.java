package com.fengdis.component.rpc.websocket;

import java.util.Date;

/**
 * @version 1.0
 * @Descrittion: webscoket消息实体类
 * @author: fengdi
 * @since: 2018/8/10 0010 21:02
 */
public class Message {

	public static final String FROM_SYSTEM = "SYSTEM";

	//发送者
	public String from;
	//接收者
	public String to;
	//发送的文本
	public String text;
	//发送日期
	public Date date;

	public Message() {
	}

	public Message(String from, String to, String text, Date date) {
		this.from = from;
		this.to = to;
		this.text = text;
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Message{" +
				"from='" + from + '\'' +
				", to='" + to + '\'' +
				", text='" + text + '\'' +
				", date=" + date +
				'}';
	}
}
