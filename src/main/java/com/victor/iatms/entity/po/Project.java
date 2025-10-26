package com.victor.iatms.entity.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目实体类
 */
@Data
public class Project {
    /**
     * 项目ID，自增主键
     */
    private Integer projectId;
    
    /**
     * 项目名称
     */
    private String name;
    
    /**
     * 项目描述
     */
    private String description;
    
    /**
     * 创建人ID，关联用户表
     */
    private Integer creatorId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 项目编码，唯一
     */
    private String projectCode;
    
    /**
     * 项目类型：WEB, MOBILE, API, DESKTOP, HYBRID
     */
    private String projectType;
    
    /**
     * 项目状态：ACTIVE, INACTIVE, ARCHIVED
     */
    private String status;
    
    /**
     * 项目头像URL
     */
    private String avatarUrl;
    
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
