package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 趋势数据DTO
 */
@Data
public class TrendDataDTO {

    /**
     * 时间周期标识
     */
    @JsonProperty("time_period")
    private String timePeriod;

    /**
     * 时间标签
     */
    private String label;

    /**
     * 总执行数
     */
    private Long total;

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
     * 平均时长
     */
    @JsonProperty("avg_duration")
    private Long avgDuration;
}



