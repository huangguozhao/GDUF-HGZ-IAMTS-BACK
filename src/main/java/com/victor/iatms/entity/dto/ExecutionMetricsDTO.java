package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 执行指标统计DTO
 */
@Data
public class ExecutionMetricsDTO {

    /**
     * 总执行耗时（毫秒）
     */
    @JsonProperty("total_duration")
    private Long totalDuration;

    /**
     * 平均并发数
     */
    @JsonProperty("avg_concurrency")
    private BigDecimal avgConcurrency;

    /**
     * 峰值并发数
     */
    @JsonProperty("peak_concurrency")
    private Integer peakConcurrency;

    /**
     * 吞吐量（次/分钟）
     */
    private BigDecimal throughput;

    /**
     * 可靠性（百分比）
     */
    private BigDecimal reliability;
}

