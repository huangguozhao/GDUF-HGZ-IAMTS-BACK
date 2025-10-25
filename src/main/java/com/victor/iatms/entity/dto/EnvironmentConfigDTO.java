package com.victor.iatms.entity.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 环境配置响应DTO
 */
@Data
public class EnvironmentConfigDTO {
    
    private Integer envId;
    private String envCode;
    private String envName;
    private String envType;
    private String description;
    private String baseUrl;
    private String domain;
    private String protocol;
    private Integer port;
    
    // JSON字段返回为Object，前端可以解析
    private Object databaseConfig;
    private Object externalServices;
    private Object variables;
    private Object authConfig;
    private Object featureFlags;
    private Object performanceConfig;
    private Object monitoringConfig;
    
    private String status;
    private Boolean isDefault;
    private String maintenanceMessage;
    
    private Object deploymentInfo;
    private String lastDeployedAt;
    private String deployedVersion;
    
    // 创建人信息
    private Integer createdBy;
    private String creatorName;
    private String creatorAvatar;
    
    // 更新人信息
    private Integer updatedBy;
    private String updaterName;
    private String updaterAvatar;
    
    private String createdAt;
    private String updatedAt;
}

