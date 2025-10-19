package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 模块列表响应DTO
 */
@Data
public class ModuleListResponseDTO {
    
    /**
     * 项目ID
     */
    private Integer projectId;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 总模块数
     */
    private Integer totalModules;
    
    /**
     * 模块列表
     */
    private List<ModuleDTO> modules;
}
