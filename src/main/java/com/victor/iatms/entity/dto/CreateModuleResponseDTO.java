package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建模块响应DTO
 */
@Data
public class CreateModuleResponseDTO {
    
    /**
     * 模块ID
     */
    private Integer moduleId;
    
    /**
     * 模块编码
     */
    private String moduleCode;
    
    /**
     * 项目ID
     */
    private Integer projectId;
    
    /**
     * 父模块ID
     */
    private Integer parentModuleId;
    
    /**
     * 模块名称
     */
    private String name;
    
    /**
     * 模块描述
     */
    private String description;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 模块状态
     */
    private String status;
    
    /**
     * 模块负责人ID
     */
    private Integer ownerId;
    
    /**
     * 模块负责人姓名
     */
    private String ownerName;
    
    /**
     * 标签信息
     */
    private List<String> tags;
    
    /**
     * 创建人ID
     */
    private Integer createdBy;
    
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
