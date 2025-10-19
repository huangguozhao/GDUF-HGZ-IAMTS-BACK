package com.victor.iatms.entity.enums;

/**
 * 响应状态码枚举
 */
public enum ResponseCodeEnum {
    
    SUCCESS(1, "success"),
    BUSINESS_ERROR(0, "业务逻辑失败"),
    AUTH_ERROR(-1, "认证失败"),
    FORBIDDEN(-2, "权限不足"),
    PARAM_ERROR(-3, "参数校验失败"),
    NOT_FOUND(-4, "资源不存在"),
    CODE_601(601, "信息已经存在"),
    CODE_901(901, "认证失败，请重新登录"),
    CODE_404(404, "权限不足"),
    SERVER_ERROR(-5, "服务器内部异常");
    
    
    private final Integer code;
    private final String msg;
    
    ResponseCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getMsg() {
        return msg;
    }
}