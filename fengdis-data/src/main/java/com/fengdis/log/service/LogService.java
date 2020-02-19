package com.fengdis.log.service;

import com.fengdis.log.entity.EventLog;
import com.fengdis.log.entity.OperateLog;
import com.fengdis.log.entity.SecurityLog;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Transactional(readOnly = true)
public interface LogService {

	@Modifying
	@Transactional
	void save(EventLog log);

	@Modifying
	@Transactional
	void save(SecurityLog log);

	@Modifying
	@Transactional
	void save(OperateLog log);

	@Modifying
	@Transactional
	void delSecurityLogBefore(Date startTime);

	@Modifying
	@Transactional
	void clearAllSecurityLog();

	@Modifying
	@Transactional
	void delOptLogBefore(Date startTime);

	@Modifying
	@Transactional
	void clearAllOptLog();

	@Modifying
	@Transactional
	void delEventLogBefore(Date startTime);

	@Modifying
	@Transactional
	void clearAllEventLog();

	/*Page<SecurityLog> findSecurityLogs(Map<String, Object> queryParam, Pageable pageable);

	Page<EventLog> findEventLogs(Map<String, Object> queryParam, Pageable pageable);

	Page<OperateLog> findOperateLogs(Map<String, Object> queryParam, Pageable pageable);*/

}
