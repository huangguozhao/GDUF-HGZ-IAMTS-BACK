package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 测试结果统计摘要DTO
 */
@Data
public class TestResultSummaryDTO {

    /**
     * 总记录数
     */
    @JsonProperty("total_count")
    private Long totalCount;

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
     * 未知数
     */
    private Long unknown;

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

    public TestResultSummaryDTO() {
        this.totalCount = 0L;
        this.passed = 0L;
        this.failed = 0L;
        this.broken = 0L;
        this.skipped = 0L;
        this.unknown = 0L;
        this.successRate = BigDecimal.ZERO;
        this.avgDuration = 0L;
    }
}

