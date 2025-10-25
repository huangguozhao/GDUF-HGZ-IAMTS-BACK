package com.victor.iatms.entity.enums;

/**
 * 环境类型枚举
 */
public enum EnvironmentTypeEnum {
    
    DEVELOPMENT("development", "开发环境"),
    TESTING("testing", "测试环境"),
    STAGING("staging", "预发布环境"),
    PRODUCTION("production", "生产环境"),
    PERFORMANCE("performance", "性能测试环境"),
    DISASTER_RECOVERY("disaster_recovery", "容灾环境");
    
    private final String code;
    private final String description;
    
    EnvironmentTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static boolean isValid(String code) {
        for (EnvironmentTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}

