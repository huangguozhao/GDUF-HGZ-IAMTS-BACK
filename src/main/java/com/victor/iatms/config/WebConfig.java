package com.victor.iatms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 注意：现在使用AOP进行权限校验，不再需要拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 现在使用@GlobalInterceptor注解和AOP进行权限校验
    // 不再需要配置拦截器
}
