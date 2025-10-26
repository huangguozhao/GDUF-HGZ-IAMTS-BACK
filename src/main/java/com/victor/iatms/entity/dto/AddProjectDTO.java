package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 添加项目请求DTO
 */
@Data
public class AddProjectDTO {
    
    /**
     * 项目名称
     */
    private String name;
    
    /**
     * 项目描述
     */
    private String description;
    
    /**
     * 项目编码（可选，不提供则自动生成）
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
}
