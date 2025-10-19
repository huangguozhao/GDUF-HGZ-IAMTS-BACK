package com.victor.iatms.entity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 测试套件执行结果DTO
 */
@Data
public class TestSuiteExecutionResultDTO {
    // For Async Execution
    private String taskId;
    private Integer suiteId;
    private String suiteName;
    private String suiteCode;
    private Integer totalCases;
    private Integer filteredCases;
    private Integer estimatedCases;
    private String status; // preparing, queued, running, completed, failed, cancelled
    private Integer concurrency;
    private Integer estimatedDuration; // seconds
    private Integer queuePosition;
    private String executionPlanUrl;
    private String monitorUrl;
    private String reportUrl;
    private String cancelUrl;

    // For Sync Execution
    private Long executionId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long totalDuration; // milliseconds
    private Integer executedCases;
    private Integer passed;
    private Integer failed;
    private Integer skipped;
    private Integer retried;
    private BigDecimal successRate;
    private Map<String, Object> details; // 详细统计信息
    private ExecutionPlan executionPlan; // 执行计划信息
    private Long reportId;
    private String summaryUrl;
    private String downloadUrl;
    private String artifactsUrl;

    @Data
    public static class ExecutionPlan {
        private String strategy;
        private Integer concurrency;
        private Integer dependencyLevels;
        private Integer batches;
    }
}
