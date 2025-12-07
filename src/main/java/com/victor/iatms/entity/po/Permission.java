package com.victor.iatms.entity.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 权限信息表
 * @author: victor
 * @date: 2025-12-07
 */
@Data
public class Permission implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 权限ID，自增主键
     */
    private Integer permissionId;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 是否删除
     */
    private Boolean isDeleted;

    /**
     * 删除时间
     */
    private Date deletedAt;

    /**
     * 删除人ID
     */
    private Integer deletedBy;
}


