package com.victor.iatms.entity.dto;

import lombok.Data;
import java.util.Map;

/**
 * 创建环境配置请求DTO
 */
@Data
public class CreateEnvironmentConfigDTO {
    
    private String envCode;
    private String envName;
    private String envType;
    private String description;
    private String baseUrl;
    private String domain;
    private String protocol;
    private Integer port;
    
    // JSON字段接收为Map或自定义对象
    private Map<String, Object> databaseConfig;
    private Map<String, Object> externalServices;
    private Map<String, Object> variables;
    private Map<String, Object> authConfig;
    private Map<String, Object> featureFlags;
    private Map<String, Object> performanceConfig;
    private Map<String, Object> monitoringConfig;
    
    private String status;
    private Boolean isDefault;
    private String maintenanceMessage;
    
    private Map<String, Object> deploymentInfo;
    private String deployedVersion;
}

