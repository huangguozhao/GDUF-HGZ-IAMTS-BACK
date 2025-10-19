package com.victor.iatms.exception;

import com.victor.iatms.entity.vo.ResponseVO;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
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
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseVO<Void> handleBusinessException(BusinessException e) {
        return ResponseVO.businessError(e.getMessage());
    }
    
    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseVO<Void> handleRuntimeException(RuntimeException e) {
        return ResponseVO.serverError("系统异常，请稍后重试");
    }
    
    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseVO<Void> handleException(Exception e) {
        return ResponseVO.serverError("系统异常，请稍后重试");
    }
}
