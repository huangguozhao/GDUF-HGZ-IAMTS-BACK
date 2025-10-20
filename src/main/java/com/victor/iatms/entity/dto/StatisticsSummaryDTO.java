package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 统计摘要DTO
 */
@Data
public class StatisticsSummaryDTO {

    /**
     * 总执行次数
     */
    @JsonProperty("total_executions")
    private Long totalExecutions;

    /**
     * 总用例数
     */
    @JsonProperty("total_cases")
    private Long totalCases;

    /**
     * 通过数
     */
    private Long passed;

    /**
     * 失败数
     */
    private Long failed;

    /**
     * 异常数
     */
    private Long broken;

    /**
     * 跳过数
     */
    private Long skipped;

    /**
     * 成功率（百分比）
     */
    @JsonProperty("success_rate")
    private BigDecimal successRate;

    /**
     * 平均执行时长（毫秒）
     */
    @JsonProperty("avg_duration")
    private Long avgDuration;

    /**
     * 最大执行时长（毫秒）
     */
    @JsonProperty("max_duration")
    private Long maxDuration;

    /**
     * 最小执行时长（毫秒）
     */
    @JsonProperty("min_duration")
    private Long minDuration;

    /**
     * 统计开始时间
     */
    @JsonProperty("start_time")
    private String startTime;

    /**
     * 统计结束时间
     */
    @JsonProperty("end_time")
    private String endTime;
}


