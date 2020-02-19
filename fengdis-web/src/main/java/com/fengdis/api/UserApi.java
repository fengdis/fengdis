package com.fengdis.api;

import com.fengdis.common.ResponseUtils;
import com.fengdis.common.dto.UserDTO;
import com.fengdis.common.entity.User;
import com.fengdis.log.annotation.OperateLog;
import com.fengdis.common.service.UserService;
import com.fengdis.util.PageRequestVo;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/user")
public class UserApi {
    
    private static final Logger logger = LoggerFactory.getLogger(UserApi.class);
    private static final Logger service = LoggerFactory.getLogger("service");

    @Autowired
    private UserService userService;

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();

    @RequestMapping(value = "/users",method = RequestMethod.GET)
    public ResponseEntity<String> findAll(){
        List<UserDTO> userDTOS = userService.findAll();
        return ResponseUtils.success(userDTOS);
    }

    @RequestMapping(value = "/user/{id}",method = RequestMethod.GET)
    public ResponseEntity<String> findOne(@PathVariable String id){
        UserDTO userDTO = userService.findOne(id);
        return ResponseUtils.success(userDTO);
    }

    @RequestMapping(value = "/users/findPage",method = RequestMethod.POST)
    public ResponseEntity<String> findPage(@RequestBody PageRequestVo pageRequestVo){
        Page<User> userDTOS = userService.findPage(PageRequestVo.buildPageable(pageRequestVo));
        return ResponseUtils.success(userDTOS);
    }

    @OperateLog(name = "新增",info = "新增")
    @RequestMapping(value = "/user",method = RequestMethod.POST)
    public ResponseEntity<String> insert(@Validated @RequestBody User user, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ResponseUtils.error(bindingResult.getFieldError().getDefaultMessage());
        }
        UserDTO userDTO = userService.insert(user);
        return ResponseUtils.success(userDTO);
    }

    @OperateLog(name = "更新",info = "更新")
    @RequestMapping(value = "/user",method = RequestMethod.PUT)
    public ResponseEntity<String> update(@Validated(User.Update.class) @RequestBody User user, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ResponseUtils.error(bindingResult.getFieldError().getDefaultMessage());
        }
        UserDTO userDTO = userService.update(user);
        return ResponseUtils.success(userDTO);
    }

    @OperateLog(name = "删除",info = "删除")
    @RequestMapping(value = "/user/{id}",method = RequestMethod.DELETE)
    public ResponseEntity<String> delete(@PathVariable String id){
        userService.delete(id);
        return ResponseUtils.success("删除成功");
    }

}
