package com.victor.iatms.entity.enums;

/**
 * 模块状态枚举
 */
public enum ModuleStatusEnum {
    
    /**
     * 活跃
     */
    ACTIVE("active", "活跃"),
    
    /**
     * 非活跃
     */
    INACTIVE("inactive", "非活跃"),
    
    /**
     * 已归档
     */
    ARCHIVED("archived", "已归档");
    
    private final String code;
    private final String desc;
    
    ModuleStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static ModuleStatusEnum getByCode(String code) {
        for (ModuleStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}