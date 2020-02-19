package com.fengdis.log.repository;

import com.fengdis.log.entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface EventLogDao extends JpaRepository<EventLog, String> {
	
	@Modifying
	@Query("delete from EventLog e where e.hapendTime < :startTime")
	void delByBefore(@Param(value = "startTime") Date startTime);
}
