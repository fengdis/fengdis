package com.fengdis.api;

import com.fengdis.annotation.Limit;
import com.fengdis.common.ResponseUtils;
import com.fengdis.log.annotation.LoginLog;
import com.fengdis.log.annotation.LogoutLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/login")
public class LoginApi {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginApi.class);

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();

    /**
     * 测试限流注解，下面配置说明该接口 60秒内最多只能访问 10次，保存到redis的键名为 limit_test，
     */
    @Limit(key = "login", period = 60, count = 3, name = "loginLimit", prefix = "limit")
    @LoginLog
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public ResponseEntity<String> login(){
        return ResponseUtils.success(ATOMIC_INTEGER.incrementAndGet());
    }

    @LogoutLog
    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public ResponseEntity<String> logout(){
        return ResponseUtils.success();
    }

}
