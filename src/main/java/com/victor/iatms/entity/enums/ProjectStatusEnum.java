package com.victor.iatms.entity.enums;

/**
 * 项目状态枚举
 */
public enum ProjectStatusEnum {
    
    /**
     * 活跃状态
     */
    ACTIVE("active", "活跃"),
    
    /**
     * 非活跃状态
     */
    INACTIVE("inactive", "非活跃"),
    
    /**
     * 已归档
     */
    ARCHIVED("archived", "已归档");
    
    private final String code;
    private final String desc;
    
    ProjectStatusEnum(String code, String desc) {
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
    public static ProjectStatusEnum getByCode(String code) {
        for (ProjectStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
