package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 编辑项目响应DTO
 */
@Data
public class UpdateProjectResponseDTO {
    
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
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否已删除
     */
    private Boolean isDeleted;
}
