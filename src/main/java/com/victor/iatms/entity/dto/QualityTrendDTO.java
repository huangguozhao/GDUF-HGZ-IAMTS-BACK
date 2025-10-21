package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 质量趋势对比DTO
 */
@Data
public class QualityTrendDTO {

    /**
     * 本周成功率（7天数据）
     */
    @JsonProperty("current_week_success")
    private List<BigDecimal> currentWeekSuccess;

    /**
     * 上周成功率（7天数据）
     */
    @JsonProperty("last_week_success")
    private List<BigDecimal> lastWeekSuccess;

    /**
     * 是否有改善
     */
    private Boolean improvement;
}



