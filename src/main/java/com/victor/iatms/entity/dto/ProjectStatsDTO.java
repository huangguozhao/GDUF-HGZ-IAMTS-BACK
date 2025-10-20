package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 项目统计排行DTO
 */
@Data
public class ProjectStatsDTO {

    /**
     * 项目ID
     */
    @JsonProperty("project_id")
    private Integer projectId;

    /**
     * 项目名称
     */
    @JsonProperty("project_name")
    private String projectName;

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
     * 变化百分比
     */
    private BigDecimal change;
}


