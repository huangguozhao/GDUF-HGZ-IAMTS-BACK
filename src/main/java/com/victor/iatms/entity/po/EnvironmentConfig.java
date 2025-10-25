package com.victor.iatms.entity.po;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 环境配置实体类
 */
@Data
public class EnvironmentConfig {
    
    /**
     * 环境ID，自增主键
     */
    private Integer envId;
    
    /**
     * 环境编码，唯一标识
     */
    private String envCode;
    
    /**
     * 环境名称
     */
    private String envName;
    
    /**
     * 环境类型：development-开发, testing-测试, staging-预发布, production-生产, performance-性能, disaster_recovery-容灾
     */
    private String envType;
    
    /**
     * 环境描述
     */
    private String description;
    
    /**
     * 基础URL
     */
    private String baseUrl;
    
    /**
     * 域名
     */
    private String domain;
    
    /**
     * 协议：http, https
     */
    private String protocol;
    
    /**
     * 端口号
     */
    private Integer port;
    
    /**
     * 数据库配置（JSON格式）
     */
    private String databaseConfig;
    
    /**
     * 外部服务配置（JSON格式）
     */
    private String externalServices;
    
    /**
     * 环境变量（JSON格式）
     */
    private String variables;
    
    /**
     * 认证配置（JSON格式）
     */
    private String authConfig;
    
    /**
     * 功能开关（JSON格式）
     */
    private String featureFlags;
    
    /**
     * 性能配置（JSON格式）
     */
    private String performanceConfig;
    
    /**
     * 监控配置（JSON格式）
     */
    private String monitoringConfig;
    
    /**
     * 环境状态：active-激活, inactive-非激活, maintenance-维护中
     */
    private String status;
    
    /**
     * 是否为默认环境
     */
    private Boolean isDefault;
    
    /**
     * 维护信息
     */
    private String maintenanceMessage;
    
    /**
     * 部署信息（JSON格式）
     */
    private String deploymentInfo;
    
    /**
     * 最后部署时间
     */
    private LocalDateTime lastDeployedAt;
    
    /**
     * 部署版本
     */
    private String deployedVersion;
    
    /**
     * 创建人ID
     */
    private Integer createdBy;
    
    /**
     * 更新人ID
     */
    private Integer updatedBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

