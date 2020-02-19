package com.fengdis.common.service;

import com.qiniu.storage.model.BucketInfo;
import com.qiniu.storage.model.FileInfo;

import java.util.List;

/**
 * 可自行扩展
 * @author Zheng Jie
 * @date 2018-12-10
 */
public interface QiNiuYunService {

    List<String> findBuckets();

    BucketInfo findBucket(String bucketName);

    List<FileInfo> findFiles(String bucketName, String prefix, int limit);

    FileInfo findFile(String bucketName, String fileKey);

}
