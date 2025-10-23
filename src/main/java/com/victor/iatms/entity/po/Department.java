package com.victor.iatms.entity.po;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 部门实体类
 */
@Data
public class Department {
    
    /**
     * 部门ID
     */
    private Integer departmentId;
    
    /**
     * 部门名称
     */
    private String departmentName;
    
    /**
     * 部门描述
     */
    private String description;
    
    /**
     * 父部门ID
     */
    private Integer parentId;
    
    /**
     * 部门负责人ID
     */
    private Integer managerId;
    
    /**
     * 部门状态
     */
    private String status;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 部门层级（1为最高级）
     */
    private Integer level;
    
    /**
     * 部门路径（存储所有上级部门ID）
     */
    private String path;
    
    /**
     * 是否为叶子部门（无子部门）
     */
    private Boolean isLeaf;
    
    /**
     * 成立日期
     */
    private LocalDate establishedDate;
    
    /**
     * 创建人ID
     */
    private Integer createdBy;
    
    /**
     * 更新人ID
     */
    private Integer updatedBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否删除
     */
    private Boolean isDeleted;
    
    /**
     * 删除时间
     */
    private LocalDateTime deletedAt;
    
    /**
     * 删除人ID
     */
    private Integer deletedBy;
}