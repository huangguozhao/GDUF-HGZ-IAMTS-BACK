package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 系统状态信息DTO
 */
@Data
public class SystemStatusDTO {

    /**
     * 总用例数
     */
    @JsonProperty("total_cases")
    private Long totalCases;

    /**
     * 活跃项目数
     */
    @JsonProperty("active_projects")
    private Integer activeProjects;

    /**
     * 今日执行次数
     */
    @JsonProperty("today_executions")
    private Long todayExecutions;

    /**
     * 系统健康状态
     */
    @JsonProperty("system_health")
    private String systemHealth;
}



