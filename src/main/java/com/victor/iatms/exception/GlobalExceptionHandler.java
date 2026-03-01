package com.victor.iatms.exception;

import com.victor.iatms.entity.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器 - 统一处理所有异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ========== 认证相关异常 ==========

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthException.class)
    public ResponseVO<Void> handleAuthException(AuthException e) {
        log.warn("认证异常: {}", e.getMessage());
        return ResponseVO.authError(e.getMessage());
    }

    // ========== 参数校验异常 ==========

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseVO<Void> handleValidationException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseVO.paramError(errorMsg);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseVO<Void> handleBindException(BindException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseVO.paramError(errorMsg);
    }

    // ========== 业务异常 ==========

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseVO<Void> handleBusinessException(BusinessException e) {
        return ResponseVO.businessError(e.getMessage());
    }

    /**
     * 处理运行时异常 - 包括各种业务错误
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseVO<Void> handleRuntimeException(RuntimeException e) {
        String message = e.getMessage();
        
        // 认证相关异常
        if (message != null && (message.contains("认证失败") || message.contains("请重新登录"))) {
            return ResponseVO.authError("认证失败，请重新登录");
        }
        
        // 资源不存在
        if (message != null && message.contains("不存在")) {
            return ResponseVO.notFound(message);
        }
        
        // 参数错误
        if (message != null && (message.contains("参数") || message.contains("格式"))) {
            return ResponseVO.paramError(message);
        }
        
        // 执行失败
        if (message != null && message.contains("失败")) {
            return ResponseVO.serverError(message);
        }
        
        // 未知运行时异常
        log.error("运行时异常: {}", message, e);
        return ResponseVO.serverError("系统异常，请稍后重试");
    }

    // ========== 其他异常 ==========

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseVO<Void> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return ResponseVO.serverError("系统异常，请稍后重试");
    }
}
