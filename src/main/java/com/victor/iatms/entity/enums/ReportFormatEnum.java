package com.victor.iatms.entity.enums;

/**
 * 报告格式枚举
 */
public enum ReportFormatEnum {

    /**
     * HTML格式
     */
    HTML("html", "HTML格式"),

    /**
     * PDF格式
     */
    PDF("pdf", "PDF格式"),

    /**
     * Excel格式
     */
    EXCEL("excel", "Excel格式"),

    /**
     * JSON格式
     */
    JSON("json", "JSON格式"),

    /**
     * XML格式
     */
    XML("xml", "XML格式");

    private final String code;
    private final String desc;

    ReportFormatEnum(String code, String desc) {
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
    public static ReportFormatEnum getByCode(String code) {
        for (ReportFormatEnum format : values()) {
            if (format.getCode().equals(code)) {
                return format;
            }
        }
        return null;
    }
}
