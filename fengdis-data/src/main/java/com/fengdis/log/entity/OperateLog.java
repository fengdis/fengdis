package com.fengdis.log.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fengdis.log.enums.OperateType;
import com.fengdis.log.enums.ResultType;
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
 * @Descrittion: 操作日志实体
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
@Entity
@Table(name = "tb_log_operate")
@EntityListeners({ AuditingEntityListener.class })
public class OperateLog implements Serializable {

	private static final long serialVersionUID = -3216447093712453224L;

	@Id
	/*@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "log_gen")
	@SequenceGenerator(name = "log_gen", sequenceName = "demo_log_seq")
	private Long id;*/
	@GeneratedValue(generator = "OperateLogGenerator")
	@GenericGenerator(name = "OperateLogGenerator", strategy = "uuid")
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

	@Column(nullable = false, length = 20)
	private String serverIp;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	@JsonSerialize(using = Date2DTString.class)
	private Date hapendTime;

	@Column(nullable = false, length = 20)
	private Long elapsedTime;

	private String method;

	@Column(columnDefinition = "text")
	private String params;

	@Column(columnDefinition = "text")
	private byte[] exception;

	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private OperateType operateType;

	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private ResultType resultType;

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

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public Date getHapendTime() {
		return hapendTime;
	}

	public void setHapendTime(Date hapendTime) {
		this.hapendTime = hapendTime;
	}

	public Long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(Long elapsedTime) {
		this.elapsedTime = elapsedTime;
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

	public byte[] getException() {
		return exception;
	}

	public void setException(byte[] exception) {
		this.exception = exception;
	}

	public OperateType getOperateType() {
		return operateType;
	}

	public void setOperateType(OperateType operateType) {
		this.operateType = operateType;
	}

	public ResultType getResultType() {
		return resultType;
	}

	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}
}
