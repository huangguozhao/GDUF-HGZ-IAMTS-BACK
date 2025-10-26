package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 编辑项目请求DTO
 */
@Data
public class UpdateProjectDTO {
    
    /**
     * 项目名称
     */
    private String name;
    
    /**
     * 项目描述
     */
    private String description;
    
    /**
     * 项目编码（一般不建议修改）
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
