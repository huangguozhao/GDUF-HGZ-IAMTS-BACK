package com.victor.iatms.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 定时任务执行统计DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTaskStatisticsDTO {

    private Long taskId;
    private String taskName;
    private String taskType;
    private String targetName;

    // 执行统计
    private Integer totalExecutions;
    private Integer successfulExecutions;
    private Integer failedExecutions;
    private Integer skippedExecutions;
    private Double successRate;

    // 时间统计
    private Double avgDurationSeconds;
    private Integer minDurationSeconds;
    private Integer maxDurationSeconds;

    // 最近执行记录
    private List<ScheduledTaskExecutionDTO> recentExecutions;

    // 每日统计（最近7天）
    private List<DailyStatisticsDTO> dailyStatistics;
}

