package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 项目成员列表查询DTO
 */
@Data
public class ProjectMembersQueryDTO {
    
    /**
     * 项目ID
     */
    private Integer projectId;
    
    /**
     * 成员状态过滤
     */
    private String status;
    
    /**
     * 权限级别过滤
     */
    private String permissionLevel;
    
    /**
     * 项目角色过滤
     */
    private String projectRole;
    
    /**
     * 关键字搜索（成员姓名、邮箱）
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
    
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页条数
     */
    private Integer pageSize = 20;
    
    /**
     * 分页偏移量（内部使用）
     */
    private Integer offset;
}
