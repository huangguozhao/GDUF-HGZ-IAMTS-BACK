package com.victor.iatms.service;

import com.victor.iatms.entity.dto.PendingTaskDTO;
import com.victor.iatms.entity.po.Task;

import java.util.List;

/**
 * 任务服务接口
 */
public interface TaskService {

    /**
     * 创建任务
     * @param task 任务信息
     * @param currentUserId 当前用户ID
     * @return 创建的任务ID
     */
    Long createTask(Task task, Integer currentUserId);

    /**
     * 更新任务
     * @param task 任务信息
     * @param currentUserId 当前用户ID
     * @return 是否成功
     */
    boolean updateTask(Task task, Integer currentUserId);

    /**
     * 根据ID获取任务
     * @param taskId 任务ID
     * @return 任务信息
     */
    Task getTaskById(Long taskId);

    /**
     * 删除任务
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean deleteTask(Long taskId);

    /**
     * 获取用户的待处理任务列表
     * @param userId 用户ID
     * @return 待处理任务列表
     */
    List<PendingTaskDTO> getUserPendingTasks(Integer userId);

    /**
     * 获取所有待处理任务（管理员用）
     * @return 待处理任务列表
     */
    List<PendingTaskDTO> getAllPendingTasks();

    /**
     * 根据项目ID获取任务列表
     * @param projectId 项目ID
     * @return 任务列表
     */
    List<Task> getTasksByProjectId(Integer projectId);

    /**
     * 更新任务状态
     * @param taskId 任务ID
     * @param status 新状态
     * @return 是否成功
     */
    boolean updateTaskStatus(Long taskId, String status);

    /**
     * 更新任务进度
     * @param taskId 任务ID
     * @param progress 进度
     * @return 是否成功
     */
    boolean updateTaskProgress(Long taskId, Integer progress);

    /**
     * 根据执行记录ID创建或获取任务
     * @param executionId 执行记录ID
     * @param taskTitle 任务标题
     * @param assigneeId 被分配人ID
     * @param assigneeName 被分配人姓名
     * @param projectId 项目ID
     * @param projectName 项目名称
     * @return 任务ID
     */
    Long createOrGetTaskByExecutionId(Long executionId, String taskTitle, 
                                       Integer assigneeId, String assigneeName,
                                       Integer projectId, String projectName);

    /**
     * 根据测试失败自动创建任务
     * @param executionId 执行记录ID
     * @param failureMessage 失败消息
     * @param assigneeId 被分配人ID
     * @param assigneeName 被分配人姓名
     * @param projectId 项目ID
     * @param projectName 项目名称
     * @return 创建的任务ID
     */
    Long createTaskFromFailure(Long executionId, String failureMessage,
                               Integer assigneeId, String assigneeName,
                               Integer projectId, String projectName);
}

