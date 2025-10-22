package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 性能指标DTO
 */
@Data
public class PerformanceMetricsDTO {

    /**
     * P95执行时长
     */
    @JsonProperty("p95_duration")
    private Long p95Duration;

    /**
     * P99执行时长
     */
    @JsonProperty("p99_duration")
    private Long p99Duration;

    /**
     * 最大执行时长
     */
    @JsonProperty("max_duration")
    private Long maxDuration;

    /**
     * 最小执行时长
     */
    @JsonProperty("min_duration")
    private Long minDuration;

    /**
     * 吞吐量（次/分钟）
     */
    private BigDecimal throughput;
}




