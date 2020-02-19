package com.fengdis.log.service.impl;

import com.fengdis.log.entity.EventLog;
import com.fengdis.log.entity.OperateLog;
import com.fengdis.log.entity.SecurityLog;
import com.fengdis.log.repository.EventLogDao;
import com.fengdis.log.repository.OperateLogDao;
import com.fengdis.log.repository.SecurityLogDao;
import com.fengdis.log.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LogServiceImpl implements LogService {

	@Autowired
	private SecurityLogDao securityLogDao;

	@Autowired
	private OperateLogDao operateLogDao;

	@Autowired
	private EventLogDao eventLogDao;

	@Override
	public void save(EventLog log) {
		eventLogDao.save(log);
	}

	@Override
	public void save(SecurityLog log) {
		securityLogDao.save(log);
	}

	@Override
	public void save(OperateLog log) {
		operateLogDao.save(log);
	}

	/*@Override
	public Page<SecurityLog> findSecurityLogs(Map<String, Object> queryParam, Pageable pageable) {
		return securityLogDao.findPagerByNameQueryT("SecurityLog.queryPage", queryParam, pageable);
	}

	@Override
	public Page<EventLog> findEventLogs(Map<String, Object> queryParam, Pageable pageable) {
		return eventLogDao.findPagerByNameQueryT("EventLog.queryPage", queryParam, pageable);
	}

	@Override
	public Page<OperateLog> findOperateLogs(Map<String, Object> queryParam, Pageable pageable) {
		return operateLogDao.findPagerByNameQueryT("OperateLog.queryPage", queryParam, pageable);
	}*/

	@Override
	public void delSecurityLogBefore(Date startTime) {
		securityLogDao.delByBefore(startTime);
	}

	@Override
	public void clearAllSecurityLog() {
		securityLogDao.deleteAll();
	}

	@Override
	public void delOptLogBefore(Date startTime) {
		operateLogDao.delByBefore(startTime);
	}

	@Override
	public void clearAllOptLog() {
		operateLogDao.deleteAll();
	}

	@Override
	public void delEventLogBefore(Date startTime) {
		eventLogDao.delByBefore(startTime);

	}

	@Override
	public void clearAllEventLog() {
		eventLogDao.deleteAll();
	}

}
