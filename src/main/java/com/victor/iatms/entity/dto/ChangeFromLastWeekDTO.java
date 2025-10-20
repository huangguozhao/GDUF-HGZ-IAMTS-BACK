package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 与上周对比变化DTO
 */
@Data
public class ChangeFromLastWeekDTO {

    /**
     * 成功率变化
     */
    @JsonProperty("success_rate_change")
    private BigDecimal successRateChange;

    /**
     * 趋势（up/down/stable）
     */
    private String trend;

    /**
     * 执行次数变化
     */
    @JsonProperty("executions_change")
    private BigDecimal executionsChange;
}


