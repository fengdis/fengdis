package com.fengdis.log.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fengdis.log.enums.SecurityType;
import com.fengdis.util.Date2DTString;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @version 1.0
 * @Descrittion: 安全日志实体
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
@Entity
@Table(name = "tb_log_security")
@EntityListeners({ AuditingEntityListener.class })
public class SecurityLog implements Serializable {

	private static final long serialVersionUID = -7091141778874824873L;

	@Id
	/*@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "log_gen")
	@SequenceGenerator(name = "log_gen", sequenceName = "demo_log_seq")
	private Long id;*/
	@GeneratedValue(generator = "SecurityLogGenerator")
	@GenericGenerator(name = "SecurityLogGenerator", strategy = "uuid")
	@Column(length = 32)
	private String id;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date hapendTime;

	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private SecurityType securityType;

	@Column(nullable = false, length = 20)
	private String clientIp;

	// 访问者帐号
	@Column(length = 20)
	private String account;

	/* 附加信息 */
	@Column
	private String info;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonSerialize(using = Date2DTString.class)
	public Date getHapendTime() {
		return hapendTime;
	}

	public void setHapendTime(Date hapendTime) {
		this.hapendTime = hapendTime;
	}

	public SecurityType getSecurityType() {
		return securityType;
	}

	public void setSecurityType(SecurityType securityType) {
		this.securityType = securityType;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
