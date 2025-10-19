package com.victor.iatms.entity.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户角色实体类
 */
@Data
public class UserRole {

    /**
     * 用户角色ID，自增主键
     */
    private Integer userRoleId;

    /**
     * 用户ID，关联用户表
     */
    private Integer userId;

    /**
     * 角色ID，关联角色表
     */
    private Integer roleId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
