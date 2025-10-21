package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 近七天测试执行情况DTO
 */
@Data
public class WeeklyExecutionDTO {

    /**
     * 日期范围信息
     */
    @JsonProperty("date_range")
    private DateRangeDTO dateRange;

    /**
     * 总体统计摘要
     */
    private SummaryDTO summary;

    /**
     * 每日执行趋势
     */
    @JsonProperty("daily_trend")
    private List<DailyTrendDTO> dailyTrend;

    /**
     * 项目统计排行（前5）
     */
    @JsonProperty("project_stats")
    private List<ProjectStatsDTO> projectStats;

    /**
     * 模块统计排行（前5）
     */
    @JsonProperty("module_stats")
    private List<ModuleStatsDTO> moduleStats;

    /**
     * 主要失败原因
     */
    @JsonProperty("top_failures")
    private List<TopFailureDTO> topFailures;

    /**
     * 性能指标
     */
    @JsonProperty("performance_metrics")
    private PerformanceMetricsDTO performanceMetrics;

    /**
     * 质量趋势对比
     */
    @JsonProperty("quality_trend")
    private QualityTrendDTO qualityTrend;
}



