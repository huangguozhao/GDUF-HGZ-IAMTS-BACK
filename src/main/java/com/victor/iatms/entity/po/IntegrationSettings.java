package com.victor.iatms.entity.po;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 集成设置实体类
 */
@Data
public class IntegrationSettings {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 集成类型: api-接口集成, webhook-Webhook, jenkins-Jenkins, git-Git
     */
    private String integrationType;
    
    /**
     * 集成服务名称
     */
    private String serviceName;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * API版本
     */
    private String apiVersion;
    
    /**
     * 请求超时时间(秒)
     */
    private Integer requestTimeout;
    
    /**
     * 最大重试次数
     */
    private Integer maxRetries;
    
    /**
     * 认证类型: none-无, basic-Basic, bearer-Bearer Token, oauth2-OAuth2
     */
    private String authType;
    
    /**
     * 认证令牌
     */
    private String authToken;
    
    /**
     * 用户名(Basic认证用)
     */
    private String username;
    
    /**
     * 密码(Basic认证用)
     */
    private String password;
    
    /**
     * 响应格式: json, xml, text
     */
    private String responseFormat;
    
    /**
     * Webhook URL
     */
    private String webhookUrl;
    
    /**
     * Webhook密钥
     */
    private String webhookSecret;
    
    /**
     * Jenkins地址
     */
    private String jenkinsUrl;
    
    /**
     * Jenkins用户名
     */
    private String jenkinsUsername;
    
    /**
     * Jenkins API Token
     */
    private String jenkinsToken;
    
    /**
     * Git仓库地址
     */
    private String gitUrl;
    
    /**
     * Git分支
     */
    private String gitBranch;
    
    /**
     * Git用户名
     */
    private String gitUsername;
    
    /**
     * Git Token
     */
    private String gitToken;
    
    /**
     * 其他配置(JSON格式)
     */
    private String extraConfig;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

