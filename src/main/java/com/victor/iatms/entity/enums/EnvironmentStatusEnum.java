package com.victor.iatms.entity.enums;

/**
 * 环境状态枚举
 */
public enum EnvironmentStatusEnum {
    
    ACTIVE("active", "激活"),
    INACTIVE("inactive", "非激活"),
    MAINTENANCE("maintenance", "维护中");
    
    private final String code;
    private final String description;
    
    EnvironmentStatusEnum(String code, String description) {
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
        for (EnvironmentStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}

