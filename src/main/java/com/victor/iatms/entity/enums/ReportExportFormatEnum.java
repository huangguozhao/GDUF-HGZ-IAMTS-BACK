package com.victor.iatms.entity.enums;

/**
 * 报告导出格式枚举
 */
public enum ReportExportFormatEnum {
    
    /**
     * Excel格式
     */
    EXCEL("excel", "Excel格式", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    
    /**
     * CSV格式
     */
    CSV("csv", "CSV格式", "text/csv"),
    
    /**
     * JSON格式
     */
    JSON("json", "JSON格式", "application/json");
    
    private final String code;
    private final String desc;
    private final String mimeType;
    
    ReportExportFormatEnum(String code, String desc, String mimeType) {
        this.code = code;
        this.desc = desc;
        this.mimeType = mimeType;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static ReportExportFormatEnum getByCode(String code) {
        for (ReportExportFormatEnum format : values()) {
            if (format.getCode().equals(code)) {
                return format;
            }
        }
        return null;
    }
    
    /**
     * 验证导出格式是否有效
     */
    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }
}
