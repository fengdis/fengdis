package com.fengdis.common.service.impl;

import com.fengdis.common.service.QiNiuYunService;
import com.fengdis.service.QiNiuCloudService;
import com.qiniu.storage.model.BucketInfo;
import com.qiniu.storage.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QiNiuYunServiceServiceImpl implements QiNiuYunService {

    @Autowired
    private QiNiuCloudService qiNiuCloudService;

    @Override
    public List<String> findBuckets() {
        return qiNiuCloudService.listBuckets();
    }

    @Override
    public BucketInfo findBucket(String bucketName) {
        return qiNiuCloudService.getBucketInfo(bucketName);
    }

    @Override
    public List<FileInfo> findFiles(String bucketName,String prefix,int limit) {
        return qiNiuCloudService.listFilesOfBucket(bucketName,prefix,limit);
    }

    @Override
    public FileInfo findFile(String bucketName, String fileKey) {
        return qiNiuCloudService.findFile(bucketName,fileKey);
    }

}
