package com.victor.iatms.entity.enums;

/**
 * 报告排序字段枚举
 */
public enum ReportSortFieldEnum {
    
    /**
     * 创建时间
     */
    CREATED_AT("created_at", "创建时间"),
    
    /**
     * 开始时间
     */
    START_TIME("start_time", "开始时间"),
    
    /**
     * 成功率
     */
    SUCCESS_RATE("success_rate", "成功率"),
    
    /**
     * 耗时
     */
    DURATION("duration", "耗时");
    
    private final String code;
    private final String desc;
    
    ReportSortFieldEnum(String code, String desc) {
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
    public static ReportSortFieldEnum getByCode(String code) {
        for (ReportSortFieldEnum field : values()) {
            if (field.getCode().equals(code)) {
                return field;
            }
        }
        return null;
    }
    
    /**
     * 验证排序字段是否有效
     */
    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }
}
