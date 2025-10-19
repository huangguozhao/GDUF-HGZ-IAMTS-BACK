package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.dto.ExecuteTestCaseDTO;
import com.victor.iatms.entity.dto.ExecuteModuleDTO;
import com.victor.iatms.entity.dto.ExecuteProjectDTO;
import com.victor.iatms.entity.dto.ExecuteApiDTO;
import com.victor.iatms.entity.dto.ExecuteTestSuiteDTO;
import com.victor.iatms.entity.dto.ExecutionResultDTO;
import com.victor.iatms.entity.dto.ModuleExecutionResultDTO;
import com.victor.iatms.entity.dto.ProjectExecutionResultDTO;
import com.victor.iatms.entity.dto.ApiExecutionResultDTO;
import com.victor.iatms.entity.dto.TestSuiteExecutionResultDTO;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.TestExecutionService;
import com.victor.iatms.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 测试执行控制器
 */
@RestController
@RequestMapping("/api")
@Validated
public class TestExecutionController {

    @Autowired
    private TestExecutionService testExecutionService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 执行单个测试用例
     */
    @PostMapping("/test-cases/{case_id}/execute")
//    @GlobalInterceptor(
//        checkLogin = true,
//        checkPermission = {"testcase:execute"},
//        checkResourceAccess = true,
//        resourceType = "testcase",
//        resourceIdParam = "case_id"
//    )
    public ResponseVO<ExecutionResultDTO> executeTestCase(
            @PathVariable("case_id") Integer caseId,
            @RequestBody ExecuteTestCaseDTO executeDTO,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            ExecutionResultDTO result = testExecutionService.executeTestCase(caseId, executeDTO, userId);

            return ResponseVO.success("用例执行完成", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("测试用例不存在或未启用".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("关联的接口不存在或已禁用".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("执行测试用例失败".equals(errorMsg)) {
                return ResponseVO.serverError("执行测试用例失败，请稍后重试");
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 异步执行测试用例
     */
    @PostMapping("/test-cases/{case_id}/execute-async")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:execute"},
        checkResourceAccess = true,
        resourceType = "testcase",
        resourceIdParam = "case_id"
    )
    public ResponseVO<ExecutionResultDTO> executeTestCaseAsync(
            @PathVariable("case_id") Integer caseId,
            @RequestBody ExecuteTestCaseDTO executeDTO,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 设置异步执行
            executeDTO.setAsync(true);

            // 调用服务层方法
            ExecutionResultDTO result = testExecutionService.executeTestCaseAsync(caseId, executeDTO, userId);

            return ResponseVO.success("用例执行任务已提交", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("测试用例不存在或未启用".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("关联的接口不存在或已禁用".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("创建异步任务失败".equals(errorMsg)) {
                return ResponseVO.serverError("创建异步任务失败，请稍后重试");
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 查询任务状态
     */
    @GetMapping("/tasks/{task_id}/status")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:view"}
    )
    public ResponseVO<ExecutionResultDTO> getTaskStatus(
            @PathVariable("task_id") String taskId,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            ExecutionResultDTO result = testExecutionService.getTaskStatus(taskId, userId);

            return ResponseVO.success("获取任务状态成功", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("任务不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 取消任务执行
     */
    @PostMapping("/tasks/{task_id}/cancel")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:execute"}
    )
    public ResponseVO<Boolean> cancelTask(
            @PathVariable("task_id") String taskId,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            boolean result = testExecutionService.cancelTask(taskId, userId);

            if (result) {
                return ResponseVO.success("任务取消成功", true);
            } else {
                return ResponseVO.paramError("任务取消失败");
            }

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("任务不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 获取执行结果详情
     */
    @GetMapping("/test-results/{execution_id}")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:view"}
    )
    public ResponseVO<ExecutionResultDTO> getExecutionResult(
            @PathVariable("execution_id") Long executionId,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            ExecutionResultDTO result = testExecutionService.getExecutionResult(executionId, userId);

            return ResponseVO.success("获取执行结果成功", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("执行记录不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 获取执行日志
     */
    @GetMapping("/test-results/{execution_id}/logs")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:view"}
    )
    public ResponseVO<String> getExecutionLogs(
            @PathVariable("execution_id") Long executionId,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            String logs = testExecutionService.getExecutionLogs(executionId, userId);

            return ResponseVO.success("获取执行日志成功", logs);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("执行记录不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 生成测试报告
     */
    @PostMapping("/test-results/{execution_id}/report")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:view"}
    )
    public ResponseVO<Long> generateTestReport(
            @PathVariable("execution_id") Long executionId,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            Long reportId = testExecutionService.generateTestReport(executionId, userId);

            return ResponseVO.success("测试报告生成成功", reportId);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("执行记录不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("生成测试报告失败".equals(errorMsg)) {
                return ResponseVO.serverError("生成测试报告失败，请稍后重试");
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    // ========== 模块执行相关接口 ==========

    /**
     * 执行模块测试（同步）
     */
    @PostMapping("/modules/{module_id}/execute")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"module:execute"},
        checkResourceAccess = true,
        resourceType = "module",
        resourceIdParam = "module_id"
    )
    public ResponseVO<ModuleExecutionResultDTO> executeModule(
            @PathVariable("module_id") Integer moduleId,
            @RequestBody ExecuteModuleDTO executeDTO,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            ModuleExecutionResultDTO result = testExecutionService.executeModule(moduleId, executeDTO, userId);

            return ResponseVO.success("模块测试执行完成", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("模块不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("模块已禁用，无法执行测试".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("该模块下没有可执行的测试用例".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("并发数不能超过50".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else {
                return ResponseVO.serverError("执行模块测试失败，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 异步执行模块测试
     */
    @PostMapping("/modules/{module_id}/execute-async")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"module:execute"},
        checkResourceAccess = true,
        resourceType = "module",
        resourceIdParam = "module_id"
    )
    public ResponseVO<ModuleExecutionResultDTO> executeModuleAsync(
            @PathVariable("module_id") Integer moduleId,
            @RequestBody ExecuteModuleDTO executeDTO,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 设置异步执行
            executeDTO.setAsync(true);

            // 调用服务层方法
            ModuleExecutionResultDTO result = testExecutionService.executeModuleAsync(moduleId, executeDTO, userId);

            return ResponseVO.success("模块测试执行任务已提交", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("模块不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("模块已禁用，无法执行测试".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("该模块下没有可执行的测试用例".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("并发数不能超过50".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else {
                return ResponseVO.serverError("创建异步任务失败，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 查询模块任务状态
     */
    @GetMapping("/module-tasks/{task_id}/status")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"module:view"}
    )
    public ResponseVO<ModuleExecutionResultDTO> getModuleTaskStatus(
            @PathVariable("task_id") String taskId,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            ModuleExecutionResultDTO result = testExecutionService.getModuleTaskStatus(taskId, userId);

            return ResponseVO.success("获取任务状态成功", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("任务不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 取消模块任务执行
     */
    @PostMapping("/module-tasks/{task_id}/cancel")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"module:execute"}
    )
    public ResponseVO<Boolean> cancelModuleTask(
            @PathVariable("task_id") String taskId,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            boolean result = testExecutionService.cancelModuleTask(taskId, userId);

            if (result) {
                return ResponseVO.success("任务取消成功", true);
            } else {
                return ResponseVO.paramError("任务取消失败");
            }

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("任务不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    // ========== 项目执行相关接口 ==========

    /**
     * 执行项目测试
     */
    @PostMapping("/projects/{project_id}/execute")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"project:execute"},
        checkResourceAccess = true,
        resourceType = "project",
        resourceIdParam = "project_id"
    )
    public ResponseVO<ProjectExecutionResultDTO> executeProject(
            @PathVariable("project_id") Integer projectId,
            @RequestBody ExecuteProjectDTO executeDTO,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            ProjectExecutionResultDTO result = testExecutionService.executeProject(projectId, executeDTO, userId);

            return ResponseVO.success("项目测试执行完成", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("项目不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("项目已禁用，无法执行测试".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("该项目下没有可执行的测试用例".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("并发数不能超过50".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else {
                return ResponseVO.serverError("执行项目测试失败，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 异步执行项目测试
     */
    @PostMapping("/projects/{project_id}/execute-async")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"project:execute"},
        checkResourceAccess = true,
        resourceType = "project",
        resourceIdParam = "project_id"
    )
    public ResponseVO<ProjectExecutionResultDTO> executeProjectAsync(
            @PathVariable("project_id") Integer projectId,
            @RequestBody ExecuteProjectDTO executeDTO,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 设置异步执行
            executeDTO.setAsync(true);

            // 调用服务层方法
            ProjectExecutionResultDTO result = testExecutionService.executeProjectAsync(projectId, executeDTO, userId);

            return ResponseVO.success("项目测试执行任务已提交", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("项目不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("项目已禁用，无法执行测试".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("该项目下没有可执行的测试用例".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("并发数不能超过50".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else {
                return ResponseVO.serverError("创建异步任务失败，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 查询项目任务状态
     */
    @GetMapping("/project-tasks/{task_id}/status")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"project:view"}
    )
    public ResponseVO<ProjectExecutionResultDTO> getProjectTaskStatus(
            @PathVariable("task_id") String taskId,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            ProjectExecutionResultDTO result = testExecutionService.getProjectTaskStatus(taskId, userId);

            return ResponseVO.success("获取任务状态成功", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("任务不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 取消项目任务执行
     */
    @PostMapping("/project-tasks/{task_id}/cancel")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"project:execute"}
    )
    public ResponseVO<Boolean> cancelProjectTask(
            @PathVariable("task_id") String taskId,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            boolean result = testExecutionService.cancelProjectTask(taskId, userId);

            if (result) {
                return ResponseVO.success("任务取消成功", true);
            } else {
                return ResponseVO.paramError("任务取消失败");
            }

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("任务不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    // ========== 接口执行相关接口 ==========

    /**
     * 执行接口测试
     */
    @PostMapping("/apis/{api_id}/execute")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"api:execute"},
        checkResourceAccess = true,
        resourceType = "api",
        resourceIdParam = "api_id"
    )
    public ResponseVO<ApiExecutionResultDTO> executeApi(
            @PathVariable("api_id") Integer apiId,
            @RequestBody ExecuteApiDTO executeDTO,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            ApiExecutionResultDTO result = testExecutionService.executeApi(apiId, executeDTO, userId);

            return ResponseVO.success("接口测试执行完成", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("接口不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("接口已禁用，无法执行测试".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("该接口下没有可执行的测试用例".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("并发数不能超过10".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else {
                return ResponseVO.serverError("执行接口测试失败，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 异步执行接口测试
     */
    @PostMapping("/apis/{api_id}/execute-async")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"api:execute"},
        checkResourceAccess = true,
        resourceType = "api",
        resourceIdParam = "api_id"
    )
    public ResponseVO<ApiExecutionResultDTO> executeApiAsync(
            @PathVariable("api_id") Integer apiId,
            @RequestBody ExecuteApiDTO executeDTO,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 设置异步执行
            executeDTO.setAsync(true);

            // 调用服务层方法
            ApiExecutionResultDTO result = testExecutionService.executeApiAsync(apiId, executeDTO, userId);

            return ResponseVO.success("接口测试执行任务已提交", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("接口不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("接口已禁用，无法执行测试".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("该接口下没有可执行的测试用例".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("并发数不能超过10".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else {
                return ResponseVO.serverError("创建异步任务失败，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 查询接口任务状态
     */
    @GetMapping("/api-tasks/{task_id}/status")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"api:view"}
    )
    public ResponseVO<ApiExecutionResultDTO> getApiTaskStatus(
            @PathVariable("task_id") String taskId,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            ApiExecutionResultDTO result = testExecutionService.getApiTaskStatus(taskId, userId);

            return ResponseVO.success("获取任务状态成功", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("任务不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 取消接口任务执行
     */
    @PostMapping("/api-tasks/{task_id}/cancel")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"api:execute"}
    )
    public ResponseVO<Boolean> cancelApiTask(
            @PathVariable("task_id") String taskId,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            boolean result = testExecutionService.cancelApiTask(taskId, userId);

            if (result) {
                return ResponseVO.success("任务取消成功", true);
            } else {
                return ResponseVO.paramError("任务取消失败");
            }

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("任务不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    // ========== 测试套件执行相关接口 ==========

    /**
     * 执行测试套件
     */
    @PostMapping("/test-suites/{suite_id}/execute")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"suite:execute"},
        checkResourceAccess = true,
        resourceType = "suite",
        resourceIdParam = "suite_id"
    )
    public ResponseVO<TestSuiteExecutionResultDTO> executeTestSuite(
            @PathVariable("suite_id") Integer suiteId,
            @RequestBody ExecuteTestSuiteDTO executeDTO,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            TestSuiteExecutionResultDTO result = testExecutionService.executeTestSuite(suiteId, executeDTO, userId);

            return ResponseVO.success("测试套件执行完成", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("测试套件不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("测试套件已禁用，无法执行".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("该测试套件下没有可执行的测试用例".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("并发数不能超过20".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("用例依赖关系分析失败，存在循环依赖".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else {
                return ResponseVO.serverError("执行测试套件失败，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 异步执行测试套件
     */
    @PostMapping("/test-suites/{suite_id}/execute-async")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"suite:execute"},
        checkResourceAccess = true,
        resourceType = "suite",
        resourceIdParam = "suite_id"
    )
    public ResponseVO<TestSuiteExecutionResultDTO> executeTestSuiteAsync(
            @PathVariable("suite_id") Integer suiteId,
            @RequestBody ExecuteTestSuiteDTO executeDTO,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 设置异步执行
            executeDTO.setAsync(true);

            // 调用服务层方法
            TestSuiteExecutionResultDTO result = testExecutionService.executeTestSuiteAsync(suiteId, executeDTO, userId);

            return ResponseVO.success("测试套件执行任务已提交", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("测试套件不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("测试套件已禁用，无法执行".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("该测试套件下没有可执行的测试用例".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("并发数不能超过20".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("用例依赖关系分析失败，存在循环依赖".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else {
                return ResponseVO.serverError("创建异步任务失败，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 查询测试套件任务状态
     */
    @GetMapping("/suite-tasks/{task_id}/status")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"suite:view"}
    )
    public ResponseVO<TestSuiteExecutionResultDTO> getTestSuiteTaskStatus(
            @PathVariable("task_id") String taskId,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            TestSuiteExecutionResultDTO result = testExecutionService.getTestSuiteTaskStatus(taskId, userId);

            return ResponseVO.success("获取任务状态成功", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("任务不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 取消测试套件任务执行
     */
    @PostMapping("/suite-tasks/{task_id}/cancel")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"suite:execute"}
    )
    public ResponseVO<Boolean> cancelTestSuiteTask(
            @PathVariable("task_id") String taskId,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            boolean result = testExecutionService.cancelTestSuiteTask(taskId, userId);

            if (result) {
                return ResponseVO.success("任务取消成功", true);
            } else {
                return ResponseVO.paramError("任务取消失败");
            }

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("任务不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    // ========== 测试结果查询相关接口 ==========

    /**
     * 获取测试结果详情
     */
    @GetMapping("/test-results/{result_id}")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:view"}
    )
    public ResponseVO<com.victor.iatms.entity.dto.TestResultDetailDTO> getTestResultDetail(
            @PathVariable("result_id") Long resultId,
            @RequestParam(value = "include_steps", required = false) Boolean includeSteps,
            @RequestParam(value = "include_assertions", required = false) Boolean includeAssertions,
            @RequestParam(value = "include_artifacts", required = false) Boolean includeArtifacts,
            @RequestParam(value = "include_environment", required = false) Boolean includeEnvironment,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            com.victor.iatms.entity.dto.TestResultDetailDTO detail = 
                testExecutionService.getTestResultDetail(
                    resultId, includeSteps, includeAssertions, 
                    includeArtifacts, includeEnvironment, userId);

            return ResponseVO.success("success", detail);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("测试结果不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("认证失败，请重新登录".equals(errorMsg)) {
                return ResponseVO.authError(errorMsg);
            } else if (errorMsg != null && errorMsg.contains("权限不足")) {
                return ResponseVO.forbidden(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 获取个人测试概况
     */
    @GetMapping("/dashboard/summary")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:view"}
    )
    public ResponseVO<com.victor.iatms.entity.dto.DashboardSummaryDTO> getDashboardSummary(
            @RequestParam(value = "time_range", required = false, defaultValue = "7d") String timeRange,
            @RequestParam(value = "include_recent_activity", required = false) Boolean includeRecentActivity,
            @RequestParam(value = "include_pending_tasks", required = false) Boolean includePendingTasks,
            @RequestParam(value = "include_quick_actions", required = false) Boolean includeQuickActions,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            com.victor.iatms.entity.dto.DashboardSummaryDTO dashboardSummary = 
                testExecutionService.getDashboardSummary(
                    timeRange, includeRecentActivity, includePendingTasks, 
                    includeQuickActions, userId);

            return ResponseVO.success("success", dashboardSummary);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("认证失败，请重新登录".equals(errorMsg)) {
                return ResponseVO.authError(errorMsg);
            } else if (errorMsg != null && errorMsg.contains("权限不足")) {
                return ResponseVO.forbidden(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 获取近七天测试执行情况
     */
    @GetMapping("/weekly-execution")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:view"}
    )
    public ResponseVO<com.victor.iatms.entity.dto.WeeklyExecutionDTO> getWeeklyExecution(
            @RequestParam(value = "project_id", required = false) Integer projectId,
            @RequestParam(value = "module_id", required = false) Integer moduleId,
            @RequestParam(value = "environment", required = false) String environment,
            @RequestParam(value = "include_daily_trend", required = false) Boolean includeDailyTrend,
            @RequestParam(value = "include_top_failures", required = false) Boolean includeTopFailures,
            @RequestParam(value = "include_performance", required = false) Boolean includePerformance,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            com.victor.iatms.entity.dto.WeeklyExecutionDTO weeklyExecution = 
                testExecutionService.getWeeklyExecution(
                    projectId, moduleId, environment, 
                    includeDailyTrend, includeTopFailures, includePerformance, userId);

            return ResponseVO.success("success", weeklyExecution);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("认证失败，请重新登录".equals(errorMsg)) {
                return ResponseVO.authError(errorMsg);
            } else if (errorMsg != null && errorMsg.contains("权限不足")) {
                return ResponseVO.forbidden(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 获取测试统计信息
     */
    @GetMapping("/test-results/statistics")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:view"}
    )
    public ResponseVO<com.victor.iatms.entity.dto.TestStatisticsDTO> getTestStatistics(
            @RequestParam(value = "time_range", required = false, defaultValue = "7d") String timeRange,
            @RequestParam(value = "start_time", required = false) String startTimeStr,
            @RequestParam(value = "end_time", required = false) String endTimeStr,
            @RequestParam(value = "project_id", required = false) Integer projectId,
            @RequestParam(value = "module_id", required = false) Integer moduleId,
            @RequestParam(value = "api_id", required = false) Integer apiId,
            @RequestParam(value = "environment", required = false) String environment,
            @RequestParam(value = "group_by", required = false, defaultValue = "day") String groupBy,
            @RequestParam(value = "include_trend", required = false) Boolean includeTrend,
            @RequestParam(value = "include_comparison", required = false) Boolean includeComparison,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 解析时间参数
            java.time.LocalDateTime startTime = null;
            java.time.LocalDateTime endTime = null;

            if (startTimeStr != null && !startTimeStr.isEmpty()) {
                try {
                    startTime = java.time.LocalDateTime.parse(startTimeStr, 
                        java.time.format.DateTimeFormatter.ISO_DATE_TIME);
                } catch (Exception e) {
                    return ResponseVO.paramError("开始时间格式错误，请使用ISO 8601格式");
                }
            }

            if (endTimeStr != null && !endTimeStr.isEmpty()) {
                try {
                    endTime = java.time.LocalDateTime.parse(endTimeStr, 
                        java.time.format.DateTimeFormatter.ISO_DATE_TIME);
                } catch (Exception e) {
                    return ResponseVO.paramError("结束时间格式错误，请使用ISO 8601格式");
                }
            }

            // 调用服务层方法
            com.victor.iatms.entity.dto.TestStatisticsDTO statistics = 
                testExecutionService.getTestStatistics(
                    timeRange, startTime, endTime, projectId, moduleId, apiId, 
                    environment, groupBy, includeTrend, includeComparison, userId);

            return ResponseVO.success("success", statistics);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && (errorMsg.contains("时间") || errorMsg.contains("参数"))) {
                return ResponseVO.paramError(errorMsg);
            } else if ("认证失败，请重新登录".equals(errorMsg)) {
                return ResponseVO.authError(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 分页获取测试结果列表
     */
    @GetMapping("/test-results")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:view"}
    )
    public ResponseVO<com.victor.iatms.entity.dto.TestResultPageResultDTO> getTestResults(
            @RequestParam(value = "task_type", required = false) String taskType,
            @RequestParam(value = "ref_id", required = false) Integer refId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "environment", required = false) String environment,
            @RequestParam(value = "priority", required = false) String priority,
            @RequestParam(value = "severity", required = false) String severity,
            @RequestParam(value = "start_time_begin", required = false) String startTimeBegin,
            @RequestParam(value = "start_time_end", required = false) String startTimeEnd,
            @RequestParam(value = "duration_min", required = false) Long durationMin,
            @RequestParam(value = "duration_max", required = false) Long durationMax,
            @RequestParam(value = "search_keyword", required = false) String searchKeyword,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort_order", required = false) String sortOrder,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "page_size", required = false, defaultValue = "20") Integer pageSize,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 构建查询参数
            com.victor.iatms.entity.query.TestResultQuery query = 
                new com.victor.iatms.entity.query.TestResultQuery();
            query.setTaskType(taskType);
            query.setRefId(refId);
            query.setStatus(status);
            query.setEnvironment(environment);
            query.setPriority(priority);
            query.setSeverity(severity);
            
            // 解析时间参数
            if (startTimeBegin != null && !startTimeBegin.isEmpty()) {
                try {
                    query.setStartTimeBegin(java.time.LocalDateTime.parse(startTimeBegin, 
                        java.time.format.DateTimeFormatter.ISO_DATE_TIME));
                } catch (Exception e) {
                    return ResponseVO.paramError("开始时间格式错误，请使用ISO 8601格式");
                }
            }
            if (startTimeEnd != null && !startTimeEnd.isEmpty()) {
                try {
                    query.setStartTimeEnd(java.time.LocalDateTime.parse(startTimeEnd, 
                        java.time.format.DateTimeFormatter.ISO_DATE_TIME));
                } catch (Exception e) {
                    return ResponseVO.paramError("结束时间格式错误，请使用ISO 8601格式");
                }
            }
            
            query.setDurationMin(durationMin);
            query.setDurationMax(durationMax);
            query.setSearchKeyword(searchKeyword);
            query.setSortBy(sortBy);
            query.setSortOrder(sortOrder);
            query.setPage(page);
            query.setPageSize(pageSize);

            // 调用服务层方法
            com.victor.iatms.entity.dto.TestResultPageResultDTO result = 
                testExecutionService.getTestResults(query, userId);

            return ResponseVO.success("success", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && (errorMsg.contains("无效的") || errorMsg.contains("格式错误") 
                    || errorMsg.contains("不能大于") || errorMsg.contains("不能晚于"))) {
                return ResponseVO.paramError(errorMsg);
            } else if ("认证失败，请重新登录".equals(errorMsg)) {
                return ResponseVO.authError(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
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
