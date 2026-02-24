package com.victor.iatms.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.victor.iatms.entity.po.ScheduledTaskExecution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务执行记录Mapper接口
 */
@Mapper
public interface ScheduledTaskExecutionMapper extends BaseMapper<ScheduledTaskExecution> {

    /**
     * 根据任务ID查询执行记录
     */
    List<ScheduledTaskExecution> selectByTaskId(@Param("taskId") Long taskId);

    /**
     * 查询任务最近的执行记录
     */
    List<ScheduledTaskExecution> selectRecentByTaskId(@Param("taskId") Long taskId, @Param("limit") Integer limit);

    /**
     * 根据ID查询执行记录
     */
    ScheduledTaskExecution selectByPrimaryKey(@Param("executionId") Long executionId);

    /**
     * 更新执行状态
     */
    int updateStatus(@Param("executionId") Long executionId, @Param("status") String status);

    /**
     * 更新执行结果
     */
    int updateExecutionResult(@Param("executionId") Long executionId,
                              @Param("status") String status,
                              @Param("actualStartTime") LocalDateTime actualStartTime,
                              @Param("actualEndTime") LocalDateTime actualEndTime,
                              @Param("durationSeconds") Integer durationSeconds,
                              @Param("totalCases") Integer totalCases,
                              @Param("passedCases") Integer passedCases,
                              @Param("failedCases") Integer failedCases,
                              @Param("skippedCases") Integer skippedCases,
                              @Param("successRate") java.math.BigDecimal successRate,
                              @Param("errorMessage") String errorMessage);

    /**
     * 统计任务执行记录数量
     */
    int countByTaskId(@Param("taskId") Long taskId);

    /**
     * 统计任务的平均执行时长
     */
    java.math.BigDecimal selectAvgDurationByTaskId(@Param("taskId") Long taskId);

    /**
     * 统计任务的最大执行时长
     */
    Integer selectMaxDurationByTaskId(@Param("taskId") Long taskId);

    /**
     * 统计任务的最小执行时长
     */
    Integer selectMinDurationByTaskId(@Param("taskId") Long taskId);

    /**
     * 查询任务在某时间范围内的执行记录
     */
    List<ScheduledTaskExecution> selectByTaskIdAndTimeRange(
            @Param("taskId") Long taskId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 更新通知状态
     */
    int updateNotificationStatus(@Param("executionId") Long executionId,
                                  @Param("notificationSent") Boolean notificationSent,
                                  @Param("notificationChannels") String notificationChannels,
                                  @Param("notificationTimestamp") LocalDateTime notificationTimestamp);
}

