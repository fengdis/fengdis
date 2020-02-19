package com.fengdis.log.repository;

import com.fengdis.log.entity.OperateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface OperateLogDao extends JpaRepository<OperateLog, String> {

	@Modifying
	@Query("delete from OperateLog o where o.hapendTime < :startTime")
	void delByBefore(@Param(value = "startTime") Date startTime);
}
