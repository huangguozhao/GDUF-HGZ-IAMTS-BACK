package com.victor.iatms.entity.enums;

/**
 * 接口状态枚举
 */
public enum ApiStatusEnum {
    
    ACTIVE("active", "激活"),
    INACTIVE("inactive", "未激活"),
    DEPRECATED("deprecated", "已废弃");

    private final String code;
    private final String description;

    ApiStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取枚举
     */
    public static ApiStatusEnum fromCode(String code) {
        for (ApiStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 验证代码是否有效
     */
    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }
}
