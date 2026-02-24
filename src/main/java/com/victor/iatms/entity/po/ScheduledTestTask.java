package com.victor.iatms.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 定时测试任务实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("ScheduledTestTasks")
public class ScheduledTestTask {

    @TableId(type = IdType.AUTO)
    private Long taskId;

    // 基本信息
    private String taskName;
    private String description;
    private String taskType; // single_case, module, project, test_suite, api

    // 执行目标
    private Integer targetId;
    private String targetName;

    // 调度配置
    private String triggerType; // cron, simple, daily, weekly, monthly
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

    // 状态信息
    private Boolean isEnabled;
    private Boolean isDeleted;

    // 执行统计
    private Integer totalExecutions;
    private Integer successfulExecutions;
    private Integer failedExecutions;
    private Integer skippedExecutions;

    // 时间信息
    private LocalDateTime nextTriggerTime;
    private LocalDateTime lastExecutionTime;
    private String lastExecutionStatus; // pending, running, success, failed, skipped

    // 创建和修改信息
    private Integer createdBy;
    private LocalDateTime createdAt;
    private Integer updatedBy;
    private LocalDateTime updatedAt;
    private Integer deletedBy;
    private LocalDateTime deletedAt;

    /**
     * 获取成功率百分比
     */
    public Double getSuccessRate() {
        if (totalExecutions == null || totalExecutions == 0) {
            return 0.0;
        }
        return (successfulExecutions * 100.0) / totalExecutions;
    }
}

