package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 性能指标DTO
 */
@Data
public class PerformanceDTO {

    /**
     * 响应时间（毫秒）
     */
    @JsonProperty("response_time")
    private Long responseTime;

    /**
     * 吞吐量（请求/秒）
     */
    private BigDecimal throughput;

    /**
     * 内存使用（MB）
     */
    @JsonProperty("memory_usage")
    private Long memoryUsage;

    /**
     * CPU使用率（%）
     */
    @JsonProperty("cpu_usage")
    private BigDecimal cpuUsage;
}



