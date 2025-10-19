package com.victor.iatms.entity.enums;

/**
 * 项目成员状态枚举
 */
public enum ProjectMemberStatusEnum {
    
    /**
     * 活跃
     */
    ACTIVE("active", "活跃"),
    
    /**
     * 非活跃
     */
    INACTIVE("inactive", "非活跃"),
    
    /**
     * 已移除
     */
    REMOVED("removed", "已移除");
    
    private final String code;
    private final String desc;
    
    ProjectMemberStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static ProjectMemberStatusEnum getByCode(String code) {
        for (ProjectMemberStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
