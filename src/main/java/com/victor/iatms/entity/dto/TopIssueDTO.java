package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 主要问题统计DTO
 */
@Data
public class TopIssueDTO {

    /**
     * 失败类型
     */
    @JsonProperty("failure_type")
    private String failureType;

    /**
     * 出现次数
     */
    private Long count;

    /**
     * 占比（百分比）
     */
    private BigDecimal percentage;

    /**
     * 平均执行时长（毫秒）
     */
    @JsonProperty("avg_duration")
    private Long avgDuration;
}



