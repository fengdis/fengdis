package com.fengdis.service;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @version 1.0
 * @Descrittion: 阿里云短信服务
 * @author: fengdi
 * @since: 2018/8/8 0008 21:21
 */
@Component
public class AliyunSmsService {

    private static final Logger logger = LoggerFactory.getLogger(AliyunSmsService.class);

    @Value("${sms.accessKeyId:''}")
    private String accessKeyId;

    @Value("${sms.accessKeySecret:''}")
    private String accessKeySecret;

    /**
     * 产品名称：云通信短信API产品，开发者无需替换
     */
    private static final String product = "Dysmsapi";
    /**
     * 产品域名，开发者无需替换
     */
    private static final String domain = "dysmsapi.aliyuncs.com";

    /**
     * 产品版本号，暂时无需替换
     */
    private static final String version = "2017-05-25";

    private IAcsClient client;

    /**
     * 短信发送接口，支持在一次请求中向多个不同的手机号码发送同样内容的短信。
     * 如果您需要在一次请求中分别向多个不同的手机号码发送不同签名和模版内容的短信，请使用SendBatchSms接口。
     * 在一次请求中，最多可以向1000个手机号码发送同样内容的短信。
     * @param phoneNumbers 必填:接收短信的手机号码。
     *                      格式：
     *                      国内短信：11位手机号码，例如15951955195。
     *                      国际/港澳台消息：国际区号+号码，例如85200000000。
     *                      支持对多个手机号码发送短信，手机号码之间以英文逗号（,）分隔。上限为1000个手机号码。批量调用相对于单条调用及时性稍有延迟。
     *                      验证码类型短信，建议使用单独发送的方式。
     * @param signName 必填:短信签名-可在短信控制台中找到
     * @param templateCode 必填:短信模板-可在短信控制台中找到
     * @param templateParam 选填:短信模板变量对应的实际值，JSON格式。如果JSON中需要带换行符，请参照标准的JSON协议处理。
     * @return
     */
    public CommonResponse sendSms(String phoneNumbers,String signName,String templateCode, Map<String,String> templateParam) {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessKeySecret);
        //DefaultProfile.addEndpoint("default", "default", product, domain);
        client = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(domain);
        request.setVersion(version);
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "default");
        request.putQueryParameter("PhoneNumbers", phoneNumbers);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", JSON.toJSONString(templateParam));
        //选填-上行短信扩展码，无特殊需要此字段的用户请忽略此字段
        //request.putQueryParameter("SmsUpExtendCode", "0");
        //可选-外部流水扩展字段，outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        //request.putQueryParameter("OutId", "yourOutId");

        CommonResponse response = null;
        try {
            response = client.getCommonResponse(request);
            System.out.println(response.getData());
            logger.info("阿里云短信发送成功");
        } catch (ServerException e) {
            logger.error("阿里云短信发送异常",e);
        } catch (ClientException e) {
            logger.error("阿里云短信发送异常",e);
        }

        return response;
    }

    /**
     * 调用QuerySendDetails接口查看短信发送记录和发送状态。
     * 通过调用QuerySendDetails接口，可以根据短信发送日期查看发送记录和短信内容，也可以添加发送流水号，根据流水号查询指定日期指定请求的发送详情。
     * 如果指定日期短信发送量较大，可以分页查看。指定每页显示的短信详情数量和查看的页数，即可分页查看发送记录。
     * @param phoneNumber
     * @param sendDate
     * @param pageSize
     * @param currentPage
     * @param bizId
     * @return
     */
    public CommonResponse querySendDetails(String phoneNumber,String sendDate,String pageSize,String currentPage,String bizId){
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(domain);
        request.setVersion(version);
        request.setAction("QuerySendDetails");
        request.putQueryParameter("RegionId", "default");
        request.putQueryParameter("PhoneNumber", phoneNumber);
        request.putQueryParameter("SendDate", sendDate);
        request.putQueryParameter("PageSize", pageSize);
        request.putQueryParameter("CurrentPage", currentPage);
        request.putQueryParameter("BizId", bizId);

        CommonResponse response = null;
        try {
            response = client.getCommonResponse(request);
            System.out.println(response.getData());
            logger.info("阿里云短信sendDetails查询成功");
        } catch (ServerException e) {
            logger.error("阿里云短信sendDetails查询异常",e);
        } catch (ClientException e) {
            logger.error("阿里云短信sendDetails查询异常",e);
        }

        return response;
    }

    public CommonResponse querySmsSign(String signName){
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(domain);
        request.setVersion(version);
        request.setAction("QuerySmsSign");
        request.putQueryParameter("RegionId", "default");
        request.putQueryParameter("SignName", signName);

        CommonResponse response = null;
        try {
            response = client.getCommonResponse(request);
            System.out.println(response.getData());
            logger.info("阿里云短信smsSign查询成功");
        } catch (ServerException e) {
            logger.error("阿里云短信smsSign查询异常",e);
        } catch (ClientException e) {
            logger.error("阿里云短信smsSign查询异常",e);
        }

        return response;
    }

    public CommonResponse querySmsTemplate(String templateCode){
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(domain);
        request.setVersion(version);
        request.setAction("QuerySmsTemplate");
        request.putQueryParameter("RegionId", "default");
        request.putQueryParameter("TemplateCode", templateCode);

        CommonResponse response = null;
        try {
            response = client.getCommonResponse(request);
            System.out.println(response.getData());
            logger.info("阿里云短信smsTemplate查询成功");
        } catch (ServerException e) {
            logger.error("阿里云短信smsTemplate查询异常",e);
        } catch (ClientException e) {
            logger.error("阿里云短信smsTemplate查询异常",e);
        }

        return response;
    }

    /**
     * 调用短信AddSmsSign申请短信签名。
     *
     * 您可以通过短信服务API接口或短信服务控制台申请短信签名，签名需要符合个人用户签名规范或企业用户签名规范。
     *
     * 短信签名审核流程请参考签名审核流程。
     *
     * 说明 个人用户每天最多可以申请一个短信签名，适用场景默认为通用。企业用户每天最多可以申请100个签名。
     * @param signName
     * @param signSource
     * @param remark
     * @return
     */
    public CommonResponse addSmsSign(String signName,String signSource,String remark){
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(domain);
        request.setVersion(version);
        request.setAction("AddSmsSign");
        request.putQueryParameter("RegionId", "default");
        request.putQueryParameter("SignName", "1");
        request.putQueryParameter("SignSource", "1");
        request.putQueryParameter("Remark", "1");
        request.putQueryParameter("SignFileList.1.FileSuffix", "1");
        request.putQueryParameter("SignFileList.1.FileContents", "1");
        request.putQueryParameter("SignFileList.2.FileSuffix", "1");
        request.putQueryParameter("SignFileList.2.FileContents", "1");


        CommonResponse response = null;
        try {
            response = client.getCommonResponse(request);
            System.out.println(response.getData());
            logger.info("阿里云短信smsSign添加成功");
        } catch (ServerException e) {
            logger.error("阿里云短信smsSign添加异常",e);
        } catch (ClientException e) {
            logger.error("阿里云短信smsSign添加异常",e);
        }

        return response;
    }

    public CommonResponse addSmsTemplate(String templateType,String templateName,String templateContent,String remark){
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(domain);
        request.setVersion(version);
        request.setAction("AddSmsTemplate");
        request.putQueryParameter("RegionId", "default");
        request.putQueryParameter("TemplateType", templateType);
        request.putQueryParameter("TemplateName", templateName);
        request.putQueryParameter("TemplateContent", templateContent);
        request.putQueryParameter("Remark", remark);

        CommonResponse response = null;
        try {
            response = client.getCommonResponse(request);
            System.out.println(response.getData());
            logger.info("阿里云短信smsTemplate添加成功");
        } catch (ServerException e) {
            logger.error("阿里云短信smsTemplate添加异常",e);
        } catch (ClientException e) {
            logger.error("阿里云短信smsTemplate添加异常",e);
        }

        return response;
    }

    /**
     * 调用接口DeleteSmsSign删除短信签名。
     *
     * 说明
     * 不支持删除正在审核中的签名。
     * 短信签名删除后不可恢复，请谨慎操作。
     * @param signName
     * @return
     */
    public CommonResponse deleteSmsSign(String signName){
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(domain);
        request.setVersion(version);
        request.setAction("DeleteSmsSign");
        request.putQueryParameter("RegionId", "default");
        request.putQueryParameter("SignName", signName);


        CommonResponse response = null;
        try {
            response = client.getCommonResponse(request);
            System.out.println(response.getData());
            logger.info("阿里云短信smsSign删除成功");
        } catch (ServerException e) {
            logger.error("阿里云短信smsSign删除异常",e);
        } catch (ClientException e) {
            logger.error("阿里云短信smsSign删除异常",e);
        }

        return response;
    }

    public CommonResponse deleteSmsTemplate(String templateCode){
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(domain);
        request.setVersion(version);
        request.setAction("DeleteSmsTemplate");
        request.putQueryParameter("RegionId", "default");
        request.putQueryParameter("TemplateCode", templateCode);


        CommonResponse response = null;
        try {
            response = client.getCommonResponse(request);
            System.out.println(response.getData());
            logger.info("阿里云短信smsTemplate删除成功");
        } catch (ServerException e) {
            logger.error("阿里云短信smsTemplate删除异常",e);
        } catch (ClientException e) {
            logger.error("阿里云短信smsTemplate删除异常",e);
        }

        return response;
    }

    public CommonResponse modifySmsSign(String signName,String signSource,String remark){
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(domain);
        request.setVersion(version);
        request.setAction("ModifySmsSign");
        request.putQueryParameter("RegionId", "default");
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("SignSource", signSource);
        request.putQueryParameter("Remark", remark);
        request.putQueryParameter("SignFileList.1.FileSuffix", "1");
        request.putQueryParameter("SignFileList.1.FileContents", "1");
        request.putQueryParameter("SignFileList.2.FileSuffix", "2");
        request.putQueryParameter("SignFileList.2.FileContents", "2");


        CommonResponse response = null;
        try {
            response = client.getCommonResponse(request);
            System.out.println(response.getData());
            logger.info("阿里云短信smsSign修改成功");
        } catch (ServerException e) {
            logger.error("阿里云短信smsSign修改异常",e);
        } catch (ClientException e) {
            logger.error("阿里云短信smsSign修改异常",e);
        }

        return response;
    }

    /**
     *
     * @param templateType
     * @param templateName
     * @param templateCode
     * @param templateContent
     * @param remark
     * @return
     */
    public CommonResponse modifySmsTemplate(String templateType,String templateName,String templateCode,String templateContent,String remark){
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(domain);
        request.setVersion(version);
        request.setAction("ModifySmsTemplate");
        request.putQueryParameter("RegionId", "default");
        request.putQueryParameter("TemplateType", templateType);
        request.putQueryParameter("TemplateName", templateName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateContent", templateContent);
        request.putQueryParameter("Remark", remark);


        CommonResponse response = null;
        try {
            response = client.getCommonResponse(request);
            System.out.println(response.getData());
            logger.info("阿里云短信smsTemplate修改成功");
        } catch (ServerException e) {
            logger.error("阿里云短信smsTemplate修改异常",e);
        } catch (ClientException e) {
            logger.error("阿里云短信smsTemplate修改异常",e);
        }

        return response;
    }

    /**
     * SendBatchSms接口是短信批量发送接口，支持在一次请求中分别向多个不同的手机号码发送不同签名的短信。手机号码等参数均为JSON格式，字段个数相同，一一对应，短信服务根据字段在JSON中的顺序判断发往指定手机号码的签名。
     * 在一次请求中，最多可以向100个手机号码分别发送短信。
     * @param phoneNumberJson
     * @param signNameJson
     * @param templateCode
     * @param templateParamJson
     * @param smsUpExtendCodeJson
     * @return
     */
    public CommonResponse sendBatchSms(String phoneNumberJson,String signNameJson,String templateCode,String templateParamJson,String smsUpExtendCodeJson){
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(domain);
        request.setVersion(version);
        request.setAction("SendBatchSms");
        request.putQueryParameter("RegionId", "default");
        request.putQueryParameter("PhoneNumberJson", phoneNumberJson);
        request.putQueryParameter("SignNameJson", signNameJson);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParamJson", templateParamJson);
        request.putQueryParameter("SmsUpExtendCodeJson", smsUpExtendCodeJson);


        CommonResponse response = null;
        try {
            response = client.getCommonResponse(request);
            System.out.println(response.getData());
            logger.info("阿里云短信批量发送成功");
        } catch (ServerException e) {
            logger.error("阿里云短信批量发送异常",e);
        } catch (ClientException e) {
            logger.error("阿里云短信批量发送异常",e);
        }

        return response;
    }


    /**
     * 以下为aliyun-java-sdk-core.jar 3.3.1版本代码
     */
    /*public SendSmsResponse sendSms(String phoneNumber,String templateParams) throws ClientException {

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("default", "default", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(phoneNumber);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam(templateParams);

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");

        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
            logger.info("阿里云短信发送成功");
        }catch (Exception e){
            logger.error("阿里云短信发送异常",e);
        }

        return sendSmsResponse;
    }


    public QuerySendDetailsResponse querySendDetails(SendSmsResponse response,String phoneNumber) throws ClientException,InterruptedException {

        System.out.println("短信接口返回的数据----------------");
        System.out.println("Code=" + response.getCode());
        System.out.println("Message=" + response.getMessage());
        System.out.println("RequestId=" + response.getRequestId());
        System.out.println("BizId=" + response.getBizId());

        Thread.sleep(3000L);

        //查明细
        if(response.getCode() != null && response.getCode().equals("OK")) {
            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("default", "default", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象
            QuerySendDetailsRequest request = new QuerySendDetailsRequest();
            //必填-号码
            request.setPhoneNumber(phoneNumber);
            //可选-流水号
            request.setBizId(response.getBizId());
            //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
            SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
            request.setSendDate(ft.format(new Date()));
            //必填-页大小
            request.setPageSize(10L);
            //必填-当前页码从1开始计数
            request.setCurrentPage(1L);

            //hint 此处可能会抛出异常，注意catch
            QuerySendDetailsResponse querySendDetailsResponse = null;

            try {
                querySendDetailsResponse = acsClient.getAcsResponse(request);
                logger.info("查询阿里云短信明细成功");
            }catch (Exception e){
                logger.error("查询阿里云短信明细异常",e);
            }


            System.out.println("短信明细查询接口返回数据----------------");
            System.out.println("Code=" + querySendDetailsResponse.getCode());
            System.out.println("Message=" + querySendDetailsResponse.getMessage());
            int i = 0;
            for(QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse.getSmsSendDetailDTOs())
            {
                System.out.println("SmsSendDetailDTO["+i+"]:");
                System.out.println("Content=" + smsSendDetailDTO.getContent());
                System.out.println("ErrCode=" + smsSendDetailDTO.getErrCode());
                System.out.println("OutId=" + smsSendDetailDTO.getOutId());
                System.out.println("PhoneNum=" + smsSendDetailDTO.getPhoneNum());
                System.out.println("ReceiveDate=" + smsSendDetailDTO.getReceiveDate());
                System.out.println("SendDate=" + smsSendDetailDTO.getSendDate());
                System.out.println("SendStatus=" + smsSendDetailDTO.getSendStatus());
                System.out.println("Template=" + smsSendDetailDTO.getTemplateCode());
            }
            System.out.println("TotalCount=" + querySendDetailsResponse.getTotalCount());
            System.out.println("RequestId=" + querySendDetailsResponse.getRequestId());

            return querySendDetailsResponse;
        }

        return null;

    }*/

}
