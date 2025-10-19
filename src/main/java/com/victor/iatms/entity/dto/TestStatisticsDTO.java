package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 测试统计信息DTO
 */
@Data
public class TestStatisticsDTO {

    /**
     * 总体统计摘要
     */
    private StatisticsSummaryDTO summary;

    /**
     * 趋势数据（按时间分组）
     */
    @JsonProperty("trend_data")
    private List<TrendDataDTO> trendData;

    /**
     * 分组统计数据（按指定维度分组）
     */
    @JsonProperty("group_data")
    private List<GroupDataDTO> groupData;

    /**
     * 同比环比数据
     */
    @JsonProperty("comparison_data")
    private ComparisonDataDTO comparisonData;

    /**
     * 主要问题统计
     */
    @JsonProperty("top_issues")
    private List<TopIssueDTO> topIssues;

    /**
     * 执行指标统计
     */
    @JsonProperty("execution_metrics")
    private ExecutionMetricsDTO executionMetrics;
}

