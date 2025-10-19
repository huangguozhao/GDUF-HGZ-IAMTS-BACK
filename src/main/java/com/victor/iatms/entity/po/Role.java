package com.victor.iatms.entity.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体类
 */
@Data
public class Role {

    /**
     * 角色ID，自增主键
     */
    private Integer roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 是否为超级管理员角色
     */
    private Boolean isSuperAdmin;

    /**
     * 创建时间
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
