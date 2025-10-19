package com.victor.iatms.entity.enums;

/**
 * 测试用例严重程度枚举
 */
public enum TestCaseSeverityEnum {

    CRITICAL("critical", "严重"),
    HIGH("high", "高"),
    MEDIUM("medium", "中"),
    LOW("low", "低");

    private final String code;
    private final String desc;

    TestCaseSeverityEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean isValidSeverity(String severity) {
        for (TestCaseSeverityEnum severityEnum : values()) {
            if (severityEnum.getCode().equalsIgnoreCase(severity)) {
                return true;
            }
        }
        return false;
    }
}
