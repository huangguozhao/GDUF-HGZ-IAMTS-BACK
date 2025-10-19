package com.victor.iatms.entity.enums;

/**
 * 测试用例严重程度枚举
 */
public enum SeverityEnum {
    
    CRITICAL("critical", "严重"),
    HIGH("high", "高"),
    MEDIUM("medium", "中"),
    LOW("low", "低");

    private final String code;
    private final String description;

    SeverityEnum(String code, String description) {
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
    public static SeverityEnum fromCode(String code) {
        for (SeverityEnum severity : values()) {
            if (severity.getCode().equals(code)) {
                return severity;
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
