package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 系统状态信息DTO
 */
@Data
public class SystemStatusDTO {

    /**
     * CPU使用率 (0-100)
     */
    @JsonProperty("cpu_usage")
    private Integer cpuUsage;

    /**
     * 内存使用率 (0-100)
     */
    @JsonProperty("memory_usage")
    private Integer memoryUsage;

    /**
     * 磁盘使用率 (0-100)
     */
    @JsonProperty("disk_usage")
    private Integer diskUsage;

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

    /**
     * 服务器操作系统
     */
    @JsonProperty("os_name")
    private String osName;

    /**
     * 服务器运行时间（秒）
     */
    @JsonProperty("uptime")
    private Long uptime;
}




