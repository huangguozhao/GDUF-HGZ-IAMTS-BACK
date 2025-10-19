package com.victor.iatms.entity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 报告统计摘要DTO
 */
@Data
public class ReportSummaryDTO {
    
    /**
     * 总报告数
     */
    private Long totalReports;
    
    /**
     * 按报告类型统计
     */
    private Map<String, Long> byType;
    
    /**
     * 按报告状态统计
     */
    private Map<String, Long> byStatus;
    
    /**
     * 按环境统计
     */
    private Map<String, Long> byEnvironment;
    
    /**
     * 平均成功率
     */
    private BigDecimal avgSuccessRate;
}
