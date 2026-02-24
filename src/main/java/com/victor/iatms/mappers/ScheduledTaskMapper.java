package com.victor.iatms.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.victor.iatms.entity.po.ScheduledTestTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 定时测试任务Mapper接口
 */
@Mapper
public interface ScheduledTaskMapper extends BaseMapper<ScheduledTestTask> {

    /**
     * 根据ID查询任务（包含未删除的）
     */
    ScheduledTestTask selectByPrimaryKey(@Param("taskId") Long taskId);

    /**
     * 逻辑删除任务
     */
    int deleteByPrimaryKey(@Param("taskId") Long taskId);

    /**
     * 更新任务启用状态
     */
    int updateEnabledStatus(@Param("taskId") Long taskId, @Param("isEnabled") Boolean isEnabled);

    /**
     * 更新任务的执行统计
     */
    int updateExecutionStatistics(@Param("taskId") Long taskId,
                                   @Param("totalExecutions") Integer totalExecutions,
                                   @Param("successfulExecutions") Integer successfulExecutions,
                                   @Param("failedExecutions") Integer failedExecutions,
                                   @Param("skippedExecutions") Integer skippedExecutions);

    /**
     * 更新下次触发时间
     */
    int updateNextTriggerTime(@Param("taskId") Long taskId, @Param("nextTriggerTime") java.time.LocalDateTime nextTriggerTime);

    /**
     * 更新最后执行状态
     */
    int updateLastExecutionStatus(@Param("taskId") Long taskId,
                                   @Param("lastExecutionTime") java.time.LocalDateTime lastExecutionTime,
                                   @Param("lastExecutionStatus") String lastExecutionStatus);

    /**
     * 统计指定用户的任务数量
     */
    int countByCreatedBy(@Param("userId") Integer userId);
}

