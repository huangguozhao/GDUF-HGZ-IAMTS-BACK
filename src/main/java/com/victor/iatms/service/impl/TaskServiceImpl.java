package com.victor.iatms.service.impl;

import com.victor.iatms.entity.dto.PendingTaskDTO;
import com.victor.iatms.entity.po.Task;
import com.victor.iatms.mappers.TaskMapper;
import com.victor.iatms.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务服务实现类
 */
@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public Long createTask(Task task, Integer currentUserId) {
        task.setCreatedBy(currentUserId);
        task.setUpdatedBy(currentUserId);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setIsDeleted(false);
        
        // 设置默认状态
        if (task.getStatus() == null) {
            task.setStatus("pending");
        }
        if (task.getPriority() == null) {
            task.setPriority(3); // 默认中等优先级
        }
        if (task.getProgress() == null) {
            task.setProgress(0);
        }
        
        taskMapper.insert(task);
        logger.info("创建任务成功: taskId={}, title={}", task.getTaskId(), task.getTaskTitle());
        return task.getTaskId();
    }

    @Override
    public boolean updateTask(Task task, Integer currentUserId) {
        task.setUpdatedBy(currentUserId);
        task.setUpdatedAt(LocalDateTime.now());
        
        int result = taskMapper.update(task);
        logger.info("更新任务: taskId={}, result={}", task.getTaskId(), result);
        return result > 0;
    }

    @Override
    public Task getTaskById(Long taskId) {
        return taskMapper.selectById(taskId);
    }

    @Override
    public boolean deleteTask(Long taskId) {
        int result = taskMapper.deleteById(taskId);
        logger.info("删除任务: taskId={}, result={}", taskId, result);
        return result > 0;
    }

    @Override
    public List<PendingTaskDTO> getUserPendingTasks(Integer userId) {
        return taskMapper.getUserPendingTasks(userId);
    }

    @Override
    public List<PendingTaskDTO> getAllPendingTasks() {
        return taskMapper.getAllPendingTasks();
    }

    @Override
    public List<Task> getTasksByProjectId(Integer projectId) {
        return taskMapper.selectByProjectId(projectId);
    }

    @Override
    public boolean updateTaskStatus(Long taskId, String status) {
        int result = taskMapper.updateStatus(taskId, status);
        
        // 如果任务完成，记录完成时间
        if ("completed".equals(status)) {
            Task task = taskMapper.selectById(taskId);
            if (task != null) {
                task.setCompletedDate(LocalDateTime.now());
                taskMapper.update(task);
            }
        }
        
        logger.info("更新任务状态: taskId={}, status={}, result={}", taskId, status, result);
        return result > 0;
    }

    @Override
    public boolean updateTaskProgress(Long taskId, Integer progress) {
        int result = taskMapper.updateProgress(taskId, progress);
        
        // 如果进度达到100%，自动更新状态为完成
        if (progress >= 100) {
            taskMapper.updateStatus(taskId, "completed");
        }
        
        logger.info("更新任务进度: taskId={}, progress={}, result={}", taskId, progress, result);
        return result > 0;
    }

    @Override
    public Long createOrGetTaskByExecutionId(Long executionId, String taskTitle, 
                                             Integer assigneeId, String assigneeName,
                                             Integer projectId, String projectName) {
        // 先查询是否已存在
        Task existingTask = taskMapper.selectByExecutionId(executionId);
        if (existingTask != null) {
            logger.info("任务已存在: taskId={}, executionId={}", existingTask.getTaskId(), executionId);
            return existingTask.getTaskId();
        }
        
        // 创建新任务
        Task task = Task.builder()
                .taskType("api_test")
                .taskTitle(taskTitle)
                .executionId(executionId)
                .assigneeId(assigneeId)
                .assigneeName(assigneeName)
                .projectId(projectId)
                .projectName(projectName)
                .status("pending")
                .priority(2) // 默认高优先级
                .progress(0)
                .build();
        
        createTask(task, assigneeId);
        logger.info("创建任务成功: taskId={}, executionId={}", task.getTaskId(), executionId);
        return task.getTaskId();
    }

    @Override
    public Long createTaskFromFailure(Long executionId, String failureMessage,
                                      Integer assigneeId, String assigneeName,
                                      Integer projectId, String projectName) {
        // 生成任务标题
        String taskTitle = "测试失败待处理: " + (failureMessage != null && failureMessage.length() > 50 
                ? failureMessage.substring(0, 50) + "..." 
                : failureMessage);
        
        return createOrGetTaskByExecutionId(executionId, taskTitle, 
                                            assigneeId, assigneeName, 
                                            projectId, projectName);
    }
}

