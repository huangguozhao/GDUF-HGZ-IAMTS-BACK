package com.victor.iatms.entity.enums;

/**
 * 项目成员排序字段枚举
 */
public enum ProjectMemberSortFieldEnum {
    
    /**
     * 加入时间
     */
    JOIN_TIME("join_time", "加入时间"),
    
    /**
     * 姓名
     */
    NAME("name", "姓名"),
    
    /**
     * 权限级别
     */
    PERMISSION_LEVEL("permission_level", "权限级别"),
    
    /**
     * 项目角色
     */
    PROJECT_ROLE("project_role", "项目角色");
    
    private final String code;
    private final String desc;
    
    ProjectMemberSortFieldEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static ProjectMemberSortFieldEnum getByCode(String code) {
        for (ProjectMemberSortFieldEnum field : values()) {
            if (field.getCode().equals(code)) {
                return field;
            }
        }
        return null;
    }
}
