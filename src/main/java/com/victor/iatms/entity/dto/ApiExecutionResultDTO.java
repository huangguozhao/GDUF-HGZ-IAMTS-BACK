package com.victor.iatms.entity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 接口执行结果DTO
 */
@Data
public class ApiExecutionResultDTO {
    // For Async Execution
    private String taskId;
    private Integer apiId;
    private String apiName;
    private String apiMethod;
    private String apiPath;
    private Integer totalCases;
    private Integer filteredCases;
    private String status; // queued, running, completed, failed, cancelled
    private Integer concurrency;
    private Integer estimatedDuration; // seconds
    private Integer queuePosition;
    private String monitorUrl;
    private String reportUrl;

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
    private List<CaseResult> caseResults; // 用例执行结果列表
    private Map<String, Object> summary; // 汇总统计信息
    private Long reportId;
    private String detailUrl;

    @Data
    public static class CaseResult {
        private Integer caseId;
        private String caseCode;
        private String caseName;
        private String status;
        private Long duration;
        private Integer responseStatus;
        private String failureMessage;
        private String logsLink;
    }
}
