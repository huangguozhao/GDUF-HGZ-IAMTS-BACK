package com.victor.iatms.entity.enums;

/**
 * 项目角色枚举
 */
public enum ProjectRoleEnum {
    
    /**
     * 项目所有者
     */
    OWNER("owner", "项目所有者"),
    
    /**
     * 项目经理
     */
    MANAGER("manager", "项目经理"),
    
    /**
     * 开发人员
     */
    DEVELOPER("developer", "开发人员"),
    
    /**
     * 测试人员
     */
    TESTER("tester", "测试人员"),
    
    /**
     * 观察者
     */
    VIEWER("viewer", "观察者");
    
    private final String code;
    private final String desc;
    
    ProjectRoleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static ProjectRoleEnum getByCode(String code) {
        for (ProjectRoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }
}
