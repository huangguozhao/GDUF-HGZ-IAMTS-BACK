package com.victor.iatms.entity.enums;

/**
 * 报告类型枚举
 */
public enum ReportTypeEnum {

    /**
     * 执行报告
     */
    EXECUTION("execution", "执行报告"),

    /**
     * 覆盖率报告
     */
    COVERAGE("coverage", "覆盖率报告"),

    /**
     * 趋势报告
     */
    TREND("trend", "趋势报告"),

    /**
     * 对比报告
     */
    COMPARISON("comparison", "对比报告"),

    /**
     * 自定义报告
     */
    CUSTOM("custom", "自定义报告");

    private final String code;
    private final String desc;

    ReportTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据代码获取枚举
     */
    public static ReportTypeEnum getByCode(String code) {
        for (ReportTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
