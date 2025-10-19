package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 创建模块请求DTO
 */
@Data
public class CreateModuleDTO {
    
    /**
     * 模块编码，项目内唯一
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
     * 标签信息
     */
    private List<String> tags;
}
