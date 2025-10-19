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
