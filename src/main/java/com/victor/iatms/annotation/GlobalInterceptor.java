package com.victor.iatms.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * 全局拦截器注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface GlobalInterceptor {

    /**
     * 校验登录
     *
     * @return
     */
    boolean checkLogin() default true;

    /**
     * 校验管理员
     *
     * @return
     */
    boolean checkAdmin() default false;

    /**
     * 校验权限
     *
     * @return
     */
    String[] checkPermission() default {};

    /**
     * 校验角色
     *
     * @return
     */
    String[] checkRole() default {};

    /**
     * 校验资源访问权限（如接口访问权限）
     *
     * @return
     */
    boolean checkResourceAccess() default false;

    /**
     * 校验项目权限（基于项目成员角色）
     * 例如：checkProjectPermission = "api:create" 表示需要检查用户在该项目是否有创建接口的权限
     *
     * @return
     */
    String checkProjectPermission() default "";

    /**
     * 项目ID参数名（用于从请求参数中获取项目ID）
     * 例如：projectId、project_id 等
     *
     * @return
     */
    String projectIdParam() default "";

    /**
     * 资源类型，用于从资源ID反查项目ID
     * 当 projectIdParam 为空时使用此项
     * 例如：resourceTypeForProjectCheck = "api" 表示从 apiId 参数查找项目ID
     *
     * @return
     */
    String resourceTypeForProjectCheck() default "";

    /**
     * 资源ID参数名（用于从请求参数中获取资源ID，然后反查项目ID）
     * 当 projectIdParam 为空时使用此项
     * 例如：resourceIdParamForProjectCheck = "apiId"
     *
     * @return
     */
    String resourceIdParamForProjectCheck() default "";

    /**
     * 额外的资源ID参数名（用于从请求参数中获取第二个资源ID）
     * 例如：moduleId 可以作为 api.moduleId 的查询依据
     *
     * @return
     */
    String extraResourceIdParam() default "";

    /**
     * 额外的资源类型
     *
     * @return
     */
    String extraResourceType() default "";

    /**
     * 资源类型（如api、module等）
     *
     * @return
     */
    String resourceType() default "";

    /**
     * 资源ID参数名（用于从请求参数中获取资源ID）
     *
     * @return
     */
    String resourceIdParam() default "";
}
