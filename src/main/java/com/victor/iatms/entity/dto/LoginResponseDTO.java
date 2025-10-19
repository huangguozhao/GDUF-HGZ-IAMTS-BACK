package com.victor.iatms.entity.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户登录响应DTO
 */
@Data
public class LoginResponseDTO {
    
    /**
     * 用户信息
     */
    private UserInfo user;
    
    /**
     * 认证令牌 (JWT)
     */
    private String token;
    
    /**
     * 用户信息内部类
     */
    @Data
    public static class UserInfo {
        /**
         * 用户ID
         */
        private Integer userId;
        
        /**
         * 用户姓名
         */
        private String name;
        
        /**
         * 用户邮箱
         */
        private String email;
        
        /**
         * 用户头像URL
         */
        private String avatarUrl;
        
        /**
         * 用户手机号
         */
        private String phone;
        
        /**
         * 部门ID
         */
        private Integer departmentId;
        
        /**
         * 员工工号
         */
        private String employeeId;
        
        /**
         * 职位信息
         */
        private String position;
        
        /**
         * 备注/描述
         */
        private String description;
        
        /**
         * 账户状态
         */
        private String status;
        
        /**
         * 最后登录时间
         */
        private LocalDateTime lastLoginTime;
    }
}
