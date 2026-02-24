package com.victor.iatms.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.victor.iatms.service.ScheduledTaskService;

/**
 * 定时测试任务Job执行器
 * 由Quartz调度器在指定时间触发执行
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTestTaskJob implements Job {

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            // 从JobDetail中获取任务ID和用户ID
            Long taskId = context.getJobDetail().getJobDataMap().getLong("taskId");
            Integer userId = context.getJobDetail().getJobDataMap().getInt("userId");

            log.info("开始执行定时任务: taskId={}, userId={}", taskId, userId);

            // 调用服务层执行任务
            scheduledTaskService.executeScheduledTask(taskId, userId, true);

            log.info("定时任务执行完成: taskId={}", taskId);

        } catch (Exception e) {
            log.error("定时任务执行失败", e);
            throw new JobExecutionException(e);
        }
    }
}

