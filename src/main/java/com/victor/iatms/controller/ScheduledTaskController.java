package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.dto.*;
import com.victor.iatms.entity.vo.PaginationResultVO;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.ScheduledTaskService;
import com.victor.iatms.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 定时测试任务控制器
 */
@Slf4j
@RestController
@RequestMapping("/scheduled-tasks")
@Validated
@RequiredArgsConstructor
public class ScheduledTaskController {

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 创建定时任务
     */
    @PostMapping
    @GlobalInterceptor(checkLogin = true, checkPermission = {"task:create"})
    public ResponseVO<ScheduledTaskDTO> createScheduledTask(
            @Valid @RequestBody CreateScheduledTaskDTO dto,
            HttpServletRequest request) {
        try {
            Integer userId = getCurrentUserId(request);
            ScheduledTaskDTO result = scheduledTaskService.createScheduledTask(dto, userId);
            return ResponseVO.success("定时任务创建成功", result);
        } catch (Exception e) {
            log.error("创建定时任务失败", e);
            return ResponseVO.serverError("创建定时任务失败: " + e.getMessage());
        }
    }

    /**
     * 更新定时任务
     */
    @PutMapping("/{taskId}")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"task:update"})
    public ResponseVO<ScheduledTaskDTO> updateScheduledTask(
            @PathVariable("taskId") Long taskId,
            @Valid @RequestBody CreateScheduledTaskDTO dto,
            HttpServletRequest request) {
        try {
            Integer userId = getCurrentUserId(request);
            ScheduledTaskDTO result = scheduledTaskService.updateScheduledTask(taskId, dto, userId);
            return ResponseVO.success("定时任务更新成功", result);
        } catch (Exception e) {
            log.error("更新定时任务失败: taskId={}", taskId, e);
            return ResponseVO.serverError("更新定时任务失败: " + e.getMessage());
        }
    }

    /**
     * 删除定时任务
     */
    @DeleteMapping("/{taskId}")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"task:delete"})
    public ResponseVO<Void> deleteScheduledTask(
            @PathVariable("taskId") Long taskId,
            HttpServletRequest request) {
        try {
            Integer userId = getCurrentUserId(request);
            scheduledTaskService.deleteScheduledTask(taskId, userId);
            return ResponseVO.success("定时任务删除成功", null);
        } catch (Exception e) {
            log.error("删除定时任务失败: taskId={}", taskId, e);
            return ResponseVO.serverError("删除定时任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"task:view"})
    public ResponseVO<ScheduledTaskDTO> getScheduledTask(
            @PathVariable("taskId") Long taskId,
            HttpServletRequest request) {
        try {
            Integer userId = getCurrentUserId(request);
            ScheduledTaskDTO result = scheduledTaskService.getScheduledTask(taskId, userId);
            return ResponseVO.success("获取任务详情成功", result);
        } catch (Exception e) {
            log.error("获取任务详情失败: taskId={}", taskId, e);
            return ResponseVO.serverError("获取任务详情失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询任务列表
     */
    @GetMapping
    @GlobalInterceptor(checkLogin = true, checkPermission = {"task:view"})
    public ResponseVO<PaginationResultVO<ScheduledTaskDTO>> listScheduledTasks(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "page_size", required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(value = "task_name", required = false) String taskName,
            @RequestParam(value = "task_type", required = false) String taskType,
            @RequestParam(value = "target_id", required = false) Integer targetId,
            @RequestParam(value = "trigger_type", required = false) String triggerType,
            @RequestParam(value = "is_enabled", required = false) Boolean isEnabled,
            @RequestParam(value = "execution_environment", required = false) String executionEnvironment,
            HttpServletRequest request) {
        try {
            Integer userId = getCurrentUserId(request);

            ScheduledTaskQueryDTO query = new ScheduledTaskQueryDTO();
            query.setPage(page);
            query.setPageSize(pageSize);
            query.setTaskName(taskName);
            query.setTaskType(taskType);
            query.setTargetId(targetId);
            query.setTriggerType(triggerType);
            query.setIsEnabled(isEnabled);
            query.setExecutionEnvironment(executionEnvironment);
            query.setCreatedBy(userId);

            PaginationResultVO<ScheduledTaskDTO> result = scheduledTaskService.listScheduledTasks(query, userId);
            return ResponseVO.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询任务列表失败", e);
            return ResponseVO.serverError("查询任务列表失败: " + e.getMessage());
        }
    }

    /**
     * 启用任务
     */
    @PostMapping("/{taskId}/enable")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"task:update"})
    public ResponseVO<Void> enableScheduledTask(
            @PathVariable("taskId") Long taskId,
            HttpServletRequest request) {
        try {
            Integer userId = getCurrentUserId(request);
            scheduledTaskService.enableScheduledTask(taskId, userId);
            return ResponseVO.success("任务已启用", null);
        } catch (Exception e) {
            log.error("启用任务失败: taskId={}", taskId, e);
            return ResponseVO.serverError("启用任务失败: " + e.getMessage());
        }
    }

    /**
     * 禁用任务
     */
    @PostMapping("/{taskId}/disable")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"task:update"})
    public ResponseVO<Void> disableScheduledTask(
            @PathVariable("taskId") Long taskId,
            HttpServletRequest request) {
        try {
            Integer userId = getCurrentUserId(request);
            scheduledTaskService.disableScheduledTask(taskId, userId);
            return ResponseVO.success("任务已禁用", null);
        } catch (Exception e) {
            log.error("禁用任务失败: taskId={}", taskId, e);
            return ResponseVO.serverError("禁用任务失败: " + e.getMessage());
        }
    }

    /**
     * 立即执行任务
     */
    @PostMapping("/{taskId}/execute")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"task:execute"})
    public ResponseVO<Long> executeScheduledTaskNow(
            @PathVariable("taskId") Long taskId,
            HttpServletRequest request) {
        try {
            Integer userId = getCurrentUserId(request);
            Long executionId = scheduledTaskService.executeScheduledTaskNow(taskId, userId);
            return ResponseVO.success("任务已提交执行", executionId);
        } catch (Exception e) {
            log.error("执行任务失败: taskId={}", taskId, e);
            return ResponseVO.serverError("执行任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取执行历史
     */
    @GetMapping("/{taskId}/history")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"task:view"})
    public ResponseVO<PaginationResultVO<ScheduledTaskExecutionDTO>> getExecutionHistory(
            @PathVariable("taskId") Long taskId,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "page_size", required = false, defaultValue = "20") Integer pageSize,
            HttpServletRequest request) {
        try {
            PaginationResultVO<ScheduledTaskExecutionDTO> result =
                    scheduledTaskService.getExecutionHistory(taskId, page, pageSize);
            return ResponseVO.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询执行历史失败: taskId={}", taskId, e);
            return ResponseVO.serverError("查询执行历史失败: " + e.getMessage());
        }
    }

    /**
     * 获取执行统计
     */
    @GetMapping("/{taskId}/statistics")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"task:view"})
    public ResponseVO<ScheduledTaskStatisticsDTO> getExecutionStatistics(
            @PathVariable("taskId") Long taskId,
            HttpServletRequest request) {
        try {
            ScheduledTaskStatisticsDTO result = scheduledTaskService.getExecutionStatistics(taskId);
            return ResponseVO.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询执行统计失败: taskId={}", taskId, e);
            return ResponseVO.serverError("查询执行统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取执行记录详情
     */
    @GetMapping("/executions/{executionId}")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"task:view"})
    public ResponseVO<ScheduledTaskExecutionDTO> getExecutionDetail(
            @PathVariable("executionId") Long executionId,
            HttpServletRequest request) {
        try {
            ScheduledTaskExecutionDTO result = scheduledTaskService.getExecutionDetail(executionId);
            return ResponseVO.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询执行记录详情失败: executionId={}", executionId, e);
            return ResponseVO.serverError("查询执行记录详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户ID
     */
    private Integer getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("认证失败，请重新登录");
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtUtils.validateToken(token)) {
                throw new RuntimeException("认证失败，请重新登录");
            }

            Integer userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                throw new RuntimeException("认证失败，请重新登录");
            }

            return userId;
        } catch (Exception e) {
            throw new RuntimeException("认证失败，请重新登录");
        }
    }
}

