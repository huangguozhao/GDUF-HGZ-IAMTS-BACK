package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 模块列表查询DTO
 */
@Data
public class ModuleListQueryDTO {
    
    /**
     * 项目ID
     */
    private Integer projectId;
    
    /**
     * 返回结构（tree/flat）
     */
    private String structure;
    
    /**
     * 模块状态过滤
     */
    private String status;
    
    /**
     * 是否包含已删除的模块
     */
    private Boolean includeDeleted;
    
    /**
     * 是否包含统计信息
     */
    private Boolean includeStatistics;
    
    /**
     * 关键字搜索（模块名称、描述）
     */
    private String searchKeyword;
    
    /**
     * 排序字段
     */
    private String sortBy;
    
    /**
     * 排序顺序
     */
    private String sortOrder;
}
