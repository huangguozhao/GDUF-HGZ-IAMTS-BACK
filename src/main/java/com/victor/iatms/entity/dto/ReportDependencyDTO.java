package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 报告依赖信息DTO
 */
@Data
public class ReportDependencyDTO {
    
    /**
     * 依赖类型
     */
    private String type;
    
    /**
     * 依赖数量
     */
    private Integer count;
    
    /**
     * 依赖描述
     */
    private String description;
}
