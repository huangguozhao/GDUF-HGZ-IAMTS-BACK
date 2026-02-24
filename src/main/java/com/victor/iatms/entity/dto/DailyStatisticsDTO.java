package com.victor.iatms.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 每日统计DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatisticsDTO {

    private String date;
    private Integer executionCount;
    private Integer passedCount;
    private Integer failedCount;
    private Integer skippedCount;
    private BigDecimal successRate;
    private Double avgDurationSeconds;
}

