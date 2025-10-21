package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 执行统计信息DTO
 */
@Data
public class ExecutionStatsDTO {

    /**
     * 总执行次数
     */
    @JsonProperty("total_executions")
    private Long totalExecutions;

    /**
     * 成功率
     */
    @JsonProperty("success_rate")
    private BigDecimal successRate;

    /**
     * 平均执行时长
     */
    @JsonProperty("avg_duration")
    private Long avgDuration;

    /**
     * 创建的用例数
     */
    @JsonProperty("cases_created")
    private Long casesCreated;

    /**
     * 维护的用例数
     */
    @JsonProperty("cases_maintained")
    private Long casesMaintained;

    /**
     * 发现的缺陷数
     */
    @JsonProperty("bugs_found")
    private Long bugsFound;

    /**
     * 趋势（up/down/stable）
     */
    private String trend;

    /**
     * 变化百分比
     */
    @JsonProperty("change_percent")
    private BigDecimal changePercent;
}



