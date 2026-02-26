package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.dto.PendingTaskDTO;
import com.victor.iatms.entity.po.Task;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务控制器
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * 获取当前用户的待处理任务列表
     * @return 待处理任务列表
     */
    @GetMapping("/pending")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<List<PendingTaskDTO>> getUserPendingTasks() {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer currentUserId = 1; // 临时硬编码，实际应该从认证上下文获取
            
            List<PendingTaskDTO> tasks = taskService.getUserPendingTasks(currentUserId);
            return ResponseVO.success("获取待处理任务成功", tasks);
        } catch (Exception e) {
            return ResponseVO.serverError("获取待处理任务失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有待处理任务（管理员用）
     * @return 待处理任务列表
     */
    @GetMapping("/pending/all")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<List<PendingTaskDTO>> getAllPendingTasks() {
        try {
            List<PendingTaskDTO> tasks = taskService.getAllPendingTasks();
            return ResponseVO.success("获取所有待处理任务成功", tasks);
        } catch (Exception e) {
            return ResponseVO.serverError("获取待处理任务失败：" + e.getMessage());
        }
    }

    /**
     * 根据项目ID获取任务列表
     * @param projectId 项目ID
     * @return 任务列表
     */
    @GetMapping("/project/{projectId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<List<Task>> getTasksByProjectId(@PathVariable("projectId") Integer projectId) {
        try {
            List<Task> tasks = taskService.getTasksByProjectId(projectId);
            return ResponseVO.success("获取项目任务列表成功", tasks);
        } catch (Exception e) {
            return ResponseVO.serverError("获取项目任务列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取任务详情
     * @param taskId 任务ID
     * @return 任务详情
     */
    @GetMapping("/{taskId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Task> getTaskById(@PathVariable("taskId") Long taskId) {
        try {
            Task task = taskService.getTaskById(taskId);
            if (task == null) {
                return ResponseVO.notFound("任务不存在");
            }
            return ResponseVO.success("获取任务详情成功", task);
        } catch (Exception e) {
            return ResponseVO.serverError("获取任务详情失败：" + e.getMessage());
        }
    }

    /**
     * 创建任务
     * @param task 任务信息
     * @return 创建结果
     */
    @PostMapping
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Map<String, Long>> createTask(@RequestBody Task task) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer currentUserId = 1; // 临时硬编码，实际应该从认证上下文获取
            
            Long taskId = taskService.createTask(task, currentUserId);
            
            Map<String, Long> result = new HashMap<>();
            result.put("taskId", taskId);
            return ResponseVO.success("创建任务成功", result);
        } catch (Exception e) {
            return ResponseVO.serverError("创建任务失败：" + e.getMessage());
        }
    }

    /**
     * 更新任务
     * @param taskId 任务ID
     * @param task 任务信息
     * @return 更新结果
     */
    @PutMapping("/{taskId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Void> updateTask(
            @PathVariable("taskId") Long taskId,
            @RequestBody Task task) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer currentUserId = 1; // 临时硬编码，实际应该从认证上下文获取
            
            task.setTaskId(taskId);
            boolean success = taskService.updateTask(task, currentUserId);
            
            if (success) {
                return ResponseVO.success("更新任务成功", null);
            } else {
                return ResponseVO.businessError("更新任务失败");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("更新任务失败：" + e.getMessage());
        }
    }

    /**
     * 删除任务
     * @param taskId 任务ID
     * @return 删除结果
     */
    @DeleteMapping("/{taskId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Void> deleteTask(@PathVariable("taskId") Long taskId) {
        try {
            boolean success = taskService.deleteTask(taskId);
            
            if (success) {
                return ResponseVO.success("删除任务成功", null);
            } else {
                return ResponseVO.businessError("删除任务失败");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("删除任务失败：" + e.getMessage());
        }
    }

    /**
     * 更新任务状态
     * @param taskId 任务ID
     * @param status 新状态
     * @return 更新结果
     */
    @PatchMapping("/{taskId}/status")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Void> updateTaskStatus(
            @PathVariable("taskId") Long taskId,
            @RequestParam("status") String status) {
        try {
            boolean success = taskService.updateTaskStatus(taskId, status);
            
            if (success) {
                return ResponseVO.success("更新任务状态成功", null);
            } else {
                return ResponseVO.businessError("更新任务状态失败");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("更新任务状态失败：" + e.getMessage());
        }
    }

    /**
     * 更新任务进度
     * @param taskId 任务ID
     * @param progress 进度
     * @return 更新结果
     */
    @PatchMapping("/{taskId}/progress")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Void> updateTaskProgress(
            @PathVariable("taskId") Long taskId,
            @RequestParam("progress") Integer progress) {
        try {
            boolean success = taskService.updateTaskProgress(taskId, progress);
            
            if (success) {
                return ResponseVO.success("更新任务进度成功", null);
            } else {
                return ResponseVO.businessError("更新任务进度失败");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("更新任务进度失败：" + e.getMessage());
        }
    }

    /**
     * 根据测试失败自动创建任务
     * @param executionId 执行记录ID
     * @param failureMessage 失败消息
     * @param projectId 项目ID
     * @param projectName 项目名称
     * @return 创建结果
     */
    @PostMapping("/from-failure")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Map<String, Long>> createTaskFromFailure(
            @RequestParam("executionId") Long executionId,
            @RequestParam(value = "failureMessage", required = false) String failureMessage,
            @RequestParam(value = "projectId", required = false) Integer projectId,
            @RequestParam(value = "projectName", required = false) String projectName) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer currentUserId = 1; // 临时硬编码
            
            Long taskId = taskService.createTaskFromFailure(
                    executionId, 
                    failureMessage, 
                    currentUserId, 
                    "系统管理员", // 暂时使用固定用户名
                    projectId, 
                    projectName
            );
            
            Map<String, Long> result = new HashMap<>();
            result.put("taskId", taskId);
            return ResponseVO.success("创建任务成功", result);
        } catch (Exception e) {
            return ResponseVO.serverError("创建任务失败：" + e.getMessage());
        }
    }
}

