package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 个人测试概况DTO
 */
@Data
public class DashboardSummaryDTO {

    /**
     * 用户基本信息
     */
    @JsonProperty("user_info")
    private UserInfoDTO userInfo;

    /**
     * 执行统计信息
     */
    @JsonProperty("execution_stats")
    private ExecutionStatsDTO executionStats;

    /**
     * 项目统计概览
     */
    @JsonProperty("project_stats")
    private List<ProjectStatsDTO> projectStats;

    /**
     * 最近活动记录
     */
    @JsonProperty("recent_activity")
    private List<RecentActivityDTO> recentActivity;

    /**
     * 待办事项列表
     */
    @JsonProperty("pending_tasks")
    private List<PendingTaskDTO> pendingTasks;

    /**
     * 快捷操作链接
     */
    @JsonProperty("quick_actions")
    private List<QuickActionDTO> quickActions;

    /**
     * 系统状态信息
     */
    @JsonProperty("system_status")
    private SystemStatusDTO systemStatus;

    /**
     * 质量健康评分
     */
    @JsonProperty("health_score")
    private HealthScoreDTO healthScore;
}


