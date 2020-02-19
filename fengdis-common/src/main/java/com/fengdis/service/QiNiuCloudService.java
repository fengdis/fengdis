package com.fengdis.service;

import com.alibaba.fastjson.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.sms.SmsManager;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.*;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * @version 1.0
 * @Descrittion: 七牛云服务
 * @author: fengdi
 * @since: 2018/8/8 0008 21:21
 */
@Component
public class QiNiuCloudService {

    private static final Logger logger = LoggerFactory.getLogger(QiNiuCloudService.class);

    @Value("${qiniu.ak:''}")
    private String accessKey;

    @Value("${qiniu.sk:''}")
    private String secretKey;

    /*@Value("${qiniu.bucket:''}")
    private String bucket;*/

    /*@Value("${qiniu.zone:''}")
    private String zone;*/

    @Value("${qiniu.hostname:''}")
    private String hostname;

    @Value("${qiniu.style:''}")
    private String style;

    // 区域名称：z0 华东  z1 华北  z2 华南  na0 北美  as0 东南亚
    public enum ZoneType {
        zone0("zone0",Zone.huadong()),
        zone1("zone1",Zone.huabei()),
        zone2("zone2",Zone.huanan()),
        zoneNa0("zoneNa0",Zone.beimei()),
        zoneAs0("zoneAs0",Zone.xinjiapo());

        private String type;
        private Zone zone;

        ZoneType(String type, Zone zone) {
            this.type = type;
            this.zone = zone;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Zone getZone() {
            return zone;
        }

        public void setZone(Zone zone) {
            this.zone = zone;
        }

        public static Zone getZoneByType(String type){
            for(ZoneType zoneType : ZoneType.values()){
                if(zoneType.getType().equals(type)){
                    return zoneType.getZone();
                }
            }
            return null;
        }
    }

    /**
     * 构造一个带指定Zone对象的配置类
     * Zone表示与机房的对应关系
     * 华东	Zone.zone0(),Zone.huadong()
     * 华北	Zone.zone1(),Zone.huabei()
     * 华南	Zone.zone2(),Zone.huanan()
     * 北美	Zone.zoneNa0(),Zone.beimei()
     * 东南亚	Zone.zoneAs0(),Zone.xinjiapo()
     *
     * 现在推荐使用Region
     * 华东	Region.huadong()
     * 华北	Region.huabei()
     * 华南	Region.huanan()
     * 北美	Region.beimei(),Region.regionNa0()
     * 新加坡	Region.xinjiapo(),Region.regionAs0()
     */
    private Configuration cfg = new Configuration(Zone.huabei());
    //private Configuration cfg = new Configuration(Region.huabei());

    private UploadManager uploadManager = new UploadManager(cfg);

    /**
     * 获取当前账号所有空间（buckets）
     * @return
     */
    public List<String> listBuckets(){
        try {
            Auth auth = Auth.create(accessKey, secretKey);
            BucketManager bucketManager = new BucketManager(auth,cfg);
            logger.info("获取七牛云所有空间成功");
            return Arrays.asList(bucketManager.buckets());
        } catch (QiniuException e) {
            logger.error("获取七牛云所有空间异常",e);
            return null;
        }
    }

    public BucketInfo getBucketInfo(String bucketName){
        try {
            Auth auth = Auth.create(accessKey, secretKey);
            BucketManager bucketManager = new BucketManager(auth,cfg);
            logger.info("获取七牛云空间信息成功");
            return bucketManager.getBucketInfo(bucketName);
        } catch (QiniuException e) {
            logger.error("获取七牛云空间信息异常",e);
            return null;
        }
    }

    /**
     * 创建空间
     * @param bucketName
     * @param region
     * @return
     */
    public String createBucket(String bucketName, String region){
        try {
            Auth auth = Auth.create(accessKey, secretKey);
            BucketManager bucketManager = new BucketManager(auth,cfg);
            Response response = bucketManager.createBucket(bucketName, region);
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            logger.info("创建七牛云空间成功");
            return putRet.key;
        } catch (QiniuException e) {
            logger.error("创建七牛云空间异常",e);
            return null;
        }
    }

    /**
     * 获取该空间下所有的domain
     * @param bucket
     * @return
     */
    public List<String> listDomains(String bucket){
        try {
            Auth auth = Auth.create(accessKey, secretKey);
            BucketManager bucketManager = new BucketManager(auth,cfg);
            logger.info(String.format("获取七牛云%s空间下所有的domain成功",bucket));
            return Arrays.asList(bucketManager.domainList(bucket));
        } catch (QiniuException e) {
            logger.error(String.format("获取七牛云%s空间下所有的domain成功",bucket),e);
            return null;
        }
    }

