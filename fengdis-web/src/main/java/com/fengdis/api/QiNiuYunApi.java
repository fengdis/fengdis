package com.fengdis.api;

import com.fengdis.common.ResponseUtils;
import com.fengdis.common.service.QiNiuYunService;
import com.fengdis.log.annotation.OperateLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qiniuyun")
public class QiNiuYunApi {

    private static final Logger logger = LoggerFactory.getLogger(UserApi.class);

    @Autowired
    private QiNiuYunService qiNiuYunService;

    @OperateLog(name = "查询所有bucket")
    @GetMapping(value = "/buckets")
    public ResponseEntity<String> findBuckets(){
        return ResponseUtils.success(qiNiuYunService.findBuckets());
    }

    @OperateLog(name = "查询bucket")
    @GetMapping(value = "/bucket/{bucketName}")
    public ResponseEntity<String> findBucket(@PathVariable String bucketName){
        return ResponseUtils.success(qiNiuYunService.findBucket(bucketName));
    }

    @OperateLog(name = "查询所有file")
    @GetMapping(value = "/files")
    public ResponseEntity<String> findFiles(String bucketName,String fileKey,int limit){
        return ResponseUtils.success(qiNiuYunService.findFiles(bucketName,fileKey,limit));
    }

    @OperateLog(name = "查询file")
    @GetMapping(value = "/file/{bucketName}/{fileKey}")
    public ResponseEntity<String> findFile(@PathVariable String bucketName,@PathVariable String fileKey){
        return ResponseUtils.success(qiNiuYunService.findFile(bucketName,fileKey));
    }

}
