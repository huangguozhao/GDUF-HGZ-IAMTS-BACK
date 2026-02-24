package com.victor.iatms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.victor.iatms.entity.dto.*;
import com.victor.iatms.entity.po.ScheduledTaskExecution;
import com.victor.iatms.entity.po.ScheduledTestTask;
import com.victor.iatms.entity.vo.PaginationResultVO;
import com.victor.iatms.mappers.ScheduledTaskExecutionMapper;
import com.victor.iatms.mappers.ScheduledTaskMapper;
import com.victor.iatms.quartz.ScheduledTestTaskJob;
import com.victor.iatms.service.ScheduledTaskService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 定时测试任务服务实现类
 */
@Slf4j
@Service
public class ScheduledTaskServiceImpl implements ScheduledTaskService {

    @Autowired
    private ScheduledTaskMapper scheduledTaskMapper;

    @Autowired
    private ScheduledTaskExecutionMapper scheduledTaskExecutionMapper;

    @Autowired
    private Scheduler scheduler;

    private Scheduler getScheduler() {
        return scheduler;
    }

    // ==================== 任务管理 ====================

    @Override
    @Transactional
    public ScheduledTaskDTO createScheduledTask(CreateScheduledTaskDTO dto, Integer userId) {
        // 创建任务实体
        ScheduledTestTask task = ScheduledTestTask.builder()
                .taskName(dto.getTaskName())
                .description(dto.getDescription())
                .taskType(dto.getTaskType())
                .targetId(dto.getTargetId())
                .targetName(dto.getTargetName())
                .triggerType(dto.getTriggerType())
                .cronExpression(dto.getCronExpression())
                .simpleRepeatInterval(dto.getSimpleRepeatInterval())
                .simpleRepeatCount(dto.getSimpleRepeatCount())
                .dailyHour(dto.getDailyHour())
                .dailyMinute(dto.getDailyMinute())
                .weeklyDays(dto.getWeeklyDays())
                .monthlyDay(dto.getMonthlyDay())
                .executionEnvironment(dto.getExecutionEnvironment())
                .baseUrl(dto.getBaseUrl())
                .timeoutSeconds(dto.getTimeoutSeconds())
                .concurrency(dto.getConcurrency())
                .executionStrategy(dto.getExecutionStrategy())
                .retryEnabled(dto.getRetryEnabled())
                .maxRetryAttempts(dto.getMaxRetryAttempts())
                .retryDelayMs(dto.getRetryDelayMs())
                .notifyOnSuccess(dto.getNotifyOnSuccess())
                .notifyOnFailure(dto.getNotifyOnFailure())
                .notificationRecipients(dto.getNotificationRecipients())
                .skipIfPreviousFailed(dto.getSkipIfPreviousFailed())
                .maxDurationSeconds(dto.getMaxDurationSeconds())
                .isEnabled(true)
                .isDeleted(false)
                .totalExecutions(0)
                .successfulExecutions(0)
                .failedExecutions(0)
                .skippedExecutions(0)
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();

        // 保存到数据库
        scheduledTaskMapper.insert(task);

        // 创建 Quartz 任务
        createQuartzJob(task);
        // 更新下次触发时间
        updateNextTriggerTime(task.getTaskId());

        return convertToDTO(task);
    }

    @Override
    @Transactional
    public ScheduledTaskDTO updateScheduledTask(Long taskId, CreateScheduledTaskDTO dto, Integer userId) {
        ScheduledTestTask task = scheduledTaskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在");
        }
        if (task.getIsDeleted()) {
            throw new IllegalArgumentException("任务已被删除");
        }