    /**
     * 获取当前账号指定空间（bucket）下的文件列表
     * @param bucketName 空间名
     * @param prefix 文件名前缀
     * @param limit 每次迭代的长度限制，最大1000，推荐值 100
     * @return
     */
    public List<FileInfo> listFilesOfBucket(String bucketName, String prefix, int limit) {
        BucketManager bucketManager = new BucketManager(Auth.create(accessKey, secretKey),cfg);
        List<FileInfo> list = null;
        try {
            BucketManager.FileListIterator it = bucketManager.createFileListIterator(bucketName, prefix, limit, null);
            list = new ArrayList<FileInfo>();
            while (it.hasNext()) {
                FileInfo[] items = it.next();
                if (null != items && items.length > 0) {
                    list.addAll(Arrays.asList(items));
                }
            }
            logger.info(String.format("获取七牛云%s-%s指定目录文件成功",bucketName,prefix));
            return list;
        } catch (Exception e) {
            logger.error(String.format("获取七牛云%s-%s指定目录文件成功",bucketName,prefix),e);
            return null;
        }
    }

    /**
     * 获取当前账号指定空间（bucket）下的文件列表
     * v2 接口可以避免由于大量删除导致的列举超时问题
     * @param bucketName 空间名
     * @param prefix 文件名前缀
     * @param marker 上一次获取文件列表时返回的 marker
     * @param limit 每次迭代的长度限制，推荐值 1000
     * @return
     */
    public List<FileInfo> listFilesOfPrefix(String bucketName, String prefix,String marker, int limit) {
        BucketManager bucketManager = new BucketManager(Auth.create(accessKey, secretKey),cfg);
        try{
            FileListing listing = bucketManager.listFilesV2(bucketName, prefix, null, limit, null);
            if (listing == null || listing.items == null || listing.items.length <= 0) {
                return null;
            }
            logger.info(String.format("获取七牛云%s-%s指定目录文件成功",bucketName,prefix));
            return Arrays.asList(listing.items);
        }catch (QiniuException e){
            logger.error(String.format("获取七牛云%s-%s指定目录文件成功",bucketName,prefix),e);
            return null;
        }
    }

    /**
     * 获取空间中文件的属性
     * @param bucketName 空间名
     * @param fileKey 文件名称
     * @return
     */
    public FileInfo findFile(String bucketName, String fileKey){
        BucketManager bucketManager = new BucketManager(Auth.create(accessKey, secretKey),cfg);
        try {
            logger.info("获取七牛云指定文件成功");
            return bucketManager.stat(bucketName, fileKey);
        } catch (QiniuException e) {
            logger.error("获取七牛云指定文件异常",e);
            return null;
        }
    }

    /**
     * 上传文件
     * @param filePath
     * @param key
     * @param bucketName
     * @return
     */
    public String uploadFile(String filePath, String key, String bucketName) {
        Auth auth = Auth.create(accessKey, secretKey);
        String token = auth.uploadToken(bucketName);
        try {
            Response response = uploadManager.put(filePath, key, token);
            //解析上传成功的结果
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            logger.info(String.format("七牛云上传文件成功：key:%s#hash:%s",putRet.key,putRet.hash));
            return getStyleUrl(putRet,false);
        } catch (QiniuException e) {
            logger.error("七牛云上传文件异常",e);
            return null;
        }
    }

    /**
     * 上传文件
     * @param uploadBytes
     * @param key
     * @param bucketName
     * @return
     */
    public String uploadFile(byte[] uploadBytes, String key, String bucketName) {
        Auth auth = Auth.create(accessKey, secretKey);
        String token = auth.uploadToken(bucketName);
        try {
            Response response = uploadManager.put(uploadBytes, key, token);
            //解析上传成功的结果
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            logger.info(String.format("七牛云上传文件成功：key:%s#hash:%s",putRet.key,putRet.hash));
            return getStyleUrl(putRet,false);
        } catch (QiniuException e) {
            logger.error("七牛云上传文件异常",e);
            return null;
        }
    }

    /**
     * 上传文件
     * @param inputStream
     * @param key
     * @param bucketName
     * @return
     */
    public String uploadFile(InputStream inputStream, String key, String bucketName) {
        /*byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
        ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);*/
        Auth auth = Auth.create(accessKey, secretKey);
        String token = auth.uploadToken(bucketName);
        try {
            Response response = uploadManager.put(inputStream, key, token, null, null);
            //解析上传成功的结果
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            logger.info(String.format("七牛云上传文件成功：key:%s#hash:%s",putRet.key,putRet.hash));
            return getStyleUrl(putRet,false);
        } catch (QiniuException e) {
            logger.error("七牛云上传文件异常",e);
            return null;
        }
    }

