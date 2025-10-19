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