        // 更新任务实体
        task.setTaskName(dto.getTaskName());
        task.setDescription(dto.getDescription());
        task.setTaskType(dto.getTaskType());
        task.setTargetId(dto.getTargetId());
        task.setTriggerType(dto.getTriggerType());
        task.setCronExpression(dto.getCronExpression());
        task.setSimpleRepeatInterval(dto.getSimpleRepeatInterval());
        task.setSimpleRepeatCount(dto.getSimpleRepeatCount());
        task.setDailyHour(dto.getDailyHour());
        task.setDailyMinute(dto.getDailyMinute());
        task.setWeeklyDays(dto.getWeeklyDays());
        task.setMonthlyDay(dto.getMonthlyDay());
        task.setExecutionEnvironment(dto.getExecutionEnvironment());
        task.setBaseUrl(dto.getBaseUrl());
        task.setTimeoutSeconds(dto.getTimeoutSeconds());
        task.setConcurrency(dto.getConcurrency());
        task.setExecutionStrategy(dto.getExecutionStrategy());
        task.setRetryEnabled(dto.getRetryEnabled());
        task.setMaxRetryAttempts(dto.getMaxRetryAttempts());
        task.setRetryDelayMs(dto.getRetryDelayMs());
        task.setNotifyOnSuccess(dto.getNotifyOnSuccess());
        task.setNotifyOnFailure(dto.getNotifyOnFailure());
        task.setNotificationRecipients(dto.getNotificationRecipients());
        task.setSkipIfPreviousFailed(dto.getSkipIfPreviousFailed());
        task.setMaxDurationSeconds(dto.getMaxDurationSeconds());
        task.setUpdatedBy(userId);
        task.setUpdatedAt(LocalDateTime.now());

        // 更新数据库
        scheduledTaskMapper.updateById(task);

        // 更新 Quartz 任务
        updateQuartzJob(task);
        updateNextTriggerTime(task.getTaskId());

