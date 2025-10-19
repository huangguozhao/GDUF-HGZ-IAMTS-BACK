package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 同比环比数据DTO
 */
@Data
public class ComparisonDataDTO {

    /**
     * 上一周期数据
     */
    @JsonProperty("previous_period")
    private PeriodComparisonDTO previousPeriod;

    /**
     * 同比数据（去年同期）
     */
    @JsonProperty("year_over_year")
    private PeriodComparisonDTO yearOverYear;

    /**
     * 周期对比数据
     */
    @Data
    public static class PeriodComparisonDTO {
        
        /**
         * 成功率
         */
        @JsonProperty("success_rate")
        private java.math.BigDecimal successRate;

        /**
         * 变化百分比
         */
        @JsonProperty("change_percent")
        private java.math.BigDecimal changePercent;

        /**
         * 趋势（up/down/stable）
         */
        private String trend;
    }
}

