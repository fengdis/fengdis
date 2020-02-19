package com.fengdis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @version 1.0
 * @Descrittion: 邮件服务
 * @author: fengdi
 * @since: 2018/8/8 0008 21:21
 */
@Component
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.fromMail.addr:''}")
    private String from;

    @Value("${mail.fromMail.name:''}")
    private String fromName;

    @Value("${domain.name:''}")
    private String domain;

    public String getMailTemplate(String name,String text){
        StringBuffer content = new StringBuffer("<div style=\"width:100%;overflow:hidden;border-bottom:1px solid #bdbdbe;\">");
        content.append("<div style=\"height:52px;overflow:hidden;border:1px solid #464c51;background:#353b3f url("+ domain +"/blog/images/hdbg.png);\">");
        content.append("<a href="+ domain +" target=\"_blank\" style=\"display:block;width:40px;height:35px;margin:10px 0 0 20px;overflow:hidden;text-indent:-2000px;background:url("+ domain +"/blog/images/icon.png) no-repeat;background-size:100% 100%;-moz-background-size:100% 100%;\">Blog</a>");
        content.append("</div>");
        content.append("<div style=\"padding:20px 20px;\">您好，"+ name +"<br/><br/>本站是一款\"专注兴趣、分享创作\"的轻博客产品，旨在为\"热爱记录生活、热衷分享技术\"的你，打造一个全新的展示平台！<br/><br/>" );
        content.append(text);
        content.append("<br/><br/><br/>专注兴趣，分享创作<br/>" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+ "</div></div>" );
        return content.toString();
    }

    /**
     * 发送普通文本邮件
     * @param to
     * @param subject
     * @param content
     */
    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        try {
            mailSender.send(message);
            logger.info(String.format("简单邮件发送成功：to %s",to));
        } catch (Exception e) {
            logger.error("简单邮发送发生异常", e);
        }

    }

    /**
     * 发送html格式邮件
     * @param to
     * @param subject
     * @param content
     */
    public void sendHtmlMail(String to, String subject, String content) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            logger.info(String.format("HTML邮件发送成功：to %s",to));
        } catch (MessagingException e) {
            logger.error("html邮件发送异常", e);
        }
    }

    /**
     * 发送带附件的邮件
     * @param to
     * @param subject
     * @param content
     * @param filePath
     */
    public void sendAttachmentsMail(String to, String subject, String content, String filePath){
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
            helper.addAttachment(fileName, file);

            mailSender.send(message);
            logger.info(String.format("附件邮件发送成功：to %s",to));
        } catch (MessagingException e) {
            logger.error("附件邮件发送异常", e);
        }
    }

    /**
     * 发送带静态资源的邮件
     * @param to
     * @param subject
     * @param content
     * @param rscPath
     * @param rscId
     */
    public void sendInlineResourceMail(String to, String subject, String content, String rscPath, String rscId){
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource res = new FileSystemResource(new File(rscPath));
            helper.addInline(rscId, res);

            mailSender.send(message);
            logger.info(String.format("嵌入静态资源的邮件发送成功：to %s",to));
        } catch (MessagingException e) {
            logger.error("嵌入静态资源的邮件发送异常", e);
        }
    }


    /**
     * 发送邮件
     * @param to        收件人
     * @param cc        抄送
     * @param bcc       密送
     * @param subject   主题
     * @param content   内容
     * @param fileList  附件列表
     * @param staticRes 静态资源Map
     */
    public void sendMail(String nikeName, String[] to, String[] cc, String[] bcc, String subject, String content, String[] fileList, Map<String, String> staticRes) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            try {
                if(nikeName != null){
                    nikeName = MimeUtility.encodeText(nikeName);
                }else {
                    nikeName = MimeUtility.encodeText(fromName);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // 设置发件人
            //helper.setFrom(new InternetAddress(from));
            helper.setFrom(new InternetAddress(from, nikeName));

            // 设置收件人
            if (to != null && to.length > 0) {
                helper.setTo(to);
            }
            // 设置抄送人
            if (cc != null && cc.length > 0) {
                helper.setCc(cc);
            }
            // 设置密送人
            if (bcc != null && bcc.length > 0) {
                helper.setBcc(bcc);
            }

            // 设置主题
            helper.setSubject(subject);
            // 设置正文，默认开启html
            helper.setText(content, true);

            // 设置附件
            if (fileList != null && fileList.length > 0) {
                for (int i = 0; i < fileList.length; i++) {
                    FileSystemResource file = new FileSystemResource(new File(fileList[i]));
                    String fileName = fileList[i].substring(fileList[i].lastIndexOf(File.separator));
                    helper.addAttachment(fileName, file);
                }
            }

            // 设置静态资源
            if (staticRes != null && staticRes.size() > 0) {
                for (String resId : staticRes.keySet()) {
                    FileSystemResource fileSystemResource = new FileSystemResource(new File(staticRes.get(resId)));
                    helper.addInline(resId, fileSystemResource);
                }
            }

            mailSender.send(message);

            logger.info("邮件发送成功");
        } catch (MessagingException e) {
            logger.error("邮件发送异常", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("邮件发送异常", e);
        }
    }

    public String getMailList(String[] mailArray) {
        StringBuffer toList = new StringBuffer();
        int length = mailArray.length;
        if (mailArray != null && length < 2) {
            toList.append(mailArray[0]);
        } else {
            for (int i = 0; i < length; i++) {
                toList.append(mailArray[i]);
                if (i != (length - 1)) {
                    toList.append(",");
                }

            }
        }
        return toList.toString();
    }


}
