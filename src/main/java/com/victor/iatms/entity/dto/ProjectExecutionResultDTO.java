package com.victor.iatms.entity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 项目执行结果DTO
 */
@Data
public class ProjectExecutionResultDTO {
    // For Async Execution
    private String taskId;
    private Integer projectId;
    private String projectName;
    private Integer totalModules;
    private Integer filteredModules;
    private Integer totalCases;
    private Integer filteredCases;
    private String status; // queued, running, completed, failed, cancelled
    private Integer concurrency;
    private Integer estimatedDuration; // seconds
    private Integer queuePosition;
    private String monitorUrl;
    private String reportUrl;
    private String cancelUrl;

    // For Sync Execution
    private Long executionId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long totalDuration; // milliseconds
    private Integer passed;
    private Integer failed;
    private Integer skipped;
    private Integer broken;
    private BigDecimal successRate;
    private Map<String, Object> details; // 详细统计，如按模块、按优先级统计
    private Long reportId;
    private String summaryUrl;
    private String downloadUrl;
}
