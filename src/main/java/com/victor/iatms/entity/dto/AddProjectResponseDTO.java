package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 添加项目响应DTO
 */
@Data
public class AddProjectResponseDTO {
    
    /**
     * 项目ID
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
     * 项目编码
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
     * 创建人ID
     */
    private Integer creatorId;
    
    /**
     * 创建人姓名
     */
    private String creatorName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
