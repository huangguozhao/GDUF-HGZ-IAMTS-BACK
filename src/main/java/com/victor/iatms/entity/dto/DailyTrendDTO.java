package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 每日执行趋势DTO
 */
@Data
public class DailyTrendDTO {

    /**
     * 日期（YYYY-MM-DD）
     */
    private String date;

    /**
     * 星期几
     */
    @JsonProperty("day_of_week")
    private String dayOfWeek;

    /**
     * 当日总执行数
     */
    private Long total;

    /**
     * 当日通过数
     */
    private Long passed;

    /**
     * 当日失败数
     */
    private Long failed;

    /**
     * 当日异常数
     */
    private Long broken;

    /**
     * 当日跳过数
     */
    private Long skipped;

    /**
     * 当日成功率
     */
    @JsonProperty("success_rate")
    private BigDecimal successRate;

    /**
     * 当日平均时长
     */
    @JsonProperty("avg_duration")
    private Long avgDuration;
}




