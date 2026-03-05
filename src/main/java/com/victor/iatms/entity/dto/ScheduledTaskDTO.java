package com.victor.iatms.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务详情响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTaskDTO {

    private Long taskId;
    private String taskName;
    private String description;
    private String taskType;
    private Integer targetId;
    private String targetName;
    private List<Integer> caseIds;

    // 调度配置
    private String triggerType;
    private String cronExpression;
    private Long simpleRepeatInterval;
    private Integer simpleRepeatCount;
    private Integer dailyHour;
    private Integer dailyMinute;
    private String weeklyDays;
    private Integer monthlyDay;

    // 执行配置
    private String executionEnvironment;
    private String baseUrl;
    private Integer timeoutSeconds;
    private Integer concurrency;
    private String executionStrategy;
    private Boolean retryEnabled;
    private Integer maxRetryAttempts;
    private Long retryDelayMs;

    // 通知配置
    private Boolean notifyOnSuccess;
    private Boolean notifyOnFailure;
    private String notificationRecipients;

    // 执行限制
    private Boolean skipIfPreviousFailed;
    private Integer maxDurationSeconds;

    // 状态
    private Boolean isEnabled;
    private Double successRate;

    // 执行统计
    private Integer totalExecutions;
    private Integer successfulExecutions;
    private Integer failedExecutions;
    private Integer skippedExecutions;

    // 时间信息
    private LocalDateTime nextTriggerTime;
    private LocalDateTime lastExecutionTime;
    private String lastExecutionStatus;

    // 审计信息
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
