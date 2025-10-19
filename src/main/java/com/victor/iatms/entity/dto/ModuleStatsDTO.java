package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 模块统计排行DTO
 */
@Data
public class ModuleStatsDTO {

    /**
     * 模块ID
     */
    @JsonProperty("module_id")
    private Integer moduleId;

    /**
     * 模块名称
     */
    @JsonProperty("module_name")
    private String moduleName;

    /**
     * 执行次数
     */
    private Long executions;

    /**
     * 成功率
     */
    @JsonProperty("success_rate")
    private BigDecimal successRate;

    /**
     * 平均执行时长
     */
    @JsonProperty("avg_duration")
    private Long avgDuration;
}

