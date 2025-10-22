package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 总体统计摘要DTO
 */
@Data
public class SummaryDTO {

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
     * 成功率
     */
    @JsonProperty("success_rate")
    private BigDecimal successRate;

    /**
     * 平均执行时长（ms）
     */
    @JsonProperty("avg_duration")
    private Long avgDuration;

    /**
     * 与上周对比变化
     */
    @JsonProperty("change_from_last_week")
    private ChangeFromLastWeekDTO changeFromLastWeek;
}




