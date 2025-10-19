package com.victor.iatms.entity.enums;

/**
 * 任务类型枚举
 */
public enum TaskTypeEnum {

    /**
     * 测试套件
     */
    TEST_SUITE("test_suite", "测试套件"),

    /**
     * 测试用例
     */
    TEST_CASE("test_case", "测试用例"),

    /**
     * 项目
     */
    PROJECT("project", "项目"),

    /**
     * 模块
     */
    MODULE("module", "模块"),

    /**
     * API监控
     */
    API_MONITOR("api_monitor", "API监控");

    private final String code;
    private final String desc;

    TaskTypeEnum(String code, String desc) {
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
    public static TaskTypeEnum getByCode(String code) {
        for (TaskTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
