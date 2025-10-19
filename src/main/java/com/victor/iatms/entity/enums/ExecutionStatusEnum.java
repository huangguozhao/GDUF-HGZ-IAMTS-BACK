package com.victor.iatms.entity.enums;

/**
 * 执行状态枚举
 */
public enum ExecutionStatusEnum {

    /**
     * 通过
     */
    PASSED("passed", "通过"),

    /**
     * 失败
     */
    FAILED("failed", "失败"),

    /**
     * 中断
     */
    BROKEN("broken", "中断"),

    /**
     * 跳过
     */
    SKIPPED("skipped", "跳过"),

    /**
     * 未知
     */
    UNKNOWN("unknown", "未知"),

    /**
     * 待执行
     */
    PENDING("pending", "待执行"),

    /**
     * 执行中
     */
    RUNNING("running", "执行中"),

    /**
     * 已取消
     */
    CANCELLED("cancelled", "已取消");

    private final String code;
    private final String desc;

    ExecutionStatusEnum(String code, String desc) {
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
    public static ExecutionStatusEnum getByCode(String code) {
        for (ExecutionStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否为最终状态
     */
    public boolean isFinalStatus() {
        return this == PASSED || this == FAILED || this == BROKEN || 
               this == SKIPPED || this == UNKNOWN || this == CANCELLED;
    }

    /**
     * 判断是否为成功状态
     */
    public boolean isSuccessStatus() {
        return this == PASSED;
    }

    /**
     * 判断是否为失败状态
     */
    public boolean isFailureStatus() {
        return this == FAILED || this == BROKEN;
    }
}
