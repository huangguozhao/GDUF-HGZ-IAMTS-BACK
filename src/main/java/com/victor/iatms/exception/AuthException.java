package com.victor.iatms.exception;

/**
 * 认证异常类 - 用于处理token过期、未登录等认证失败场景
 */
public class AuthException extends RuntimeException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}

