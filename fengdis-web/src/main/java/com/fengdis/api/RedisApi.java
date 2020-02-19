package com.fengdis.api;

import com.fengdis.common.ResponseUtils;
import com.fengdis.vo.RedisVo;
import com.fengdis.common.service.RedisService;
import com.fengdis.log.annotation.OperateLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
public class RedisApi {

    private static final Logger logger = LoggerFactory.getLogger(UserApi.class);

    @Autowired
    private RedisService redisService;

    @OperateLog(name = "查询所有Redis缓存")
    @GetMapping(value = "/redis/all")
    public ResponseEntity<String> getRedisAll(){
        return ResponseUtils.success(redisService.findAll());
    }

    @OperateLog(name = "查询Redis缓存")
    @GetMapping(value = "/redis")
    public ResponseEntity<String> getRedis(String key, Pageable pageable){
        return ResponseUtils.success(redisService.findByKey(key,pageable));
    }

    @OperateLog(name = "删除Redis缓存")
    @DeleteMapping(value = "/redis")
    public ResponseEntity<String> delete(@RequestBody RedisVo resources){
        redisService.delete(resources.getKey());
        return ResponseUtils.success(HttpStatus.OK);
    }

    @OperateLog(name = "清空Redis缓存")
    @DeleteMapping(value = "/redis/all")
    public ResponseEntity<String> deleteAll(){
        redisService.flushdb();
        return ResponseUtils.success(HttpStatus.OK);
    }
}
