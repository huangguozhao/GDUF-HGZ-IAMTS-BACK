package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 项目列表查询DTO
 */
@Data
public class ProjectListQueryDTO {
    
    /**
     * 项目名称（模糊查询）
     */
    private String name;
    
    /**
     * 创建人ID
     */
    private Integer creatorId;
    
    /**
     * 是否包含已删除的项目
     */
    private Boolean includeDeleted = false;
    
    /**
     * 排序字段：name, created_at, updated_at
     */
    private String sortBy = "created_at";
    
    /**
     * 排序顺序：asc, desc
     */
    private String sortOrder = "desc";
    
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页条数
     */
    private Integer pageSize = 10;
    
    /**
     * 偏移量（用于分页）
     */
    private Integer offset;
}
