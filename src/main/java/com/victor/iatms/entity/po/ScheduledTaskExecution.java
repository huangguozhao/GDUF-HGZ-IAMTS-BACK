package com.victor.iatms.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 定时任务执行记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("ScheduledTaskExecutions")
public class ScheduledTaskExecution {

    @TableId(type = IdType.AUTO)
    private Long executionId;

    // 关联信息
    private Long taskId;
    private Long testExecutionRecordId;
    private String quartzJobId;

    // 执行状态
    private String status; // pending, running, success, failed, skipped, timeout, cancelled

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

    // 审计字段
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

