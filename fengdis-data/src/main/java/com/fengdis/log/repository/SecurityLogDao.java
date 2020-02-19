package com.fengdis.log.repository;

import com.fengdis.log.entity.SecurityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface SecurityLogDao extends JpaRepository<SecurityLog, String>/* extends BaseDao<SecurityLog, String>*/ {
	
	@Modifying
	@Query("delete from SecurityLog s where s.hapendTime < :startTime")
	void delByBefore(@Param(value = "startTime") Date startTime);
}