    /**
     * 上传文件
     * @param file
     * @param key
     * @param bucketName
     * @return
     */
    public String uploadFile(File file, String key, String bucketName) {
        /*byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
        ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);*/
        Auth auth = Auth.create(accessKey, secretKey);
        String token = auth.uploadToken(bucketName);
        try {
            Response response = uploadManager.put(file, key, token);
            //解析上传成功的结果
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            logger.info(String.format("七牛云上传文件成功：key:%s#hash:%s",putRet.key,putRet.hash));
            return getStyleUrl(putRet,false);
        } catch (QiniuException e) {
            logger.error("七牛云上传文件异常",e);
            return null;
        }
    }

    //断点续传
    /*public String uploadFile(String filePath, String key, String bucketName) {
        *//*byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
        ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);*//*
        Auth auth = Auth.create(accessKey, secretKey);
        String token = auth.uploadToken(bucketName);
        String localTempDir = Paths.get(System.getenv("java.io.tmpdir"), bucketName).toString();
        try {
            //设置断点续传文件进度保存目录
            FileRecorder fileRecorder = new FileRecorder(localTempDir);
            UploadManager uploadManager = new UploadManager(cfg, fileRecorder);
            try {
                Response response = uploadManager.put(filePath, key, token);
                //解析上传成功的结果
                DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class)
                logger.info(String.format("七牛云上传文件成功：key:%s#hash:%s",putRet.key,putRet.hash));
                return putRet.key;
            } catch (QiniuException e) {
                logger.error("七牛云上传文件异常",e);
                return null;
            }
        } catch (IOException e) {
            logger.error("七牛云上传文件异常",e);
            return null;
        }
    }*/

    /**
     * 删除指定空间下的文件
     * @param bucketName 空间名
     * @param key 文件路径+文件名
     * @return
     */
    public String deleteFile(String bucketName, String key) {
        BucketManager bucketManager = new BucketManager(Auth.create(accessKey, secretKey),cfg);
        try {
            Response response = bucketManager.delete(bucketName, key);
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            logger.info(String.format("七牛云删除文件成功：key:%s#hash:%s",putRet.key,putRet.hash));
            return putRet.key;
        } catch (QiniuException e) {
            logger.error("七牛云删除文件异常",e);
            return null;
        }
    }

    /**
     * 修改文件的MimeType
     * @param bucketName 空间名称
     * @param key 文件路径+文件名
     * @param newMimeType 改变的文件类型
     * @return
     */
    public String changeMime(String bucketName,String key,String newMimeType) {
        BucketManager bucketManager = new BucketManager(Auth.create(accessKey, secretKey),cfg);
        try {
            Response response = bucketManager.changeMime(bucketName, key, newMimeType);
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            logger.info(String.format("七牛云修改文件类型成功：key:%s#hash:%s",putRet.key,putRet.hash));
            return putRet.key;
        } catch (QiniuException e) {
            logger.error("七牛云修改文件类型异常",e);
            return null;
        }
    }

    /**
     * 修改文件的元数据
     * @param bucketName 空间名称
     * @param key 文件名称
     * @param headers 需要修改的文件元数据
     * @return
     */
    public String changeHeaders(String bucketName,String key,Map<String, String> headers) {
        BucketManager bucketManager = new BucketManager(Auth.create(accessKey, secretKey),cfg);
        try {
            Response response = bucketManager.changeHeaders(bucketName, key, headers);
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            logger.info(String.format("七牛云修改文件元数据成功：key:%s#hash:%s",putRet.key,putRet.hash));
            return putRet.key;
        } catch (QiniuException e) {
            logger.error("七牛云修改文件元数据异常",e);
            return null;
        }
    }

    /**
     * 修改文件的存储类型（普通存储或低频存储）
     * @param bucketName
     * @param key
     * @param type type=0 表示普通存储，type=1 表示低频存存储
     * @return
     */
    public String changeType(String bucketName, String key, StorageType type){
        BucketManager bucketManager = new BucketManager(Auth.create(accessKey, secretKey),cfg);
        try {
            Response response = bucketManager.changeType(bucketName, key, type);
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            logger.info(String.format("七牛云修改文件存储类型成功：key:%s#hash:%s",putRet.key,putRet.hash));
            return putRet.key;
        } catch (QiniuException e) {
            logger.error("七牛云修改文件存储类型异常",e);
            return null;
        }
    }

