package com.victor.iatms.entity.enums;

/**
 * 角色枚举
 */
public enum RoleEnum {
    
    SUPER_ADMIN("super_admin", "超级管理员"),
    ADMIN("admin", "管理员"),
    TEST_MANAGER("test_manager", "测试经理"),
    TEST_ENGINEER("test_engineer", "测试工程师"),
    DEVELOPER("developer", "开发人员"),
    VIEWER("viewer", "查看者");

    private final String code;
    private final String description;

    RoleEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取枚举
     */
    public static RoleEnum fromCode(String code) {
        for (RoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 验证代码是否有效
     */
    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }
}
