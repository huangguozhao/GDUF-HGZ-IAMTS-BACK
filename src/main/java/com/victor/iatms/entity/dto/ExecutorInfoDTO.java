package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 执行人信息DTO
 */
@Data
public class ExecutorInfoDTO {
    
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
     * 用户手机号码
     */
    private String phone;
    
    /**
     * 部门ID
     */
    private Integer departmentId;
    
    /**
     * 部门名称
     */
    private String departmentName;
    
    /**
     * 员工工号
     */
    private String employeeId;
    
    /**
     * 职位
     */
    private String position;
    
    /**
     * 用户状态
     */
    private String status;
}

