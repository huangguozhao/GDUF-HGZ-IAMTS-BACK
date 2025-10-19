package com.victor.iatms.entity.enums;

/**
 * 权限枚举
 */
public enum PermissionEnum {
    
    // 用户管理权限
    USER_VIEW("user:view", "查看用户"),
    USER_CREATE("user:create", "创建用户"),
    USER_UPDATE("user:update", "更新用户"),
    USER_DELETE("user:delete", "删除用户"),
    
    // 接口管理权限
    API_VIEW("api:view", "查看接口"),
    API_CREATE("api:create", "创建接口"),
    API_UPDATE("api:update", "更新接口"),
    API_DELETE("api:delete", "删除接口"),
    
    // 测试用例管理权限
    TEST_CASE_VIEW("testcase:view", "查看测试用例"),
    TEST_CASE_CREATE("testcase:create", "创建测试用例"),
    TEST_CASE_UPDATE("testcase:update", "更新测试用例"),
    TEST_CASE_DELETE("testcase:delete", "删除测试用例"),
    TEST_CASE_EXECUTE("testcase:execute", "执行测试用例"),
    
    // 模块管理权限
    MODULE_VIEW("module:view", "查看模块"),
    MODULE_CREATE("module:create", "创建模块"),
    MODULE_UPDATE("module:update", "更新模块"),
    MODULE_DELETE("module:delete", "删除模块"),
    
    // 系统管理权限
    SYSTEM_CONFIG("system:config", "系统配置"),
    SYSTEM_LOG("system:log", "系统日志"),
    SYSTEM_MONITOR("system:monitor", "系统监控");

    private final String code;
    private final String description;

    PermissionEnum(String code, String description) {
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
    public static PermissionEnum fromCode(String code) {
        for (PermissionEnum permission : values()) {
            if (permission.getCode().equals(code)) {
                return permission;
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
