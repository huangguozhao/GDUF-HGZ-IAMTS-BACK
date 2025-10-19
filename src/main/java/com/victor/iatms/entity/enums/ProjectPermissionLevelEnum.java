package com.victor.iatms.entity.enums;

/**
 * 项目权限级别枚举
 */
public enum ProjectPermissionLevelEnum {
    
    /**
     * 只读
     */
    READ("read", "只读"),
    
    /**
     * 读写
     */
    WRITE("write", "读写"),
    
    /**
     * 管理员
     */
    ADMIN("admin", "管理员");
    
    private final String code;
    private final String desc;
    
    ProjectPermissionLevelEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static ProjectPermissionLevelEnum getByCode(String code) {
        for (ProjectPermissionLevelEnum level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return null;
    }
}
