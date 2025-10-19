package com.victor.iatms.aspect;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.enums.ResponseCodeEnum;
import com.victor.iatms.exception.BusinessException;
import com.victor.iatms.service.PermissionService;
import com.victor.iatms.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 全局操作切面
 */
@Component("operationAspect")
@Aspect
public class GlobalOperationAspect {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PermissionService permissionService;

    private static Logger logger = LoggerFactory.getLogger(GlobalOperationAspect.class);

    @Before("@annotation(com.victor.iatms.annotation.GlobalInterceptor)")
    public void interceptorDo(JoinPoint point) {
        // ========== 临时禁用拦截器校验（测试用） ==========
        // TODO: 正式环境请将下面的 ENABLE_INTERCEPTOR 改为 true
        boolean ENABLE_INTERCEPTOR = false; // 设置为 false 禁用所有拦截器校验
        if (!ENABLE_INTERCEPTOR) {
            return;
        }
        // =============================================
        
        try {
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);
            if (null == interceptor) {
                return;
            }

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                throw new BusinessException(ResponseCodeEnum.SERVER_ERROR);
            }
            HttpServletRequest request = attributes.getRequest();
            Integer userId = null;

            /**
             * 校验登录
             */
            if (interceptor.checkLogin() || interceptor.checkAdmin() || 
                interceptor.checkPermission().length > 0 || interceptor.checkRole().length > 0 ||
                interceptor.checkResourceAccess()) {
                userId = checkLogin(request);
            }

            /**
             * 校验管理员
             */
            if (interceptor.checkAdmin() && userId != null) {
                checkAdmin(userId);
            }

            /**
             * 校验权限
             */
            if (interceptor.checkPermission().length > 0 && userId != null) {
                checkPermission(userId, interceptor.checkPermission());
            }

            /**
             * 校验角色
             */
            if (interceptor.checkRole().length > 0 && userId != null) {
                checkRole(userId, interceptor.checkRole());
            }

            /**
             * 校验资源访问权限
             */
            if (interceptor.checkResourceAccess() && userId != null) {
                checkResourceAccess(userId, interceptor.resourceType(), 
                    getResourceIdFromRequest(point, interceptor.resourceIdParam()));
            }

        } catch (BusinessException e) {
            logger.error("全局拦截器异常", e);
            throw e;
        } catch (Exception e) {
            logger.error("全局拦截器异常", e);
            throw new BusinessException(ResponseCodeEnum.SERVER_ERROR);
        } catch (Throwable e) {
            logger.error("全局拦截器异常", e);
            throw new BusinessException(ResponseCodeEnum.SERVER_ERROR);
        }
    }

    /**
     * 校验登录
     */
    private Integer checkLogin(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }

        String token = authHeader.substring(7);
        
        try {
            if (!jwtUtils.validateToken(token)) {
                throw new BusinessException(ResponseCodeEnum.CODE_901);
            }

            Integer userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_901);
            }

            return userId;
        } catch (Exception e) {
            logger.error("Token验证失败", e);
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
    }

    /**
     * 校验管理员
     */
    private void checkAdmin(Integer userId) {
        if (!permissionService.isAdmin(userId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
    }

    /**
     * 校验权限
     */
    private void checkPermission(Integer userId, String[] permissions) {
        if (!permissionService.hasPermission(userId, permissions)) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
    }

    /**
     * 校验角色
     */
    private void checkRole(Integer userId, String[] roles) {
        if (!permissionService.hasRole(userId, roles)) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
    }

    /**
     * 校验资源访问权限
     */
    private void checkResourceAccess(Integer userId, String resourceType, Integer resourceId) {
        if (!permissionService.hasResourceAccess(userId, resourceType, resourceId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
    }

    /**
     * 从请求参数中获取资源ID
     */
    private Integer getResourceIdFromRequest(JoinPoint point, String resourceIdParam) {
        if (resourceIdParam == null || resourceIdParam.isEmpty()) {
            return null;
        }

        try {
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            Parameter[] parameters = method.getParameters();
            Object[] args = point.getArgs();

            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                if (resourceIdParam.equals(parameter.getName())) {
                    Object arg = args[i];
                    if (arg instanceof Integer) {
                        return (Integer) arg;
                    } else if (arg instanceof String) {
                        return Integer.parseInt((String) arg);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("获取资源ID失败", e);
        }

        return null;
    }
}
