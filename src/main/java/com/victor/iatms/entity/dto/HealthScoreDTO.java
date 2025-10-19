package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 质量健康评分DTO
 */
@Data
public class HealthScoreDTO {

    /**
     * 总体评分
     */
    private Integer overall;

    /**
     * 执行质量评分
     */
    @JsonProperty("execution_quality")
    private Integer executionQuality;

    /**
     * 用例覆盖率评分
     */
    @JsonProperty("case_coverage")
    private Integer caseCoverage;

    /**
     * 缺陷密度评分
     */
    @JsonProperty("defect_density")
    private Integer defectDensity;

    /**
     * 趋势
     */
    private String trend;
}

