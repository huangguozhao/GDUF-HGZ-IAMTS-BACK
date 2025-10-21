package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 主要失败原因DTO
 */
@Data
public class TopFailureDTO {

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
     * 平均执行时长
     */
    @JsonProperty("avg_duration")
    private Long avgDuration;

    /**
     * 趋势（up/down/stable）
     */
    private String trend;

    /**
     * 主要用例（失败类型为assertion_failed时）
     */
    @JsonProperty("main_cases")
    private List<String> mainCases;

    /**
     * 环境（失败类型为connection_error时）
     */
    private String environment;
}



