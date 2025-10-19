package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 项目分页结果DTO
 */
@Data
public class ProjectPageResultDTO {
    
    /**
     * 总条数
     */
    private Long total;
    
    /**
     * 项目列表
     */
    private List<ProjectListResponseDTO> items;
    
    /**
     * 当前页码
     */
    private Integer page;
    
    /**
     * 每页条数
     */
    private Integer pageSize;
}
