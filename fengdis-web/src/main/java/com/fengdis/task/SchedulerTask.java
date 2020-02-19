package com.fengdis.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @Descrittion: 定时任务
 * @author: fengdi
 * @since: 2019/08/28 17:26
 */
@Component
public class SchedulerTask {
    
    private static final Logger logger = LoggerFactory.getLogger(SchedulerTask.class);

    private int count=0;

    /*每10秒执行一次*/
//    @Scheduled(cron="*/10 * * * * ?")
//    private void process(){
//        logger.info("this is scheduler task runing "+(count++));
//    }

    /*每天1点01分执行*/
    @Scheduled(cron="0 01 1 ? * *")
    private void eachDayTrigger(){
        logger.info("-------------------doDayTrigger");
    }

    /*每周周日1点执行*/
    @Scheduled(cron="0 0 1 ? * 7")
    private void eachWeekTrigger(){
        logger.info("-------------------doWeekTrigger");
    }

    /*每月最后一天1点执行*/
    /*@Scheduled(cron="0 0 1 L * ?")
    private void eachMonthTrigger(){
        logger.info("-------------------doMonthTrigger");
    }*/

    /*每月第一天0点执行*/
    @Scheduled(cron="0 0 0 1 * ?")
    private void eachMonthTrigger(){
        logger.info("-------------------doMonthTrigger");
    }
}
