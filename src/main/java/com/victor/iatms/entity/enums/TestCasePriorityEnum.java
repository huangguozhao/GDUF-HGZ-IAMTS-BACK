package com.victor.iatms.entity.enums;

/**
 * 测试用例优先级枚举
 */
public enum TestCasePriorityEnum {

    P0("P0", "最高优先级"),
    P1("P1", "高优先级"),
    P2("P2", "中优先级"),
    P3("P3", "低优先级");

    private final String code;
    private final String desc;

    TestCasePriorityEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean isValidPriority(String priority) {
        for (TestCasePriorityEnum priorityEnum : values()) {
            if (priorityEnum.getCode().equalsIgnoreCase(priority)) {
                return true;
            }
        }
        return false;
    }
}
