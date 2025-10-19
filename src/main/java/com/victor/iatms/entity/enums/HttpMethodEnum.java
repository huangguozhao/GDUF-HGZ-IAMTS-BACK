package com.victor.iatms.entity.enums;

/**
 * HTTP请求方法枚举
 */
public enum HttpMethodEnum {
    
    GET("GET", "GET请求"),
    POST("POST", "POST请求"),
    PUT("PUT", "PUT请求"),
    DELETE("DELETE", "DELETE请求"),
    PATCH("PATCH", "PATCH请求"),
    HEAD("HEAD", "HEAD请求"),
    OPTIONS("OPTIONS", "OPTIONS请求");

    private final String code;
    private final String description;

    HttpMethodEnum(String code, String description) {
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
    public static HttpMethodEnum fromCode(String code) {
        for (HttpMethodEnum method : values()) {
            if (method.getCode().equals(code)) {
                return method;
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
