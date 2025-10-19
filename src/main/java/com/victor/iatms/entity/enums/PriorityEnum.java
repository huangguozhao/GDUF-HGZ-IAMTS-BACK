package com.victor.iatms.entity.enums;

/**
 * 测试用例优先级枚举
 */
public enum PriorityEnum {
    
    P0("P0", "最高优先级"),
    P1("P1", "高优先级"),
    P2("P2", "中优先级"),
    P3("P3", "低优先级");

    private final String code;
    private final String description;

    PriorityEnum(String code, String description) {
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
    public static PriorityEnum fromCode(String code) {
        for (PriorityEnum priority : values()) {
            if (priority.getCode().equals(code)) {
                return priority;
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
