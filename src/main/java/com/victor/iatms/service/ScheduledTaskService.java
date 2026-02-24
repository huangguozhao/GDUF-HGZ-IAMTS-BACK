package com.victor.iatms.service;

import com.victor.iatms.entity.dto.*;
import com.victor.iatms.entity.po.ScheduledTestTask;
import com.victor.iatms.entity.vo.PaginationResultVO;

/**
 * 定时测试任务服务接口
 */
public interface ScheduledTaskService {

    /**
     * 创建定时任务
     * @param dto 创建任务参数
     * @param userId 当前用户ID
     * @return 创建的任务详情
     */
    ScheduledTaskDTO createScheduledTask(CreateScheduledTaskDTO dto, Integer userId);

    /**
     * 更新定时任务
     * @param taskId 任务ID
     * @param dto 更新参数
     * @param userId 当前用户ID
     * @return 更新后的任务详情
     */
    ScheduledTaskDTO updateScheduledTask(Long taskId, CreateScheduledTaskDTO dto, Integer userId);

    /**
     * 删除定时任务
     * @param taskId 任务ID
     * @param userId 当前用户ID
     */
    void deleteScheduledTask(Long taskId, Integer userId);

    /**
     * 获取任务详情
     * @param taskId 任务ID
     * @param userId 当前用户ID
     * @return 任务详情
     */
    ScheduledTaskDTO getScheduledTask(Long taskId, Integer userId);

    /**
     * 分页查询任务列表
     * @param query 查询参数
     * @param userId 当前用户ID
     * @return 分页结果
     */
    PaginationResultVO<ScheduledTaskDTO> listScheduledTasks(ScheduledTaskQueryDTO query, Integer userId);

    /**
     * 启用任务
     * @param taskId 任务ID
     * @param userId 当前用户ID
     */
    void enableScheduledTask(Long taskId, Integer userId);

    /**
     * 禁用任务
     * @param taskId 任务ID
     * @param userId 当前用户ID
     */
    void disableScheduledTask(Long taskId, Integer userId);

    /**
     * 立即执行任务
     * @param taskId 任务ID
     * @param userId 当前用户ID
     * @return 执行记录ID
     */
    Long executeScheduledTaskNow(Long taskId, Integer userId);

    /**
     * 内部执行方法（由Quartz调用）
     * @param taskId 任务ID
     * @param userId 用户ID
     * @param isTriggered 是否由触发器触发
     */
    void executeScheduledTask(Long taskId, Integer userId, boolean isTriggered);

    /**
     * 获取执行历史
     * @param taskId 任务ID
     * @param page 页码
     * @param size 每页大小
     * @return 执行历史分页结果
     */
    PaginationResultVO<ScheduledTaskExecutionDTO> getExecutionHistory(Long taskId, int page, int size);

    /**
     * 获取执行统计
     * @param taskId 任务ID
     * @return 统计信息
     */
    ScheduledTaskStatisticsDTO getExecutionStatistics(Long taskId);

    /**
     * 获取执行记录详情
     * @param executionId 执行记录ID
     * @return 执行详情
     */
    ScheduledTaskExecutionDTO getExecutionDetail(Long executionId);

    /**
     * 创建Quartz任务
     * @param task 任务实体
     */
    void createQuartzJob(ScheduledTestTask task);

    /**
     * 更新Quartz任务
     * @param task 任务实体
     */
    void updateQuartzJob(ScheduledTestTask task);

    /**
     * 删除Quartz任务
     * @param taskId 任务ID
     */
    void deleteQuartzJob(Long taskId);

    /**
     * 暂停Quartz任务
     * @param taskId 任务ID
     */
    void pauseQuartzJob(Long taskId);

    /**
     * 恢复Quartz任务
     * @param taskId 任务ID
     */
    void resumeQuartzJob(Long taskId);
}

