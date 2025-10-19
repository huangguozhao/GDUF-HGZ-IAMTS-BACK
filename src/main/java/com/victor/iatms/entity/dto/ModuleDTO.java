package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 模块信息DTO
 */
@Data
public class ModuleDTO {
    
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
     * 负责人信息
     */
    private OwnerInfoDTO ownerInfo;
    
    /**
     * 标签
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
    
    /**
     * 是否已删除
     */
    private Boolean isDeleted;
    
    /**
     * 接口数量（统计信息）
     */
    private Integer apiCount;
    
    /**
     * 用例数量（统计信息）
     */
    private Integer caseCount;
    
    /**
     * 层级（平铺结构使用）
     */
    private Integer level;
    
    /**
     * 路径（平铺结构使用）
     */
    private String path;
    
    /**
     * 子模块列表（树形结构使用）
     */
    private List<ModuleDTO> children;
}
