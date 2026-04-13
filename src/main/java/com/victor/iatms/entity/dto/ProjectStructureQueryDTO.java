package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 项目结构查询DTO
 */
@Data
public class ProjectStructureQueryDTO {
    
    /**
     * 页码（默认1）
     */
    private Integer page = 1;
    
    /**
     * 每页数量（默认10）
     */
    private Integer pageSize = 10;
}
