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
                interceptor.checkPermission().length > 0 ||
                interceptor.checkResourceAccess() ||
                interceptor.checkExecutePermission()) {
                userId = checkLogin(request);
                // 将userId设置到request属性中，供controller使用
                request.setAttribute("userId", userId);
            }

            /**
             * 校验管理员
             */
            if (interceptor.checkAdmin() && userId != null) {
                checkAdmin(userId);
            }

            /**
             * 校验权限（简化版：admin拥有所有权限）
             */
            if (interceptor.checkPermission().length > 0 && userId != null) {
                checkPermission(userId, interceptor.checkPermission());
            }

            /**
             * 校验项目权限（基于项目成员角色）
             */
            if (!interceptor.checkProjectPermission().isEmpty() && userId != null) {
                String projectPermission = interceptor.checkProjectPermission();
                Integer projectId = getProjectIdFromRequest(point, interceptor.projectIdParam());

                // 如果 projectId 为空，尝试从资源 ID 反查
                if (projectId == null && !interceptor.resourceTypeForProjectCheck().isEmpty()) {
                    Integer resourceId = getResourceIdFromRequest(point, interceptor.resourceIdParamForProjectCheck());
                    if (resourceId != null) {
                        projectId = permissionService.getProjectIdByResource(
                            interceptor.resourceTypeForProjectCheck(), resourceId);
                    }
                }

                // 如果还是没有 projectId，尝试使用额外的资源参数
                if (projectId == null && !interceptor.extraResourceType().isEmpty() &&
                    !interceptor.extraResourceIdParam().isEmpty()) {
                    Integer extraResourceId = getResourceIdFromRequest(point, interceptor.extraResourceIdParam());
                    if (extraResourceId != null) {
                        projectId = permissionService.getProjectIdByResource(
                            interceptor.extraResourceType(), extraResourceId);
                    }
                }

                if (projectId != null) {
                    checkProjectPermission(userId, projectId, projectPermission);
                }
            }

            /**
             * 校验测试用例执行权限
             */
            if (interceptor.checkExecutePermission() && userId != null) {
                Integer resourceId = getResourceIdFromRequest(point, interceptor.resourceIdParam());
                if (resourceId != null) {
                    String resourceType = interceptor.executeResourceType();
                    if (resourceType == null || resourceType.isEmpty()) {
                        resourceType = "testcase"; // 默认值
                    }
                    checkExecutePermission(userId, resourceId, resourceType);
                }
            }

            /**
             * 校验资源访问权限
             */
//            if (interceptor.checkResourceAccess() && userId != null) {
//                checkResourceAccess(userId, interceptor.resourceType(),
//                    getResourceIdFromRequest(point, interceptor.resourceIdParam()));
//            }

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
     * 校验权限（简化版：admin拥有所有权限）
     */
    private void checkPermission(Integer userId, String[] permissions) {
        if (!permissionService.hasPermission(userId, permissions)) {
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
     * 校验项目权限（基于项目成员角色）
     */
    private void checkProjectPermission(Integer userId, Integer projectId, String permission) {
        if (!permissionService.hasProjectPermission(userId, projectId, permission)) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
    }

    /**
     * 校验测试用例执行权限
     * 权限规则：admin/owner/manager/tester 可以执行，viewer/developer 不能执行
     */
    private void checkExecutePermission(Integer userId, Integer resourceId, String resourceType) {
        if (!permissionService.canExecute(userId, resourceId, resourceType)) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
    }

    /**
     * 从请求参数中获取项目ID
     * 支持的参数字段：projectId, project_id, projectId
     */
    private Integer getProjectIdFromRequest(JoinPoint point, String projectIdParam) {
        if (projectIdParam == null || projectIdParam.isEmpty()) {
            // 尝试从常见参数名中获取
            String[] commonParams = {"projectId", "project_id", "projectId"};
            for (String param : commonParams) {
                Integer id = getParamValue(point, param);
                if (id != null) {
                    return id;
                }
            }
            return null;
        }

        return getParamValue(point, projectIdParam);
    }

    /**
     * 从请求参数中获取指定名称的参数值
     */
    private Integer getParamValue(JoinPoint point, String paramName) {
        try {
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            Parameter[] parameters = method.getParameters();
            Object[] args = point.getArgs();

            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                if (paramName.equals(parameter.getName())) {
                    Object arg = args[i];
                    if (arg instanceof Integer) {
                        return (Integer) arg;
                    } else if (arg instanceof String && arg != null) {
                        try {
                            return Integer.parseInt((String) arg);
                        } catch (NumberFormatException e) {
                            // 忽略
                        }
                    }
                }
            }

            // 尝试从 request 中获取
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String value = request.getParameter(paramName);
                if (value != null && !value.isEmpty()) {
                    return Integer.parseInt(value);
                }
            }
        } catch (Exception e) {
            logger.error("获取项目ID失败", e);
        }

        return null;
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
