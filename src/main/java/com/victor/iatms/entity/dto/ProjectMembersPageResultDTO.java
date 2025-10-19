package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 项目成员分页结果DTO
 */
@Data
public class ProjectMembersPageResultDTO {
    
    /**
     * 总条数
     */
    private Long total;
    
    /**
     * 成员列表
     */
    private List<ProjectMemberDTO> items;
    
    /**
     * 当前页码
     */
    private Integer page;
    
    /**
     * 每页条数
     */
    private Integer pageSize;
    
    /**
     * 成员统计摘要
     */
    private ProjectMembersSummaryDTO summary;
}
