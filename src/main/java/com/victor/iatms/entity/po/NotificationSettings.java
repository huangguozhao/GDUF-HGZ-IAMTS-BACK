package com.victor.iatms.entity.po;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 通知设置实体类
 */
@Data
public class NotificationSettings {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 是否启用邮件通知
     */
    private Boolean emailEnabled;
    
    /**
     * 是否启用短信通知
     */
    private Boolean smsEnabled;
    
    /**
     * 是否启用系统通知
     */
    private Boolean systemEnabled;
    
    /**
     * 邮件服务器主机
     */
    private String emailHost;
    
    /**
     * 邮件服务器端口
     */
    private Integer emailPort;
    
    /**
     * 邮件用户名
     */
    private String emailUsername;
    
    /**
     * 邮件密码
     */
    private String emailPassword;
    
    /**
     * 是否启用SSL
     */
    private Boolean emailSsl;
    
    /**
     * 邮件发件人
     */
    private String emailFrom;
    
    /**
     * 短信服务商: aliyun-阿里云, tencent-腾讯云, huawei-华为云
     */
    private String smsProvider;
    
    /**
     * 短信AccessKey
     */
    private String smsAccessKey;
    
    /**
     * 短信SecretKey
     */
    private String smsSecretKey;
    
    /**
     * 短信签名
     */
    private String smsSignName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

