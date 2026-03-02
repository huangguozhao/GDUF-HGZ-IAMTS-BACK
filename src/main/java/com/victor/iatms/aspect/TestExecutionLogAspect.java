package com.victor.iatms.aspect;

import com.victor.iatms.entity.dto.ExecuteTestCaseDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 测试执行日志切面
 * 统一记录测试执行过程中的详细日志
 */
@Slf4j
@Aspect
@Component
public class TestExecutionLogAspect {

    /**
     * 定义切点：拦截TestExecutionService中所有以execute开头的方法
     */
    @Pointcut("execution(* com.victor.iatms.service.TestExecutionService.execute*(..))")
    public void executionPointcut() {}

    /**
     * 环绕通知：记录方法执行前后的详细信息
     */
    @Around("executionPointcut()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();
        
        // 1. 记录方法调用开始
        logMethodStart(methodName, className, args);
        
        long startTime = System.currentTimeMillis();
        Object result = null;
        boolean success = false;
        String errorMessage = null;
        
        try {
            result = joinPoint.proceed();
            success = true;
            log.info("✅ 测试执行成功 - 方法: {}.{}, 耗时: {}ms", 
                    className, methodName, System.currentTimeMillis() - startTime);
            return result;
        } catch (Exception e) {
            success = false;
            errorMessage = e.getMessage();
            log.error("❌ 测试执行失败 - 方法: {}.{}, 耗时: {}ms, 错误: {}", 
                     className, methodName, System.currentTimeMillis() - startTime, e.getMessage(), e);
            throw e;
        } finally {
            // 2. 记录方法执行结果摘要
            logMethodSummary(methodName, success, errorMessage, System.currentTimeMillis() - startTime);
        }
    }

    /**
     * 记录方法开始执行的详细信息
     */
    private void logMethodStart(String methodName, String className, Object[] args) {
        log.info("═══════════════════════════════════════════════════════════");
        log.info("▶ 开始执行测试 - 类: {}, 方法: {}", className, methodName);
        
        if (args == null || args.length == 0) {
            log.debug("  参数: (无参数)");
            return;
        }
        
        // 遍历参数并记录（过滤敏感信息）
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                log.debug("  参数[{}]: null", i);
            } else if (arg instanceof ExecuteTestCaseDTO) {
                logExecuteDTO((ExecuteTestCaseDTO) arg);
            } else if (arg instanceof Integer || arg instanceof String || arg instanceof Long) {
                log.debug("  参数[{}]: {}", i, arg);
            } else if (arg instanceof Map) {
                log.debug("  参数[{}]: Map<{}, {}>", i, 
                    ((Map<?, ?>) arg).keySet().size(), 
                    ((Map<?, ?>) arg).values().iterator().getClass().getSimpleName());
            } else {
                log.debug("  参数[{}]: {}", i, arg.getClass().getSimpleName());
            }
        }
    }

    /**
     * 记录测试执行DTO的详细信息
     */
    private void logExecuteDTO(ExecuteTestCaseDTO dto) {
        if (dto == null) {
            log.debug("  执行配置: null");
            return;
        }
        log.info("  📋 执行配置信息:");
        log.info("     - 执行类型: {}", dto.getExecutionType());
        log.info("     - 环境: {}", dto.getEnvironment());
        log.info("     - 基础URL: {}", dto.getBaseUrl());
        log.info("     - 超时时间: {}s", dto.getTimeout());
        log.info("     - 是否异步: {}", dto.getAsync());
        
        if (dto.getVariables() != null && !dto.getVariables().isEmpty()) {
            log.debug("     - 执行变量: {}", dto.getVariables());
        }
        if (dto.getAuthOverride() != null && !dto.getAuthOverride().isEmpty()) {
            log.debug("     - 认证覆盖: {}", dto.getAuthOverride());
        }
    }

    /**
     * 记录方法执行摘要
     */
    private void logMethodSummary(String methodName, boolean success, String errorMessage, long duration) {
        if (success) {
            log.info("✅ 执行完成 - 方法: {}, 耗时: {}ms", methodName, duration);
        } else {
            log.error("❌ 执行失败 - 方法: {}, 耗时: {}ms, 错误: {}", 
                      methodName, duration, errorMessage);
        }
        log.info("═══════════════════════════════════════════════════════════");
    }
}