        return convertToDTO(task);
    }

    @Override
    @Transactional
    public void deleteScheduledTask(Long taskId, Integer userId) {
        ScheduledTestTask task = scheduledTaskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在");
        }

        // 逻辑删除
        task.setIsDeleted(true);
        task.setDeletedBy(userId);
        task.setDeletedAt(LocalDateTime.now());
        scheduledTaskMapper.updateById(task);

        // 删除 Quartz 任务
        deleteQuartzJob(taskId);
    }

    @Override
    public ScheduledTaskDTO getScheduledTask(Long taskId, Integer userId) {
        ScheduledTestTask task = scheduledTaskMapper.selectByPrimaryKey(taskId);
        if (task == null || task.getIsDeleted()) {
            throw new IllegalArgumentException("任务不存在");
        }
        return convertToDTO(task);
    }

    @Override
    public PaginationResultVO<ScheduledTaskDTO> listScheduledTasks(ScheduledTaskQueryDTO query, Integer userId) {
        Page<ScheduledTestTask> page = new Page<>(query.getPage(), query.getPageSize());
        
        LambdaQueryWrapper<ScheduledTestTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduledTestTask::getIsDeleted, false);
        wrapper.eq(ScheduledTestTask::getCreatedBy, userId);
        
        if (StringUtils.hasText(query.getTaskName())) {
            wrapper.like(ScheduledTestTask::getTaskName, query.getTaskName());
        }
        if (StringUtils.hasText(query.getTaskType())) {
            wrapper.eq(ScheduledTestTask::getTaskType, query.getTaskType());
        }
        if (query.getTargetId() != null) {
            wrapper.eq(ScheduledTestTask::getTargetId, query.getTargetId());
        }
        if (StringUtils.hasText(query.getTriggerType())) {
            wrapper.eq(ScheduledTestTask::getTriggerType, query.getTriggerType());
        }
        if (query.getIsEnabled() != null) {
            wrapper.eq(ScheduledTestTask::getIsEnabled, query.getIsEnabled());
        }
        if (StringUtils.hasText(query.getExecutionEnvironment())) {
            wrapper.eq(ScheduledTestTask::getExecutionEnvironment, query.getExecutionEnvironment());
        }
        
        wrapper.orderByDesc(ScheduledTestTask::getCreatedAt);
        
        IPage<ScheduledTestTask> result = scheduledTaskMapper.selectPage(page, wrapper);
        
        PaginationResultVO<ScheduledTaskDTO> vo = new PaginationResultVO<>();
        vo.setPage(query.getPage());
        vo.setPageSize(query.getPageSize());
        vo.setTotal(result.getTotal());
        vo.setItems(result.getRecords().stream().map(this::convertToDTO).toList());
        
        return vo;
    }

    @Override
    @Transactional
    public void enableScheduledTask(Long taskId, Integer userId) {
        ScheduledTestTask task = scheduledTaskMapper.selectByPrimaryKey(taskId);
        if (task == null || task.getIsDeleted()) {
            throw new IllegalArgumentException("任务不存在");
        }
        
        task.setIsEnabled(true);
        task.setUpdatedBy(userId);
        task.setUpdatedAt(LocalDateTime.now());
        scheduledTaskMapper.updateById(task);
        
        resumeQuartzJob(taskId);
    }

    @Override
    @Transactional
    public void disableScheduledTask(Long taskId, Integer userId) {
        ScheduledTestTask task = scheduledTaskMapper.selectByPrimaryKey(taskId);
        if (task == null || task.getIsDeleted()) {
            throw new IllegalArgumentException("任务不存在");
        }
        
        task.setIsEnabled(false);
        task.setUpdatedBy(userId);
        task.setUpdatedAt(LocalDateTime.now());
        scheduledTaskMapper.updateById(task);
        
        pauseQuartzJob(taskId);
    }

    @Override
    @Transactional
    public Long executeScheduledTaskNow(Long taskId, Integer userId) {
        return executeTestTask(taskId, userId, false);
    }

    @Override
    @Transactional
    public void executeScheduledTask(Long taskId, Integer userId, boolean isTriggered) {
        executeTestTask(taskId, userId, isTriggered);
    }

    private Long executeTestTask(Long taskId, Integer userId, boolean isTriggered) {
        ScheduledTestTask task = scheduledTaskMapper.selectByPrimaryKey(taskId);
        if (task == null || task.getIsDeleted()) {
            throw new IllegalArgumentException("任务不存在");
        }
        
        // 检查是否启用
        if (!task.getIsEnabled() && !isTriggered) {
            throw new IllegalArgumentException("任务未启用");
        }
        
        // 检查上次是否失败且设置了跳过
        if (Boolean.TRUE.equals(task.getSkipIfPreviousFailed()) 
                && "failed".equals(task.getLastExecutionStatus())) {
            log.info("任务 {} 上次执行失败，跳过本次执行", taskId);
            return null;
        }
        
        // 创建执行记录
        ScheduledTaskExecution execution = ScheduledTaskExecution.builder()
                .taskId(taskId)
                .status("pending")
                .scheduledTime(LocalDateTime.now())
                .retryCount(0)
                .isRetry(false)
                .notificationSent(false)
                .createdAt(LocalDateTime.now())
                .build();
        scheduledTaskExecutionMapper.insert(execution);
        
        try {
            // 更新状态为运行中
            execution.setStatus("running");
            execution.setActualStartTime(LocalDateTime.now());
            scheduledTaskExecutionMapper.updateById(execution);
            
            // 更新任务状态
            task.setLastExecutionTime(LocalDateTime.now());
            task.setLastExecutionStatus("running");
            task.setTotalExecutions(task.getTotalExecutions() + 1);
            scheduledTaskMapper.updateById(task);
            
            // 执行测试逻辑
            executeTest(task, execution);
            
            // 更新执行结果
            execution.setStatus("success");
            execution.setActualEndTime(LocalDateTime.now());
            execution.setDurationSeconds((int) java.time.Duration.between(
                    execution.getActualStartTime(), execution.getActualEndTime()).getSeconds());
            
            task.setSuccessfulExecutions(task.getSuccessfulExecutions() + 1);
            task.setLastExecutionStatus("success");
            
        } catch (Exception e) {
            log.error("执行任务失败: taskId={}", taskId, e);
            
            execution.setStatus("failed");
            execution.setErrorMessage(e.getMessage());
            execution.setActualEndTime(LocalDateTime.now());
            execution.setDurationSeconds((int) java.time.Duration.between(
                    execution.getActualStartTime(), execution.getActualEndTime()).getSeconds());
            
            task.setFailedExecutions(task.getFailedExecutions() + 1);
            task.setLastExecutionStatus("failed");
            
            // 处理重试
            handleRetry(task, execution, userId);
        }
        
        // 更新统计
        scheduledTaskExecutionMapper.updateById(execution);
        scheduledTaskMapper.updateById(task);
        
        // 发送通知
        sendNotification(task, execution);
        
        return execution.getExecutionId();
    }

    private void executeTest(ScheduledTestTask task, ScheduledTaskExecution execution) {
        // 这里需要调用实际的测试执行服务
        log.info("执行定时测试任务: taskId={}, taskType={}, targetId={}", 
                task.getTaskId(), task.getTaskType(), task.getTargetId());
        
        // 模拟执行结果
        execution.setTotalCases(10);
        execution.setPassedCases(9);
        execution.setFailedCases(1);
        execution.setSkippedCases(0);
        execution.setSuccessRate(new BigDecimal("90.00"));
    }

    private void handleRetry(ScheduledTestTask task, ScheduledTaskExecution execution, Integer userId) {
        if (Boolean.TRUE.equals(task.getRetryEnabled()) 
                && task.getMaxRetryAttempts() != null 
                && execution.getRetryCount() < task.getMaxRetryAttempts()) {
            
            log.info("准备重试任务: taskId={}, retryCount={}", task.getTaskId(), execution.getRetryCount());
            
            // 延迟重试
            if (task.getRetryDelayMs() != null && task.getRetryDelayMs() > 0) {
                try {
                    Thread.sleep(task.getRetryDelayMs());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            // 创建重试执行记录
            ScheduledTaskExecution retryExecution = ScheduledTaskExecution.builder()
                    .taskId(task.getTaskId())
                    .status("pending")
                    .scheduledTime(LocalDateTime.now())
                    .retryCount(execution.getRetryCount() + 1)
                    .isRetry(true)
                    .originalExecutionId(execution.getExecutionId())
                    .createdAt(LocalDateTime.now())
                    .build();
            scheduledTaskExecutionMapper.insert(retryExecution);
            
            try {
                executeTest(task, retryExecution);
                retryExecution.setStatus("success");
                retryExecution.setActualEndTime(LocalDateTime.now());
                
                task.setSuccessfulExecutions(task.getSuccessfulExecutions() + 1);
                task.setLastExecutionStatus("success");
            } catch (Exception e) {
                retryExecution.setStatus("failed");
                retryExecution.setErrorMessage(e.getMessage());
                
                if (retryExecution.getRetryCount() >= task.getMaxRetryAttempts()) {
                    task.setFailedExecutions(task.getFailedExecutions() + 1);
                    task.setLastExecutionStatus("failed");
                }
            }
            
            scheduledTaskExecutionMapper.updateById(retryExecution);
            scheduledTaskMapper.updateById(task);
        }
    }

    private void sendNotification(ScheduledTestTask task, ScheduledTaskExecution execution) {
        boolean shouldNotify = ("success".equals(execution.getStatus()) && Boolean.TRUE.equals(task.getNotifyOnSuccess()))
                || ("failed".equals(execution.getStatus()) && Boolean.TRUE.equals(task.getNotifyOnFailure()));
        
        if (!shouldNotify || !StringUtils.hasText(task.getNotificationRecipients())) {
            return;
        }
        
        log.info("发送通知: recipients={}, status={}", task.getNotificationRecipients(), execution.getStatus());
        
        execution.setNotificationSent(true);
        execution.setNotificationChannels("email");
        execution.setNotificationTimestamp(LocalDateTime.now());
        scheduledTaskExecutionMapper.updateById(execution);
    }

    // ==================== 执行历史和统计 ====================

    @Override
    public PaginationResultVO<ScheduledTaskExecutionDTO> getExecutionHistory(Long taskId, int page, int size) {
        Page<ScheduledTaskExecution> pageObj = new Page<>(page, size);
        
        LambdaQueryWrapper<ScheduledTaskExecution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduledTaskExecution::getTaskId, taskId);
        wrapper.orderByDesc(ScheduledTaskExecution::getCreatedAt);
        
        IPage<ScheduledTaskExecution> result = scheduledTaskExecutionMapper.selectPage(pageObj, wrapper);
        
        PaginationResultVO<ScheduledTaskExecutionDTO> vo = new PaginationResultVO<>();
        vo.setPage(page);
        vo.setPageSize(size);
        vo.setTotal(result.getTotal());
        vo.setItems(result.getRecords().stream().map(this::convertExecutionToDTO).toList());
        
        return vo;
    }

    @Override
    public ScheduledTaskStatisticsDTO getExecutionStatistics(Long taskId) {
        ScheduledTestTask task = scheduledTaskMapper.selectByPrimaryKey(taskId);
        if (task == null || task.getIsDeleted()) {
            throw new IllegalArgumentException("任务不存在");
        }
        
        ScheduledTaskStatisticsDTO dto = new ScheduledTaskStatisticsDTO();
        dto.setTaskId(taskId);
        dto.setTaskName(task.getTaskName());
        dto.setTotalExecutions(task.getTotalExecutions());
        dto.setSuccessfulExecutions(task.getSuccessfulExecutions());
        dto.setFailedExecutions(task.getFailedExecutions());
        dto.setSkippedExecutions(task.getSkippedExecutions());
        dto.setSuccessRate(task.getSuccessRate());
        
        // 计算平均执行时长
        BigDecimal avgDuration = scheduledTaskExecutionMapper.selectAvgDurationByTaskId(taskId);
        dto.setAvgDurationSeconds(avgDuration != null ? avgDuration.doubleValue() : 0.0);
        
        // 计算最大/最小执行时长
        Integer maxDuration = scheduledTaskExecutionMapper.selectMaxDurationByTaskId(taskId);
        Integer minDuration = scheduledTaskExecutionMapper.selectMinDurationByTaskId(taskId);
        dto.setMaxDurationSeconds(maxDuration != null ? maxDuration : 0);
        dto.setMinDurationSeconds(minDuration != null ? minDuration : 0);
        
        return dto;
    }

    @Override
    public ScheduledTaskExecutionDTO getExecutionDetail(Long executionId) {
        ScheduledTaskExecution execution = scheduledTaskExecutionMapper.selectByPrimaryKey(executionId);
        if (execution == null) {
            throw new IllegalArgumentException("执行记录不存在");
        }
        return convertExecutionToDTO(execution);
    }

    // ==================== Quartz 任务管理 ====================

    @Override
    public void createQuartzJob(ScheduledTestTask task) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(ScheduledTestTaskJob.class)
                    .withIdentity(getJobKey(task.getTaskId()))
                    .usingJobData("taskId", task.getTaskId())
                    .usingJobData("userId", task.getCreatedBy())
                    .storeDurably()
                    .build();

            Trigger trigger = buildTrigger(task);

            getScheduler().scheduleJob(jobDetail, trigger);
            log.info("创建Quartz任务: taskId={}", task.getTaskId());
        } catch (SchedulerException e) {
            log.error("创建Quartz任务失败", e);
            throw new RuntimeException("创建Quartz任务失败: " + e.getMessage());
        }
    }

    @Override
    public void updateQuartzJob(ScheduledTestTask task) {
        try {
            JobKey jobKey = getJobKey(task.getTaskId());
            
            if (getScheduler().checkExists(jobKey)) {
                getScheduler().deleteJob(jobKey);
            }
            
            createQuartzJob(task);
            log.info("更新Quartz任务: taskId={}", task.getTaskId());
        } catch (SchedulerException e) {
            log.error("更新Quartz任务失败", e);
            throw new RuntimeException("更新Quartz任务失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteQuartzJob(Long taskId) {
        try {
            JobKey jobKey = getJobKey(taskId);
            if (getScheduler().checkExists(jobKey)) {
                getScheduler().deleteJob(jobKey);
                log.info("删除Quartz任务: taskId={}", taskId);
            }
        } catch (SchedulerException e) {
            log.error("删除Quartz任务失败", e);
            throw new RuntimeException("删除Quartz任务失败: " + e.getMessage());
        }
    }

    @Override
    public void pauseQuartzJob(Long taskId) {
        try {
            JobKey jobKey = getJobKey(taskId);
            if (getScheduler().checkExists(jobKey)) {
                getScheduler().pauseJob(jobKey);
                log.info("暂停Quartz任务: taskId={}", taskId);
            }
        } catch (SchedulerException e) {
            log.error("暂停Quartz任务失败", e);
            throw new RuntimeException("暂停Quartz任务失败: " + e.getMessage());
        }
    }

    @Override
    public void resumeQuartzJob(Long taskId) {
        try {
            JobKey jobKey = getJobKey(taskId);
            if (getScheduler().checkExists(jobKey)) {
                getScheduler().resumeJob(jobKey);
                log.info("恢复Quartz任务: taskId={}", taskId);
            }
        } catch (SchedulerException e) {
            log.error("恢复Quartz任务失败", e);
            throw new RuntimeException("恢复Quartz任务失败: " + e.getMessage());
        }
    }

    private Trigger buildTrigger(ScheduledTestTask task) throws SchedulerException {
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey(task.getTaskId()))
                .startNow();

        String triggerType = task.getTriggerType();
        
        switch (triggerType) {
            case "cron" -> {
                if (StringUtils.hasText(task.getCronExpression())) {
                    triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(task.getCronExpression())
                            .withMisfireHandlingInstructionDoNothing());
                }
            }
            case "simple" -> {
                SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
                if (task.getSimpleRepeatCount() != null && task.getSimpleRepeatCount() > 0) {
                    scheduleBuilder.withRepeatCount(task.getSimpleRepeatCount());
                } else {
                    scheduleBuilder.repeatForever();
                }
                if (task.getSimpleRepeatInterval() != null) {
                    scheduleBuilder.withIntervalInMilliseconds(task.getSimpleRepeatInterval());
                }
                triggerBuilder.withSchedule(scheduleBuilder);
            }
            case "daily" -> {
                // Quartz cron 表达式：秒 分 时 日 月 周，日用 ? 代替
                String cron = String.format("0 %d %d ? * *", 
                        task.getDailyMinute() != null ? task.getDailyMinute() : 0,
                        task.getDailyHour() != null ? task.getDailyHour() : 0);
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron)
                        .withMisfireHandlingInstructionDoNothing());
            }
            case "weekly" -> {
                if (StringUtils.hasText(task.getWeeklyDays())) {
                    // Quartz cron 表达式：秒 分 时 日 月 周
                    // 当指定周时，日应该用 ? 代替，避免日和周冲突
                    String cron = String.format("0 %d %d ? * %s", 
                            task.getDailyMinute() != null ? task.getDailyMinute() : 0,
                            task.getDailyHour() != null ? task.getDailyHour() : 0,
                            task.getWeeklyDays());
                    triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron)
                            .withMisfireHandlingInstructionDoNothing());
                }
            }
            case "monthly" -> {
                if (task.getMonthlyDay() != null) {
                    // Quartz cron 表达式：秒 分 时 日 月 周
                    String cron = String.format("0 %d %d %d * ?", 
                            task.getDailyMinute() != null ? task.getDailyMinute() : 0,
                            task.getDailyHour() != null ? task.getDailyHour() : 0,
                            task.getMonthlyDay());
                    triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron)
                            .withMisfireHandlingInstructionDoNothing());
                }
            }
            default -> {
                throw new IllegalArgumentException("不支持的触发器类型: " + triggerType);
            }
        }

        return triggerBuilder.build();
    }

    private JobKey getJobKey(Long taskId) {
        return JobKey.jobKey("scheduledTask_" + taskId, "scheduledTaskGroup");
    }

    private TriggerKey getTriggerKey(Long taskId) {
        return TriggerKey.triggerKey("scheduledTaskTrigger_" + taskId, "scheduledTaskTriggerGroup");
    }

    private void updateNextTriggerTime(Long taskId) {
        try {
            ScheduledTestTask task = scheduledTaskMapper.selectByPrimaryKey(taskId);
            if (task == null) return;
            
            JobKey jobKey = getJobKey(taskId);
            if (getScheduler().checkExists(jobKey)) {
                List<? extends Trigger> triggers = getScheduler().getTriggersOfJob(jobKey);
                if (!triggers.isEmpty()) {
                    Date nextFireTime = triggers.get(0).getNextFireTime();
                    if (nextFireTime != null) {
                        LocalDateTime nextTime = nextFireTime.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();
                        scheduledTaskMapper.updateNextTriggerTime(taskId, nextTime);
                    }
                }
            }
        } catch (SchedulerException e) {
            log.error("更新下次触发时间失败", e);
        }
    }

    // ==================== 转换方法 ====================

    private ScheduledTaskDTO convertToDTO(ScheduledTestTask task) {
        return ScheduledTaskDTO.builder()
                .taskId(task.getTaskId())
                .taskName(task.getTaskName())
                .description(task.getDescription())
                .taskType(task.getTaskType())
                .targetId(task.getTargetId())
                .targetName(task.getTargetName())
                .triggerType(task.getTriggerType())
                .cronExpression(task.getCronExpression())
                .simpleRepeatInterval(task.getSimpleRepeatInterval())
                .simpleRepeatCount(task.getSimpleRepeatCount())
                .dailyHour(task.getDailyHour())
                .dailyMinute(task.getDailyMinute())
                .weeklyDays(task.getWeeklyDays())
                .monthlyDay(task.getMonthlyDay())
                .executionEnvironment(task.getExecutionEnvironment())
                .baseUrl(task.getBaseUrl())
                .timeoutSeconds(task.getTimeoutSeconds())
                .concurrency(task.getConcurrency())
                .executionStrategy(task.getExecutionStrategy())
                .retryEnabled(task.getRetryEnabled())
                .maxRetryAttempts(task.getMaxRetryAttempts())
                .retryDelayMs(task.getRetryDelayMs())
                .notifyOnSuccess(task.getNotifyOnSuccess())
                .notifyOnFailure(task.getNotifyOnFailure())
                .notificationRecipients(task.getNotificationRecipients())
                .skipIfPreviousFailed(task.getSkipIfPreviousFailed())
                .maxDurationSeconds(task.getMaxDurationSeconds())
                .isEnabled(task.getIsEnabled())
                .totalExecutions(task.getTotalExecutions())
                .successfulExecutions(task.getSuccessfulExecutions())
                .failedExecutions(task.getFailedExecutions())
                .skippedExecutions(task.getSkippedExecutions())
                .successRate(task.getSuccessRate())
                .nextTriggerTime(task.getNextTriggerTime())
                .lastExecutionTime(task.getLastExecutionTime())
                .lastExecutionStatus(task.getLastExecutionStatus())
                .createdAt(task.getCreatedAt())
                .build();
    }

    private ScheduledTaskExecutionDTO convertExecutionToDTO(ScheduledTaskExecution execution) {
        return ScheduledTaskExecutionDTO.builder()
                .executionId(execution.getExecutionId())
                .taskId(execution.getTaskId())
                .testExecutionRecordId(execution.getTestExecutionRecordId())
                .quartzJobId(execution.getQuartzJobId())
                .status(execution.getStatus())
                .scheduledTime(execution.getScheduledTime())
                .actualStartTime(execution.getActualStartTime())
                .actualEndTime(execution.getActualEndTime())
                .durationSeconds(execution.getDurationSeconds())
                .delaySeconds(execution.getDelaySeconds())
                .totalCases(execution.getTotalCases())
                .passedCases(execution.getPassedCases())
                .failedCases(execution.getFailedCases())
                .skippedCases(execution.getSkippedCases())
                .successRate(execution.getSuccessRate())
                .errorMessage(execution.getErrorMessage())
                .retryCount(execution.getRetryCount())
                .isRetry(execution.getIsRetry())
                .notificationSent(execution.getNotificationSent())
                .notificationChannels(execution.getNotificationChannels())
                .createdAt(execution.getCreatedAt())
                .build();
    }
}
