package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 日期范围信息DTO
 */
@Data
public class DateRangeDTO {

    /**
     * 开始日期（7天前）
     */
    @JsonProperty("start_date")
    private String startDate;

    /**
     * 结束日期（当天）
     */
    @JsonProperty("end_date")
    private String endDate;

    /**
     * 天数（7天）
     */
    private Integer days;
}




