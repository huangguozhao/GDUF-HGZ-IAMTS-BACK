package com.victor.iatms.entity.enums;

/**
 * 项目排序字段枚举
 */
public enum ProjectSortFieldEnum {
    
    /**
     * 按名称排序
     */
    NAME("name", "项目名称"),
    
    /**
     * 按创建时间排序
     */
    CREATED_AT("created_at", "创建时间"),
    
    /**
     * 按更新时间排序
     */
    UPDATED_AT("updated_at", "更新时间");
    
    private final String code;
    private final String desc;
    
    ProjectSortFieldEnum(String code, String desc) {
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
    public static ProjectSortFieldEnum getByCode(String code) {
        for (ProjectSortFieldEnum field : values()) {
            if (field.getCode().equals(code)) {
                return field;
            }
        }
        return null;
    }
    
    /**
     * 验证排序字段是否有效
     */
    public static boolean isValidSortField(String code) {
        return getByCode(code) != null;
    }
}
