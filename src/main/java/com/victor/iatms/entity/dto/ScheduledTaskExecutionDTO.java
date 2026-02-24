package com.victor.iatms.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 定时任务执行记录响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTaskExecutionDTO {

    private Long executionId;
    private Long taskId;
    private String taskName;
    private Long testExecutionRecordId;
    private String quartzJobId;
    private String status;

    // 时间信息
    private LocalDateTime scheduledTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private Integer durationSeconds;
    private Integer delaySeconds;

    // 执行统计
    private Integer totalCases;
    private Integer passedCases;
    private Integer failedCases;
    private Integer skippedCases;
    private BigDecimal successRate;

    // 错误信息
    private String errorMessage;
    private String stackTrace;

    // 重试信息
    private Integer retryCount;
    private Boolean isRetry;
    private Long originalExecutionId;

    // 通知状态
    private Boolean notificationSent;
    private String notificationChannels;
    private LocalDateTime notificationTimestamp;

    // 审计信息
    private LocalDateTime createdAt;
}
