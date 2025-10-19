package com.victor.iatms.entity.po;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户信息表实体类
 */
@Data
public class User {
    /**
     * 用户ID，自增主键
     */
    private Integer userId;
    
    /**
     * 用户姓名
     */
    private String name;
    
    /**
     * 用户邮箱，唯一
     */
    private String email;
    
    /**
     * 用户头像URL
     */
    private String avatarUrl;
    
    /**
     * 用户手机号码，允许为空
     */
    private String phone;
    
    /**
     * 用户密码，加密存储
     */
    private String password;
    
    /**
     * 部门ID，关联部门表
     */
    private Integer departmentId;
    
    /**
     * 员工工号
     */
    private String employeeId;
    
    /**
     * 创建人ID，关联用户表
     */
    private Integer creatorId;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 职位
     */
    private String position;
    
    /**
     * 备注/描述
     */
    private String description;
    
    /**
     * 用户状态：激活、非激活、待审核
     */
    private String status;
    
    /**
     * 账户创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否删除
     */
    private Boolean isDeleted;
    
    /**
     * 删除时间
     */
    private LocalDateTime deletedAt;
    
    /**
     * 删除人ID
     */
    private Integer deletedBy;
}
