package com.victor.iatms.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建定时任务请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduledTaskDTO {

    @NotBlank(message = "任务名称不能为空")
    private String taskName;

    private String description;

    @NotBlank(message = "任务类型不能为空")
    private String taskType; // single_case, module, project, test_suite, api

    @NotNull(message = "目标ID不能为空")
    private Integer targetId;

    @NotBlank(message = "触发器类型不能为空")
    private String triggerType; // cron, simple, daily, weekly, monthly

    // 条件字段根据triggerType
    private String cronExpression;
    private Long simpleRepeatInterval;
    private Integer simpleRepeatCount;
    private Integer dailyHour;
    private Integer dailyMinute;
    private String weeklyDays;
    private Integer monthlyDay;

    // 执行配置
    private String executionEnvironment; // dev, test, prod, staging
    private String baseUrl;
    private Integer timeoutSeconds;
    private Integer concurrency;
    private String executionStrategy; // sequential, parallel, by_module, smart

    // 重试配置
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
}

