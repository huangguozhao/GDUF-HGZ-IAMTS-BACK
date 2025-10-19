package com.victor.iatms.interceptor;

import com.victor.iatms.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT认证拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 获取Authorization头
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":-1,\"msg\":\"认证失败，请重新登录\",\"data\":null}");
            return false;
        }

        // 提取token
        String token = authHeader.substring(7);
        
        try {
            // 验证token
            if (!jwtUtils.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":-1,\"msg\":\"认证失败，请重新登录\",\"data\":null}");
                return false;
            }

            // 从token中获取用户ID并设置到request中
            Integer userId = jwtUtils.getUserIdFromToken(token);
            request.setAttribute("userId", userId);
            
            return true;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":-1,\"msg\":\"认证失败，请重新登录\",\"data\":null}");
            return false;
        }
    }
}
