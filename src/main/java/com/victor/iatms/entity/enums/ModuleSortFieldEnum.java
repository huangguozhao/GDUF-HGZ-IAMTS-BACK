package com.victor.iatms.entity.enums;

/**
 * 模块排序字段枚举
 */
public enum ModuleSortFieldEnum {
    
    /**
     * 排序顺序
     */
    SORT_ORDER("sort_order", "排序顺序"),
    
    /**
     * 模块名称
     */
    NAME("name", "模块名称"),
    
    /**
     * 创建时间
     */
    CREATED_AT("created_at", "创建时间");
    
    private final String code;
    private final String desc;
    
    ModuleSortFieldEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static ModuleSortFieldEnum getByCode(String code) {
        for (ModuleSortFieldEnum field : values()) {
            if (field.getCode().equals(code)) {
                return field;
            }
        }
        return null;
    }
}
