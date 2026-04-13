package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 项目结构分页响应DTO
 */
@Data
public class ProjectStructurePageDTO {
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 当前页码
     */
    private Integer page;
    
    /**
     * 每页数量
     */
    private Integer pageSize;
    
    /**
     * 项目结构列表
     */
    private List<ProjectStructureDTO> items;
}
