package com.victor.iatms.entity.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 模块实体类
 */
@Data
public class Module {
    
    /**
     * 模块ID，自增主键
     */
    private Integer moduleId;
    
    /**
     * 模块编码，项目内唯一
     */
    private String moduleCode;
    
    /**
     * 项目ID，关联Projects表的主键
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
     * 模块状态：active, inactive, archived
     */
    private String status;
    
    /**
     * 模块负责人ID
     */
    private Integer ownerId;
    
    /**
     * 标签信息，JSON格式
     */
    private String tags;
    
    /**
     * 创建人ID，关联用户表
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
