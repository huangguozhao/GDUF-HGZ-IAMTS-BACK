package com.victor.iatms.entity.enums;

/**
 * 测试结果严重程度枚举
 */
public enum ResultSeverityEnum {
    
    BLOCKER("blocker", "阻塞"),
    CRITICAL("critical", "严重"),
    NORMAL("normal", "一般"),
    MINOR("minor", "次要"),
    TRIVIAL("trivial", "轻微");

    private final String code;
    private final String description;

    ResultSeverityEnum(String code, String description) {
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
    public static ResultSeverityEnum fromCode(String code) {
        for (ResultSeverityEnum severity : values()) {
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

