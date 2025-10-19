package com.victor.iatms.entity.enums;

/**
 * 报告状态枚举
 */
public enum ReportStatusEnum {

    /**
     * 生成中
     */
    GENERATING("generating", "生成中"),

    /**
     * 已完成
     */
    COMPLETED("completed", "已完成"),

    /**
     * 失败
     */
    FAILED("failed", "失败");

    private final String code;
    private final String desc;

    ReportStatusEnum(String code, String desc) {
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
    public static ReportStatusEnum getByCode(String code) {
        for (ReportStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
