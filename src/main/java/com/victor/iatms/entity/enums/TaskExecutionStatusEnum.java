package com.victor.iatms.entity.enums;

/**
 * 任务执行状态枚举
 */
public enum TaskExecutionStatusEnum {
    
    QUEUED("queued", "排队中"),
    RUNNING("running", "执行中"),
    COMPLETED("completed", "已完成"),
    FAILED("failed", "执行失败"),
    CANCELLED("cancelled", "已取消"),
    TIMEOUT("timeout", "执行超时");
    
    private final String code;
    private final String desc;
    
    TaskExecutionStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static TaskExecutionStatusEnum getByCode(String code) {
        for (TaskExecutionStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
