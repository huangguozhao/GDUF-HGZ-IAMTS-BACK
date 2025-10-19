package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 报告分页结果DTO
 */
@Data
public class ReportPageResultDTO {
    
    /**
     * 符合条件的数据总条数
     */
    private Long total;
    
    /**
     * 当前页的报告数据列表
     */
    private List<ReportListResponseDTO> items;
    
    /**
     * 当前页码
     */
    private Integer page;
    
    /**
     * 当前每页条数
     */
    private Integer pageSize;
    
    /**
     * 报告统计摘要
     */
    private ReportSummaryDTO summary;
}
