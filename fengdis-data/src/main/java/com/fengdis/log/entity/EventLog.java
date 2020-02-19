package com.fengdis.log.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fengdis.log.enums.EventType;
import com.fengdis.util.Date2DTString;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @version 1.0
 * @Descrittion: 异常日志实体
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
@Entity
@Table(name = "tb_log_event")
@EntityListeners({ AuditingEntityListener.class })
public class EventLog implements Serializable {

	private static final long serialVersionUID = 107680660747040911L;

	@Id
	/*@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "log_gen")
	@SequenceGenerator(name = "log_gen", sequenceName = "demo_log_seq")
	private Long id;*/
	@GeneratedValue(generator = "EventLogGenerator")
	@GenericGenerator(name = "EventLogGenerator", strategy = "uuid")
	@Column(length = 32)
	private String id;

	@Column(nullable = false, length = 20)
	private String name;

	@Column
	private String info;

	@Column(length = 20)
	@CreatedBy
	private String account;

	@Column(nullable = false, length = 20)
	private String clientIp;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	@JsonSerialize(using = Date2DTString.class)
	private Date hapendTime;

	private String method;

	@Column(columnDefinition = "text")
	private String params;

	@Column(length = 20)
	private String serverIp;

	@Column
	private String position;

	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private EventType eventType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public Date getHapendTime() {
		return hapendTime;
	}

	public void setHapendTime(Date hapendTime) {
		this.hapendTime = hapendTime;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
}
