package com.fengdis.api.test;

import com.fengdis.common.ResponseUtils;
import com.fengdis.component.jdbc.JdbcTemplateQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Descrittion:
 * @author: fengdi
 * @since: 2019/09/25 17:55
 */
@Controller
@RequestMapping
public class TestApi {
    @Autowired
    private JdbcTemplateQuery jdbcTemplateQuery;

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> test(){
        List<Map<String, Object>> maps = jdbcTemplateQuery.queryListMapResult("select * from tb_user",new Object[]{});
        return ResponseUtils.success(maps.size());
    }

    @RequestMapping(value = "/test1",method = RequestMethod.GET)
    public String test1(){
        return "redirect:/druid/index";
    }
}
