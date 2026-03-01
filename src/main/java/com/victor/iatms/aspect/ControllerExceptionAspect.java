package com.victor.iatms.aspect;

import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Controller 统一异常处理切面
 * 用于统一处理 Controller 层抛出的异常，返回统一的响应格式
 */
@Slf4j
@Aspect
@Component
public class ControllerExceptionAspect {

    /**
     * 定义切点：所有 Controller 包下的 public 方法
     */
    @Pointcut("execution(public com.victor.iatms.entity.vo.ResponseVO com.victor.iatms.controller..*(..))")
    public void controllerPointcut() {
    }

    /**
     * 环绕通知：统一处理异常
     */
    @Around("controllerPointcut()")
    public Object handleException(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (AuthException e) {
            // 认证异常
            log.warn("Controller 认证异常: {}", e.getMessage());
            return ResponseVO.authError(e.getMessage());
        } catch (RuntimeException e) {
            // 运行时异常 - 根据消息内容返回不同的错误码
            String message = e.getMessage();
            log.warn("Controller 运行时异常: {}", message);
            
            if (message != null) {
                if (message.contains("不存在")) {
                    return ResponseVO.notFound(message);
                }
                if (message.contains("参数") || message.contains("格式")) {
                    return ResponseVO.paramError(message);
                }
                if (message.contains("认证失败") || message.contains("请重新登录")) {
                    return ResponseVO.authError("认证失败，请重新登录");
                }
                if (message.contains("失败")) {
                    return ResponseVO.serverError(message);
                }
            }
            
            return ResponseVO.serverError("系统异常，请稍后重试");
        } catch (Exception e) {
            log.error("Controller 未知异常: {}", e.getMessage(), e);
            return ResponseVO.serverError("系统异常，请稍后重试");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}