    /**
     * 修改文件的状态（禁用或者正常）
     * @param bucketName
     * @param key
     * @param status 0表示启用；1表示禁用。
     * @return
     */
    public String changeStatus(String bucketName, String key, int status){
        BucketManager bucketManager = new BucketManager(Auth.create(accessKey, secretKey),cfg);
        try {
            Response response = bucketManager.changeStatus(bucketName, key, status);
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            logger.info(String.format("七牛云修改文件状态成功：key:%s#hash:%s",putRet.key,putRet.hash));
            return putRet.key;
        } catch (QiniuException e) {
            logger.error("七牛云修改文件状态异常",e);
            return null;
        }
    }

    /**
     * 重命名指定空间下的文件
     * @param bucketName 空间名
     * @param oldKey 原文件路径+文件名
     * @param newKey 新文件路径+文件名
     * @param force  强制覆盖空间中已有同名（和 newFileKey 相同）的文件
     * @return
     */
    public String renameFile(String bucketName, String oldKey, String newKey, boolean force) {
        BucketManager bucketManager = new BucketManager(Auth.create(accessKey, secretKey),cfg);
        try {
            Response response = bucketManager.rename(bucketName, oldKey, newKey, force);
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            logger.info(String.format("七牛云重命名文件成功：key:%s#hash:%s",putRet.key,putRet.hash));
            return putRet.key;
        } catch (QiniuException e) {
            logger.error("七牛云重命名文件异常",e);
            return null;
        }
    }

    /**
     * 复制指定空间下指定文件到目标空间下的指定路径
     * @param bucketName 原空间名
     * @param key 原文件路径+原文件名
     * @param targetBucket 目标空间名
     * @param targetKey 目标文件路径+文件名
     * @param force 强制覆盖空间中已有同名（和 toFileKey 相同）的文件
     */
    public String copyFile(String bucketName, String key, String targetBucket, String targetKey, boolean force)  {
        BucketManager bucketManager = new BucketManager(Auth.create(accessKey, secretKey),cfg);
        try {
            Response response = bucketManager.copy(bucketName, key, targetBucket, targetKey, force);
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            logger.info(String.format("七牛云复制文件成功：key:%s#hash:%s",putRet.key,putRet.hash));
            return putRet.key;
        } catch (QiniuException e) {
            logger.error("七牛云复制文件异常",e);
            return null;
        }
    }

    /**
     * 移动指定空间下的指定文件到目标空间下的指定路径
     * @param bucketName 原空间名
     * @param key 原文件路径+文件名
     * @param targetBucket 目标空间名
     * @param targetKey 目标文件路径+文件名
     * @param force 强制覆盖空间中已有同名（和 toFileKey 相同）的文件
     * @return
     */
    public String moveFile(String bucketName, String key, String targetBucket, String targetKey, boolean force) {
        BucketManager bucketManager = new BucketManager(Auth.create(accessKey, secretKey),cfg);
        try {
            Response response = bucketManager.move(bucketName, key, targetBucket, targetKey,force);
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            logger.info(String.format("七牛云移动文件成功：key:%s#hash:%s",putRet.key,putRet.hash));
            return putRet.key;
        } catch (QiniuException e) {
            logger.error("七牛云移动文件异常",e);
            return null;
        }
    }

    /**
     * 设置文件的存活时间
     * @param bucketName
     * @param key
     * @param days 存活时间，单位：天
     * @return
     */
    public String deleteAfterDays(String bucketName, String key, int days) {
        BucketManager bucketManager = new BucketManager(Auth.create(accessKey, secretKey),cfg);
        try {
            Response response = bucketManager.deleteAfterDays(bucketName, key, days);
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            logger.info(String.format("七牛云设置文件存活时间成功：key:%s#hash:%s",putRet.key,putRet.hash));
            return putRet.key;
        } catch (QiniuException e) {
            logger.error("七牛云设置文件存活时间异常",e);
            return null;
        }
    }

    /**
     * 抓取指定地址的文件，以指定名称保存在指定空间
     * 要求指定url可访问，大文件不建议使用此接口抓取。可先下载再上传
     * @param url
     * @param bucketName
     * @param key
     * @return
     */
    public String fetchToBucket(String url, String bucketName, String key) {
        BucketManager bucketManager = new BucketManager(Auth.create(accessKey, secretKey),cfg);
        try {
            FetchRet fetchRet = bucketManager.fetch(url, bucketName, key);
            logger.info(String.format("七牛云抓取网络资源并上传成功：key:%s#hash:%s",fetchRet.key,fetchRet.hash));
            return fetchRet.key;
        } catch (QiniuException e) {
            logger.error("七牛云抓取网络资源并上传异常",e);
            return null;
        }
    }

