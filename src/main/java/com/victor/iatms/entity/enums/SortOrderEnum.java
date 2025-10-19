package com.victor.iatms.entity.enums;

/**
 * 排序顺序枚举
 */
public enum SortOrderEnum {
    
    /**
     * 升序
     */
    ASC("asc", "升序"),
    
    /**
     * 降序
     */
    DESC("desc", "降序");
    
    private final String code;
    private final String desc;
    
    SortOrderEnum(String code, String desc) {
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
    public static SortOrderEnum getByCode(String code) {
        for (SortOrderEnum order : values()) {
            if (order.getCode().equals(code)) {
                return order;
            }
        }
        return null;
    }
    
    /**
     * 验证排序顺序是否有效
     */
    public static boolean isValidSortOrder(String code) {
        return getByCode(code) != null;
    }
}
