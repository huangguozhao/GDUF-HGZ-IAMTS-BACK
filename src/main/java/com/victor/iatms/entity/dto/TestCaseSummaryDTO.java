package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.Map;

/**
 * 测试用例统计摘要DTO
 */
@Data
public class TestCaseSummaryDTO {

    /**
     * 总用例数
     */
    private Long totalCases;

    /**
     * 按优先级统计
     */
    private Map<String, Long> byPriority;

    /**
     * 按严重程度统计
     */
    private Map<String, Long> bySeverity;

    /**
     * 按状态统计
     */
    private Map<String, Long> byStatus;
}