    /**
     * 异步第三方资源抓取 从指定 URL 抓取资源，并将该资源存储到指定空间中。每次只抓取一个文件，抓取时可以指定保存空间名和最终资源名。
     * 主要对于大文件进行抓取
     * @param url
     * @param bucketName
     * @param key
     * @return
     */
    public String asynFetch(String url, String bucketName, String key) {
        BucketManager bucketManager = new BucketManager(Auth.create(accessKey, secretKey),cfg);
        try {
            Response response = bucketManager.asynFetch(url, bucketName, key);
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            logger.info(String.format("七牛云抓取网络资源并上传成功：key:%s#hash:%s",putRet.key,putRet.hash));
            return putRet.key;
        } catch (QiniuException e) {
            logger.error("七牛云抓取网络资源并上传异常",e);
            return null;
        }
    }

    /**
     * 批量混合指令（包括多种操作）批量文件管理请求
     * @param bucketName 空间名
     * @param statList
     * @param copyList
     * @param moveList
     * @param deleteList
     */
    public void batch(String bucketName,List<String> statList,List<String> copyList,List<String> moveList,List<String> deleteList){
        BucketManager bucketManager = new BucketManager(Auth.create(accessKey, secretKey),cfg);
        try {
            //单次批量请求的文件数量不得超过1000
            BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
            //添加混合指令
            batchOperations.addStatOps(bucketName, "qiniu.png", "qiniu.jpg");
            batchOperations.addCopyOp(bucketName, "qiniu.png", bucketName, "qiniu_copy1.png");
            batchOperations.addMoveOp(bucketName, "qiniu2.png", bucketName, "qiniu3.png");
            batchOperations.addDeleteOp(bucketName, "qiniu4.png");
            Response response = bucketManager.batch(batchOperations);
            BatchStatus[] batchStatusList = response.jsonToObject(BatchStatus[].class);
            for (BatchStatus status : batchStatusList) {
                if (status.code == 200) {
                    System.out.println("operation success");
                } else {
                    System.out.println(status.data.error);
                }
            }
        } catch (QiniuException ex) {
            System.err.println(ex.response.toString());
        }
    }

    /**
     * 更新镜像存储空间中文件内容
     * 对于设置了镜像存储的空间，从镜像源站抓取指定名称的资源并存储到该空间中
     * 如果该空间中已存在该名称的资源，则会将镜像源站的资源覆盖空间中相同名称的资源
     * @param bucketName 空间名
     * @param key
     */
    public void fetchToBucket(String bucketName, String key) {
        BucketManager bucketManager = new BucketManager(Auth.create(accessKey, secretKey),cfg);
        try {
            bucketManager.prefetch(bucketName, key);
            logger.info("七牛云更新镜像存储空间中文件内容成功");
        } catch (QiniuException e) {
            logger.error("七牛云更新镜像存储空间中文件内容异常",e);
        }
    }

    /**
     * 添加水印style的url到现有url上
     * @param putRet
     * @param isShowStyle
     * @return
     */
    public String getStyleUrl(DefaultPutRet putRet,boolean isShowStyle){
        String url;
        if(isShowStyle){
            url = hostname + putRet.key + "?" + style;
        }else {
            url = hostname + putRet.key;
        }
        return url;
    }

    public void sendSms(String templateId, String[] mobiles, Map<String, String> params){
        // 实例化一个SmsManager对象
        SmsManager smsManager = new SmsManager(Auth.create(accessKey, secretKey));

        try {
            Response resp = smsManager.sendMessage(templateId, mobiles, params);
//          Response resp = smsManager.describeSignature("passed", 0, 0);
//          Response resp = smsManager.createSignature("signature", "app",
//                  new String[] { "data:image/gif;base64,xxxxxxxxxx" });
//          Response resp = smsManager.describeTemplate("passed", 0, 0);
//          Response resp = smsManager.createTemplate("name", "template", "notification", "test", "signatureId");
//          Response resp = smsManager.modifyTemplate("templateId", "name", "template", "test", "signatureId");
//          Response resp = smsManager.modifySignature("SignatureId", "signature");
//          Response resp = smsManager.deleteSignature("signatureId");
//          Response resp = smsManager.deleteTemplate("templateId");
            System.out.println(resp.bodyString());

//          SignatureInfo sinfo = smsManager.describeSignatureItems("", 0, 0);
//          System.out.println(sinfo.getItems().get(0).getAuditStatus());
//          TemplateInfo tinfo = smsManager.describeTemplateItems("", 0, 0);
//          System.out.println(tinfo.getItems().get(0).getAuditStatus());


        } catch (QiniuException e) {
            System.out.println(e);
        }

    }

}
