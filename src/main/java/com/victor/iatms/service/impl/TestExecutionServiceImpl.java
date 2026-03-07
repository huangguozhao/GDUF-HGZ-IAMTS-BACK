package com.victor.iatms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.iatms.entity.constants.Constants;
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
import com.victor.iatms.entity.dto.TestCaseExecutionDTO;
import com.victor.iatms.entity.enums.ExecutionStatusEnum;
import com.victor.iatms.entity.enums.ModuleStatusEnum;
import com.victor.iatms.entity.enums.ReportStatusEnum;
import com.victor.iatms.entity.enums.ReportTypeEnum;
import com.victor.iatms.entity.enums.TaskExecutionStatusEnum;
import com.victor.iatms.entity.enums.TaskTypeEnum;
import com.victor.iatms.entity.po.Api;
import com.victor.iatms.entity.po.Module;
import com.victor.iatms.entity.po.Project;
import com.victor.iatms.entity.po.TestCase;
import com.victor.iatms.entity.po.TestCaseResult;
import com.victor.iatms.entity.po.TestReportSummary;
import com.victor.iatms.entity.po.TestSuite;
import com.victor.iatms.entity.po.TestExecutionRecord;
import com.victor.iatms.mappers.TestExecutionMapper;
import com.victor.iatms.mappers.TestExecutionRecordMapper;
import com.victor.iatms.mappers.LogMapper;
import com.victor.iatms.redis.RedisComponet;
import com.victor.iatms.service.TestExecutionService;
import com.victor.iatms.service.VariablePoolService;
import com.victor.iatms.service.DependencyResolverService;
import com.victor.iatms.utils.DateUtil;
import com.victor.iatms.utils.TestCaseExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试执行服务实现类
 */
@Slf4j
@Service
public class TestExecutionServiceImpl implements TestExecutionService {

    @Autowired
    private TestExecutionMapper testExecutionMapper;

    @Autowired
    private TestExecutionRecordMapper testExecutionRecordMapper;

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private TestCaseExecutor testCaseExecutor;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisComponet redisComponent;

    @Autowired
    private VariablePoolService variablePoolService;

    @Autowired
    private DependencyResolverService dependencyResolverService;

    // 异步任务存储（实际项目中应该使用Redis或数据库）
    private final Map<String, CompletableFuture<ExecutionResultDTO>> asyncTasks = new ConcurrentHashMap<>();
    
    // 模块任务状态缓存
    private final Map<String, ModuleExecutionResultDTO> moduleTaskStatusCache = new ConcurrentHashMap<>();
    
    // 项目任务状态缓存
    private final Map<String, ProjectExecutionResultDTO> projectTaskStatusCache = new ConcurrentHashMap<>();
    
    // 接口任务状态缓存
    private final Map<String, ApiExecutionResultDTO> apiTaskStatusCache = new ConcurrentHashMap<>();
    
    // 测试套件任务状态缓存
    private final Map<String, TestSuiteExecutionResultDTO> suiteTaskStatusCache = new ConcurrentHashMap<>();
    
    // 线程池用于并发执行
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExecutionResultDTO executeTestCase(Integer caseId, ExecuteTestCaseDTO executeDTO, Integer userId) {
        return executeTestCaseWithVariables(caseId, executeDTO, userId, null, null, null);
    }

    @Transactional(rollbackFor = Exception.class)
    public ExecutionResultDTO executeTestCaseWithVariables(Integer caseId, ExecuteTestCaseDTO executeDTO, 
                                                            Integer userId, String variablePoolId) {
        return executeTestCaseWithVariables(caseId, executeDTO, userId, variablePoolId, null, null);
    }

    @Transactional(rollbackFor = Exception.class)
    public ExecutionResultDTO executeTestCaseWithVariables(Integer caseId, ExecuteTestCaseDTO executeDTO, 
                                                            Integer userId, String variablePoolId,
                                                            Long sharedExecutionRecordId, Long sharedReportId) {
        TestExecutionRecord executionRecord = null;
        boolean isBatchExecution = sharedExecutionRecordId != null;
        
        try {
            TestCaseExecutionDTO executionDTO = testExecutionMapper.findTestCaseForExecution(caseId);
            if (executionDTO == null) {
                throw new RuntimeException("测试用例不存在或未启用");
            }

            if (!isBatchExecution) {
                executionRecord = createExecutionRecord("test_case", caseId, executionDTO.getName(), 
                    userId, executeDTO.getExecutionType(), executeDTO.getEnvironment());
                testExecutionRecordMapper.insertExecutionRecord(executionRecord);
            }

            setExecutionParameters(executionDTO, executeDTO);

            if (variablePoolId != null) {
                applyVariablesFromPool(executionDTO, variablePoolId);
            }

            TestCaseExecutionDTO result = testCaseExecutor.executeTestCase(executionDTO);

            Long executionId = generateExecutionId();

            Long recordIdToUse = isBatchExecution ? sharedExecutionRecordId : 
                (executionRecord != null ? executionRecord.getRecordId() : null);
            Long reportIdToUse = isBatchExecution ? sharedReportId : null;
            TestCaseResult testCaseResult = buildTestCaseResult(result, executionId, recordIdToUse, reportIdToUse, userId);
            testExecutionMapper.insertTestCaseResult(testCaseResult);

            Long reportId = sharedReportId;
            if (!isBatchExecution) {
                reportId = generateTestReport(executionId, userId);
            }

            if (!isBatchExecution && executionRecord != null) {
                updateExecutionRecordOnCompletion(executionRecord, result, reportId);
                testExecutionRecordMapper.updateExecutionRecord(executionRecord);
            }

            ExecutionResultDTO resultDTO = buildExecutionResult(result, executionId, reportId, 
                isBatchExecution ? null : executionRecord);
            
            if (result.getExtractedValues() != null) {
                resultDTO.setExtractedVariables(result.getExtractedValues());
            }
            
            return resultDTO;

        } catch (Exception e) {
            log.error("执行测试用例失败: {}", e.getMessage(), e);
            // 更新执行记录为失败
            if (executionRecord != null) {
                updateExecutionRecordOnFailure(executionRecord, e.getMessage());
                testExecutionRecordMapper.updateExecutionRecord(executionRecord);
            }
            
            // 构建失败的执行结果，而不是直接抛出异常
            ExecutionResultDTO failureResult = new ExecutionResultDTO();
            failureResult.setExecutionId(generateExecutionId());
            failureResult.setCaseId(caseId);
            failureResult.setCaseName("测试用例-" + caseId);
            failureResult.setStatus(ExecutionStatusEnum.FAILED.getCode());
            failureResult.setFailureMessage("执行失败: " + e.getMessage());
            failureResult.setFailureType("EXECUTION_ERROR");
            failureResult.setStartTime(LocalDateTime.now());
            failureResult.setEndTime(LocalDateTime.now());
            failureResult.setDuration(0L);
            failureResult.setLogsLink("/api/test-results/" + failureResult.getExecutionId() + "/logs");
            // 兜底：失败时至少有1个断言失败
            failureResult.setAssertionsPassed(0);
            failureResult.setAssertionsFailed(1);
            // 添加执行信息和执行范围
            failureResult.setExecutionScope("test_case");
            failureResult.setExecutionType(executeDTO != null ? executeDTO.getExecutionType() : "manual");
            failureResult.setEnvironment(executeDTO != null ? executeDTO.getEnvironment() : null);
            
            return failureResult;
        }
    }

    private void applyVariablesFromPool(TestCaseExecutionDTO executionDTO, String variablePoolId) {
        if (variablePoolId == null) {
            return;
        }
        
        Map<String, Object> variables = variablePoolService.getAllVariables(variablePoolId);
        if (variables == null || variables.isEmpty()) {
            log.debug("变量池为空: {}", variablePoolId);
            return;
        }
        
        log.info("应用共享变量: poolId={}, variables={}", variablePoolId, variables.keySet());
        
        // 应用变量到请求覆盖配置
        if (executionDTO.getRequestOverride() != null) {
            String applied = variablePoolService.applyVariables(variablePoolId, executionDTO.getRequestOverride());
            executionDTO.setRequestOverride(applied);
        }
        
        // 应用变量到接口信息中的请求体、请求头、请求参数
        if (executionDTO.getApiInfo() != null) {
            TestCaseExecutionDTO.ApiInfoDTO apiInfo = executionDTO.getApiInfo();
            
            if (apiInfo.getRequestBody() != null) {
                String applied = variablePoolService.applyVariables(variablePoolId, apiInfo.getRequestBody());
                apiInfo.setRequestBody(applied);
            }
            
            if (apiInfo.getRequestHeaders() != null) {
                String applied = variablePoolService.applyVariables(variablePoolId, apiInfo.getRequestHeaders());
                apiInfo.setRequestHeaders(applied);
            }
            
            if (apiInfo.getRequestParameters() != null) {
                String applied = variablePoolService.applyVariables(variablePoolId, apiInfo.getRequestParameters());
                apiInfo.setRequestParameters(applied);
            }
        }
    }

    @Override
    public ExecutionResultDTO executeTestCaseAsync(Integer caseId, ExecuteTestCaseDTO executeDTO, Integer userId) {
        try {
            // 1. 生成任务ID
            String taskId = generateTaskId();

            // 2. 创建异步任务
            CompletableFuture<ExecutionResultDTO> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return executeTestCase(caseId, executeDTO, userId);
                } catch (Exception e) {
                    log.error("异步执行测试用例失败: {}", e.getMessage(), e);
                    ExecutionResultDTO errorResult = new ExecutionResultDTO();
                    errorResult.setTaskId(taskId);
                    errorResult.setCaseId(caseId);
                    errorResult.setStatus(ExecutionStatusEnum.FAILED.getCode());
                    errorResult.setFailureMessage(e.getMessage());
                    return errorResult;
                }
            });

            // 3. 存储异步任务
            asyncTasks.put(taskId, future);

            // 4. 构建任务信息
            ExecutionResultDTO taskInfo = new ExecutionResultDTO();
            taskInfo.setTaskId(taskId);
            taskInfo.setCaseId(caseId);
            taskInfo.setStatus(ExecutionStatusEnum.PENDING.getCode());
            taskInfo.setEstimatedWaitTime(5);
            taskInfo.setQueuePosition(asyncTasks.size());
            taskInfo.setMonitorUrl("/api/tasks/" + taskId + "/status");

            return taskInfo;

        } catch (Exception e) {
            log.error("创建异步任务失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建异步任务失败: " + e.getMessage());
        }
    }

    @Override
    public ExecutionResultDTO getTaskStatus(String taskId, Integer userId) {
        CompletableFuture<ExecutionResultDTO> future = asyncTasks.get(taskId);
        if (future == null) {
            throw new RuntimeException("任务不存在");
        }

        if (future.isDone()) {
            try {
                ExecutionResultDTO result = future.get();
                // 任务完成后从缓存中移除
                asyncTasks.remove(taskId);
                return result;
            } catch (Exception e) {
                log.error("获取任务结果失败: {}", e.getMessage(), e);
                ExecutionResultDTO errorResult = new ExecutionResultDTO();
                errorResult.setTaskId(taskId);
                errorResult.setStatus(ExecutionStatusEnum.FAILED.getCode());
                errorResult.setFailureMessage("获取任务结果失败: " + e.getMessage());
                return errorResult;
            }
        } else {
            ExecutionResultDTO statusInfo = new ExecutionResultDTO();
            statusInfo.setTaskId(taskId);
            statusInfo.setStatus(ExecutionStatusEnum.RUNNING.getCode());
            statusInfo.setEstimatedWaitTime(5);
            return statusInfo;
        }
    }

    @Override
    public boolean cancelTask(String taskId, Integer userId) {
        CompletableFuture<ExecutionResultDTO> future = asyncTasks.get(taskId);
        if (future == null) {
            return false;
        }

        boolean cancelled = future.cancel(true);
        if (cancelled) {
            asyncTasks.remove(taskId);
        }
        return cancelled;
    }

    @Override
    public ExecutionResultDTO getExecutionResult(Long executionId, Integer userId) {
        TestCaseResult testCaseResult = testExecutionMapper.findTestCaseResultByExecutionId(executionId);
        if (testCaseResult == null) {
            throw new RuntimeException("执行记录不存在");
        }

        return buildExecutionResultFromTestCaseResult(testCaseResult);
    }

    @Override
    public String getExecutionLogs(Long executionId, Integer userId) {
        TestCaseResult testCaseResult = testExecutionMapper.findTestCaseResultByExecutionId(executionId);
        if (testCaseResult == null) {
            throw new RuntimeException("执行记录不存在");
        }

        // 这里可以从日志文件或数据库中获取详细日志
        return testCaseResult.getLogsLink();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateTestReport(Long executionId, Integer userId) {
        try {
            // 1. 查询测试结果
            TestCaseResult testCaseResult = testExecutionMapper.findTestCaseResultByExecutionId(executionId);
            if (testCaseResult == null) {
                throw new RuntimeException("执行记录不存在");
            }

            // 2. 创建报告汇总
            TestReportSummary reportSummary = buildTestReportSummary(testCaseResult, userId);
            testExecutionMapper.insertTestReportSummary(reportSummary);

            // 3. 更新测试结果的报告ID
            testCaseResult.setReportId(reportSummary.getReportId());
            // 注意：这里需要实现updateTestCaseResult方法，暂时注释掉
            // testExecutionMapper.updateTestCaseResult(testCaseResult);

            return reportSummary.getReportId();

        } catch (Exception e) {
            log.error("生成测试报告失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成测试报告失败: " + e.getMessage());
        }
    }

    /**
     * 设置执行参数
     */
    private void setExecutionParameters(TestCaseExecutionDTO executionDTO, ExecuteTestCaseDTO executeDTO) {
        if (executeDTO.getEnvironment() != null) {
            executionDTO.setEnvironment(executeDTO.getEnvironment());
        } else {
            executionDTO.setEnvironment(Constants.DEFAULT_ENVIRONMENT);
        }

        if (executeDTO.getVariables() != null) {
            executionDTO.setVariables(executeDTO.getVariables());
        } else {
            executionDTO.setVariables(new HashMap<>());
        }

        // 设置自定义BaseUrl（覆盖接口默认URL）
        if (executeDTO.getBaseUrl() != null && !executeDTO.getBaseUrl().trim().isEmpty()) {
            if (executionDTO.getApiInfo() != null) {
                executionDTO.getApiInfo().setBaseUrl(executeDTO.getBaseUrl());
            }
        }

        // 设置超时时间
        if (executeDTO.getTimeout() != null) {
            if (executionDTO.getApiInfo() != null) {
                executionDTO.getApiInfo().setTimeoutSeconds(executeDTO.getTimeout());
            }
        }

        // 设置执行时间
        executionDTO.setExecutionStartTime(LocalDateTime.now());
    }

    /**
     * 生成执行ID
     */
    private Long generateExecutionId() {
        return System.currentTimeMillis();
    }

    /**
     * 更新测试报告统计数据
     */
    private void updateReportSummaryStats(Long reportId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 关键修复：由于异步线程使用独立的数据库连接，主线程可能看不到异步线程插入的数据
            // 使用重试机制确保能够查询到数据
            Map<String, Object> stats = null;
            int maxRetries = 3;
            int retryDelayMs = 100;
            
            for (int retry = 0; retry < maxRetries; retry++) {
                if (retry > 0) {
                    try {
                        Thread.sleep(retryDelayMs * retry);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                stats = testExecutionMapper.countResultsByReportId(reportId);
                if (stats != null && stats.get("total") != null && ((Number) stats.get("total")).intValue() > 0) {
                    break;
                }
                log.warn("统计查询为空, 重试 {}/{}", retry + 1, maxRetries);
            }
            
            if (stats == null) {
                return;
            }
            
            TestReportSummary reportSummary = testExecutionMapper.findTestReportSummaryById(reportId);
            if (reportSummary == null) {
                return;
            }
            
            int total = stats.get("total") != null ? ((Number) stats.get("total")).intValue() : 0;
            int passed = stats.get("passed") != null ? ((Number) stats.get("passed")).intValue() : 0;
            int failed = stats.get("failed") != null ? ((Number) stats.get("failed")).intValue() : 0;
            int broken = stats.get("broken") != null ? ((Number) stats.get("broken")).intValue() : 0;
            int skipped = stats.get("skipped") != null ? ((Number) stats.get("skipped")).intValue() : 0;
            
            Long totalDuration = stats.get("totalDuration") != null ? ((Number) stats.get("totalDuration")).longValue() : 0L;
            Long avgDuration = stats.get("avgDuration") != null ? ((Number) stats.get("avgDuration")).longValue() : 0L;
            Long maxDuration = stats.get("maxDuration") != null ? ((Number) stats.get("maxDuration")).longValue() : 0L;
            Long minDuration = stats.get("minDuration") != null ? ((Number) stats.get("minDuration")).longValue() : 0L;
            
            BigDecimal successRate = total > 0 ? BigDecimal.valueOf((double) passed / total * 100) : BigDecimal.ZERO;
            
            reportSummary.setExecutedCases(total);
            reportSummary.setPassedCases(passed);
            reportSummary.setFailedCases(failed);
            reportSummary.setBrokenCases(broken);
            reportSummary.setSkippedCases(skipped);
            reportSummary.setSuccessRate(successRate);
            reportSummary.setTotalDuration(totalDuration);
            reportSummary.setAvgDuration(avgDuration);
            reportSummary.setMaxDuration(maxDuration);
            reportSummary.setMinDuration(minDuration > 0 ? minDuration : 0L);
            reportSummary.setStartTime(startTime);
            reportSummary.setEndTime(endTime);
            reportSummary.setDuration(java.time.Duration.between(startTime, endTime).toMillis());
            reportSummary.setReportStatus(failed > 0 || broken > 0 ? "failed" : "completed");
            reportSummary.setUpdatedAt(LocalDateTime.now());
            
            testExecutionMapper.updateTestReportSummary(reportSummary);
            
            log.info("更新测试报告统计: reportId={}, total={}, passed={}, failed={}, broken={}, skipped={}, successRate={}%", 
                    reportId, total, passed, failed, broken, skipped, successRate);
        } catch (Exception e) {
            log.error("更新测试报告统计失败: reportId={}, error={}", reportId, e.getMessage(), e);
        }
    }

    /**
     * 生成任务ID
     */
    private String generateTaskId() {
        return "task_" + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 验证并规范化 severity 值
     * 数据库只允许: blocker, critical, high, normal, minor, trivial
     */
    private String normalizeSeverity(String severity) {
        if (severity == null || severity.isEmpty()) {
            return "normal";
        }
        String lowerSeverity = severity.toLowerCase();
        if (lowerSeverity.matches("blocker|critical|high|normal|minor|trivial")) {
            return lowerSeverity;
        }
        return switch (lowerSeverity) {
            case "p0", "urgent", "highest" -> "blocker";
            case "p1" -> "critical";
            case "p2", "medium" -> "high";
            case "p3", "low" -> "minor";
            case "lowest" -> "trivial";
            default -> "normal";
        };
    }

    /**
     * 验证并规范化 priority 值
     * 数据库只允许: P0, P1, P2, P3
     */
    private String normalizePriority(String priority) {
        if (priority == null || priority.isEmpty()) {
            return "P2";
        }
        String upperPriority = priority.toUpperCase();
        if (upperPriority.matches("P[0-3]")) {
            return upperPriority;
        }
        // 尝试映射常见值
        return switch (upperPriority) {
            case "HIGHEST", "URGENT", "CRITICAL" -> "P0";
            case "HIGH" -> "P1";
            case "MEDIUM", "NORMAL" -> "P2";
            case "LOW", "LOWEST" -> "P3";
            default -> "P2";
        };
    }

    /**
     * 构建测试结果
     */
    private TestCaseResult buildTestCaseResult(TestCaseExecutionDTO executionDTO, Long executionId, 
                                                Long executionRecordId, Long reportId, Integer userId) {
        TestCaseResult result = new TestCaseResult();
        result.setExecutionRecordId(executionRecordId);
        result.setReportId(reportId);
        result.setExecutionId(executionId);
        result.setTaskType(TaskTypeEnum.TEST_CASE.getCode());
        result.setRefId(executionDTO.getCaseId());
        result.setFullName(executionDTO.getName());
        result.setStatus(executionDTO.getExecutionStatus());
        result.setDuration(executionDTO.getExecutionDuration());
        result.setStartTime(executionDTO.getExecutionStartTime());
        result.setEndTime(executionDTO.getExecutionEndTime());
        result.setFailureMessage(executionDTO.getFailureMessage());
        result.setFailureTrace(executionDTO.getFailureTrace());
        result.setFailureType(executionDTO.getFailureType());
        result.setErrorCode(executionDTO.getErrorCode());
        result.setEnvironment(executionDTO.getEnvironment());
        result.setSeverity(normalizeSeverity(executionDTO.getSeverity()));
        result.setPriority(normalizePriority(executionDTO.getPriority()));
        result.setRetryCount(executionDTO.getRetryCount());
        result.setFlaky(executionDTO.getFlaky());
        result.setCreatedAt(LocalDateTime.now());
        result.setIsDeleted(false);

        result.setCaseId(executionDTO.getCaseId());
        result.setCaseCode(executionDTO.getCaseCode());
        result.setCaseName(executionDTO.getName());
        
        // 从API信息中获取相关数据
        if (executionDTO.getApiInfo() != null) {
            result.setApiName(executionDTO.getApiInfo().getName());
        }
        
        // 设置测试层级和类型（根据实际业务逻辑设置默认值或从executionDTO获取）
        result.setTestLayer("API"); // 默认为API层级
        result.setTestType("POSITIVE"); // 默认为正向测试
        
        // 初始化不稳定用例相关字段
        result.setFlakyCount(0);
        result.setRetestResult("NOT_RETESTED");

        // 设置JSON字段
        try {
            if (executionDTO.getAssertionResults() != null) {
                result.setStepsJson(objectMapper.writeValueAsString(executionDTO.getAssertionResults()));
            }
            if (executionDTO.getVariables() != null) {
                result.setParametersJson(objectMapper.writeValueAsString(executionDTO.getVariables()));
            }
            if (executionDTO.getExtractedValues() != null) {
                result.setAttachmentsJson(objectMapper.writeValueAsString(executionDTO.getExtractedValues()));
            }
        } catch (Exception e) {
            log.warn("序列化JSON字段失败: {}", e.getMessage());
        }

        return result;
    }

    /**
     * 构建测试报告汇总
     */
    private TestReportSummary buildTestReportSummary(TestCaseResult testCaseResult, Integer userId) {
        TestReportSummary summary = new TestReportSummary();
        String reportName = testCaseResult.getFullName() != null ? 
            testCaseResult.getFullName() + "_" + formatTimestamp(LocalDateTime.now()) : 
            "测试用例报告_" + formatTimestamp(LocalDateTime.now());
        summary.setReportName(reportName);
        summary.setReportType(ReportTypeEnum.EXECUTION.getCode());
        summary.setExecutionId(testCaseResult.getExecutionId());
        
        // 通过测试用例ID查询所属项目ID
        Integer projectId = null;
        if (testCaseResult.getCaseId() != null) {
            projectId = testExecutionMapper.findProjectIdByCaseId(testCaseResult.getCaseId());
        }
        summary.setProjectId(projectId != null ? projectId : 1); // 如果查询不到，使用默认值
        
        summary.setEnvironment(testCaseResult.getEnvironment() != null ? testCaseResult.getEnvironment() : "test");
        summary.setStartTime(testCaseResult.getStartTime() != null ? testCaseResult.getStartTime() : LocalDateTime.now());
        summary.setEndTime(testCaseResult.getEndTime() != null ? testCaseResult.getEndTime() : LocalDateTime.now());
        summary.setDuration(testCaseResult.getDuration() != null ? testCaseResult.getDuration() : 0L);
        summary.setTotalCases(1);
        summary.setExecutedCases(1);
        summary.setPassedCases(ExecutionStatusEnum.PASSED.getCode().equals(testCaseResult.getStatus()) ? 1 : 0);
        summary.setFailedCases(ExecutionStatusEnum.FAILED.getCode().equals(testCaseResult.getStatus()) ? 1 : 0);
        summary.setBrokenCases(ExecutionStatusEnum.BROKEN.getCode().equals(testCaseResult.getStatus()) ? 1 : 0);
        summary.setSkippedCases(ExecutionStatusEnum.SKIPPED.getCode().equals(testCaseResult.getStatus()) ? 1 : 0);
        summary.setSuccessRate(ExecutionStatusEnum.PASSED.getCode().equals(testCaseResult.getStatus()) ? 
            java.math.BigDecimal.valueOf(100.00) : java.math.BigDecimal.valueOf(0.00));
        Long duration = testCaseResult.getDuration() != null ? testCaseResult.getDuration() : 0L;
        summary.setTotalDuration(duration);
        summary.setAvgDuration(duration);
        summary.setMaxDuration(duration);
        summary.setMinDuration(duration);
        summary.setReportStatus(ReportStatusEnum.GENERATING.getCode());
        summary.setFileFormat(Constants.DEFAULT_REPORT_FORMAT);
        summary.setGeneratedBy(userId);
        summary.setCreatedAt(LocalDateTime.now());
        summary.setUpdatedAt(LocalDateTime.now());
        summary.setIsDeleted(false);

        return summary;
    }

    /**
     * 构建执行结果
     */
    private ExecutionResultDTO buildExecutionResult(TestCaseExecutionDTO executionDTO, Long executionId, Long reportId, TestExecutionRecord executionRecord) {
        ExecutionResultDTO result = new ExecutionResultDTO();
        result.setExecutionId(executionId);
        result.setCaseId(executionDTO.getCaseId());
        result.setCaseCode(executionDTO.getCaseCode());
        result.setCaseName(executionDTO.getName());
        result.setStatus(executionDTO.getExecutionStatus());
        result.setDuration(executionDTO.getExecutionDuration());
        result.setStartTime(executionDTO.getExecutionStartTime());
        result.setEndTime(executionDTO.getExecutionEndTime());
        result.setResponseStatus(executionDTO.getHttpResponseStatus());
        result.setFailureMessage(executionDTO.getFailureMessage());
        result.setFailureType(executionDTO.getFailureType());
        result.setFailureTrace(executionDTO.getFailureTrace());
        result.setLogsLink("/api/test-results/" + executionId + "/logs");
        result.setReportId(reportId);
        
        // 查询报告名称
        if (reportId != null) {
            TestReportSummary reportSummary = testExecutionMapper.findTestReportSummaryById(reportId);
            if (reportSummary != null) {
                result.setReportName(reportSummary.getReportName());
            }
        }

        // 添加接口信息
        if (executionDTO.getApiInfo() != null) {
            result.setApiId(executionDTO.getApiInfo().getApiId());
            result.setApiName(executionDTO.getApiInfo().getName());
        }

        // 添加执行范围和执行类型
        result.setExecutionScope(executionRecord != null ? executionRecord.getExecutionScope() : "test_case");
        result.setExecutionType(executionRecord != null ? executionRecord.getExecutionType() : "manual");
        result.setEnvironment(executionRecord != null ? executionRecord.getEnvironment() : null);
        
        // 添加响应信息
        result.setResponseBody(executionDTO.getHttpResponseBody());
        result.setResponseHeaders(executionDTO.getHttpResponseHeaders());
        
        // 添加提取的变量
        result.setExtractedVariables(executionDTO.getExtractedValues());

        // 转换断言结果为详细格式
        if (executionDTO.getAssertionResults() != null && !executionDTO.getAssertionResults().isEmpty()) {
            int passedCount = 0;
            int failedCount = 0;
            List<ExecutionResultDTO.AssertionDetailDTO> assertionDetails = new java.util.ArrayList<>();
            
            int assertionId = 1;
            for (TestCaseExecutionDTO.AssertionResultDTO assertion : executionDTO.getAssertionResults()) {
                ExecutionResultDTO.AssertionDetailDTO detail = new ExecutionResultDTO.AssertionDetailDTO();
                detail.setAssertionId(assertionId++);
                detail.setAssertionType(assertion.getAssertionType());
                detail.setExpectedValue(assertion.getExpectedValue());
                detail.setActualValue(assertion.getActualValue());
                detail.setPassed(assertion.getPassed());
                detail.setErrorMessage(assertion.getErrorMessage());
                
                // 生成描述
                String description = generateAssertionDescription(assertion);
                detail.setDescription(description);
                
                assertionDetails.add(detail);
                
                if (assertion.getPassed()) {
                    passedCount++;
                } else {
                    failedCount++;
                }
            }
            
            result.setAssertionsPassed(passedCount);
            result.setAssertionsFailed(failedCount);
            result.setAssertionDetails(assertionDetails);
            
            log.info("构建执行结果 - 断言统计: 通过={}, 失败={}", passedCount, failedCount);
        } else {
            // 兜底逻辑：当没有断言结果时，根据执行状态设置默认的断言统计
            if (result.getAssertionsPassed() == null) {
                result.setAssertionsPassed(0);
            }
            if (result.getAssertionsFailed() == null) {
                // 如果执行失败，至少有1个断言失败
                if ("failed".equals(result.getStatus()) || "broken".equals(result.getStatus())) {
                    result.setAssertionsFailed(1);
                } else if ("passed".equals(result.getStatus())) {
                    result.setAssertionsFailed(0);
                } else {
                    result.setAssertionsFailed(0);
                }
            }
            log.info("构建执行结果 - 无断言结果，使用兜底逻辑: status={}, assertionsPassed={}, assertionsFailed={}", 
                    result.getStatus(), result.getAssertionsPassed(), result.getAssertionsFailed());
        }

        return result;
    }
    
    /**
     * 生成断言描述
     */
    private String generateAssertionDescription(TestCaseExecutionDTO.AssertionResultDTO assertion) {
        switch (assertion.getAssertionType()) {
            case "status_code":
                return "验证HTTP状态码";
            case "json_path":
                return "验证JSON字段值";
            case "json_path_exists":
                return "验证JSON字段存在";
            case "response_time":
                return "验证响应时间";
            case "schema":
                return "验证响应Schema";
            default:
                return "验证" + assertion.getAssertionType();
        }
    }

    /**
     * 从测试结果构建执行结果
     */
    private ExecutionResultDTO buildExecutionResultFromTestCaseResult(TestCaseResult testCaseResult) {
        ExecutionResultDTO result = new ExecutionResultDTO();
        result.setExecutionId(testCaseResult.getExecutionId());
        result.setCaseId(testCaseResult.getRefId());
        result.setCaseName(testCaseResult.getFullName());
        result.setStatus(testCaseResult.getStatus());
        result.setDuration(testCaseResult.getDuration());
        result.setStartTime(testCaseResult.getStartTime());
        result.setEndTime(testCaseResult.getEndTime());
        result.setFailureMessage(testCaseResult.getFailureMessage());
        result.setLogsLink(testCaseResult.getLogsLink());
        result.setReportId(testCaseResult.getReportId());

        // 兜底逻辑：当没有断言结果时，根据执行状态设置默认的断言统计
        if (result.getAssertionsPassed() == null) {
            result.setAssertionsPassed(0);
        }
        if (result.getAssertionsFailed() == null) {
            // 如果执行失败，至少有1个断言失败
            if ("failed".equals(result.getStatus()) || "broken".equals(result.getStatus())) {
                result.setAssertionsFailed(1);
            } else if ("passed".equals(result.getStatus())) {
                result.setAssertionsFailed(0);
            } else {
                result.setAssertionsFailed(0);
            }
        }

        return result;
    }

    // ========== 批量用例执行相关方法 ==========

    @Override
    public ModuleExecutionResultDTO executeTestCases(List<Integer> caseIds, String environment, String baseUrl, Integer userId) {
        log.info("执行多个测试用例: caseIds={}, environment={}, userId={}", caseIds, environment, userId);
        
        LocalDateTime startTime = LocalDateTime.now();
        
        // 创建批量执行的执行记录
        Integer firstCaseId = caseIds.isEmpty() ? 0 : caseIds.get(0);
        TestExecutionRecord executionRecord = createExecutionRecord("test_suite", firstCaseId, 
                "批量用例执行 (" + caseIds.size() + "个用例)", userId, "scheduled", environment);
        executionRecord.setTotalCases(caseIds.size());
        testExecutionRecordMapper.insertExecutionRecord(executionRecord);
        
        int passed = 0, failed = 0, skipped = 0;
        List<Long> executionIds = new ArrayList<>();
        
        for (Integer caseId : caseIds) {
            try {
                TestCaseExecutionDTO testCase = testExecutionMapper.findTestCaseForExecution(caseId);
                if (testCase == null) {
                    log.warn("测试用例不存在: caseId={}", caseId);
                    skipped++;
                    continue;
                }
                
                // 直接调用内部执行方法，不生成单独的报告
                ExecutionResultDTO result = executeTestCaseInternal(caseId, environment, baseUrl, userId, executionRecord.getRecordId());
                
                if (result.getExecutionId() != null) {
                    executionIds.add(result.getExecutionId());
                }
                
                if ("passed".equals(result.getStatus()) || "success".equals(result.getStatus())) {
                    passed++;
                } else if ("skipped".equals(result.getStatus())) {
                    skipped++;
                } else {
                    failed++;
                }
            } catch (Exception e) {
                log.error("执行测试用例失败: caseId={}", caseId, e);
                failed++;
            }
        }
        
        // 更新执行记录统计
        executionRecord.setPassedCases(passed);
        executionRecord.setFailedCases(failed);
        executionRecord.setSkippedCases(skipped);
        BigDecimal successRate = caseIds.size() > 0 ? 
            BigDecimal.valueOf(passed * 100.0 / caseIds.size()) : BigDecimal.ZERO;
        executionRecord.setSuccessRate(successRate);
        executionRecord.setStatus(failed > 0 ? "failed" : "success");
        testExecutionRecordMapper.updateExecutionRecord(executionRecord);
        
        // 生成汇总报告
        Long summaryReportId = generateBatchTestReport(executionRecord.getRecordId(), executionIds, userId, caseIds.size(), passed, failed, skipped, environment, caseIds);
        
        ModuleExecutionResultDTO resultDTO = new ModuleExecutionResultDTO();
        resultDTO.setExecutionId(executionRecord.getRecordId());
        resultDTO.setModuleId(null);
        resultDTO.setModuleName("批量用例执行");
        resultDTO.setTotalCases(caseIds.size());
        resultDTO.setPassed(passed);
        resultDTO.setFailed(failed);
        resultDTO.setSkipped(skipped);
        resultDTO.setSuccessRate(successRate.doubleValue());
        resultDTO.setStartTime(String.valueOf(startTime));
        resultDTO.setEndTime(String.valueOf(LocalDateTime.now()));
        resultDTO.setStatus(failed > 0 ? "failed" : "success");
        
        log.info("批量用例执行完成: total={}, passed={}, failed={}, skipped={}", 
                caseIds.size(), passed, failed, skipped);
        
        return resultDTO;
    }
    
    /**
     * 内部方法：执行单个测试用例，不生成独立报告（用于批量执行）
     */
    private ExecutionResultDTO executeTestCaseInternal(Integer caseId, String environment, String baseUrl, Integer userId, Long batchExecutionRecordId) {
        TestExecutionRecord executionRecord = null;
        try {
            // 1. 查询用例执行信息
            TestCaseExecutionDTO executionDTO = testExecutionMapper.findTestCaseForExecution(caseId);
            if (executionDTO == null) {
                throw new RuntimeException("测试用例不存在或未启用");
            }

            // 2. 创建执行记录
            executionRecord = createExecutionRecord("test_case", caseId, executionDTO.getName(), 
                userId, "scheduled", environment);
            testExecutionRecordMapper.insertExecutionRecord(executionRecord);

            // 3. 设置执行参数
            ExecuteTestCaseDTO executeDTO = new ExecuteTestCaseDTO();
            executeDTO.setEnvironment(environment);
            executeDTO.setBaseUrl(baseUrl);
            setExecutionParameters(executionDTO, executeDTO);

            // 4. 执行测试用例
            TestCaseExecutionDTO result = testCaseExecutor.executeTestCase(executionDTO);

            // 5. 生成执行ID
            Long executionId = generateExecutionId();

            // 6. 保存测试结果（关联批量执行记录ID）
            TestCaseResult testCaseResult = buildTestCaseResult(result, executionId, batchExecutionRecordId, null, userId);
            testExecutionMapper.insertTestCaseResult(testCaseResult);

            // 7. 更新执行记录为完成（不生成报告）
            updateExecutionRecordOnCompletion(executionRecord, result, null);
            testExecutionRecordMapper.updateExecutionRecord(executionRecord);

            // 8. 构建返回结果
            return buildExecutionResult(result, executionId, null, executionRecord);

        } catch (Exception e) {
            log.error("执行测试用例失败: {}", e.getMessage(), e);
            // 更新执行记录为失败
            if (executionRecord != null) {
                updateExecutionRecordOnFailure(executionRecord, e.getMessage());
                testExecutionRecordMapper.updateExecutionRecord(executionRecord);
            }
            
            // 构建失败的执行结果
            ExecutionResultDTO failureResult = new ExecutionResultDTO();
            failureResult.setExecutionId(generateExecutionId());
            failureResult.setCaseId(caseId);
            failureResult.setCaseName("测试用例-" + caseId);
            failureResult.setStatus(ExecutionStatusEnum.FAILED.getCode());
            failureResult.setFailureMessage("执行失败: " + e.getMessage());
            failureResult.setFailureType("EXECUTION_ERROR");
            failureResult.setStartTime(LocalDateTime.now());
            failureResult.setEndTime(LocalDateTime.now());
            failureResult.setDuration(0L);
            failureResult.setAssertionsPassed(0);
            failureResult.setAssertionsFailed(1);
            failureResult.setExecutionScope("test_case");
            failureResult.setEnvironment(environment);
            
            return failureResult;
        }
    }
    
    /**
     * 生成批量执行的汇总报告
     */
    private Long generateBatchTestReport(Long executionRecordId, List<Long> executionIds, Integer userId, 
            int totalCases, int passed, int failed, int skipped, String environment, List<Integer> caseIds) {
        try {
            LocalDateTime now = LocalDateTime.now();
            BigDecimal successRate = totalCases > 0 ? 
                BigDecimal.valueOf(passed * 100.0 / totalCases) : BigDecimal.ZERO;
            
            String reportName = generateSmartReportName(caseIds, now);
            
            TestReportSummary reportSummary = new TestReportSummary();
            reportSummary.setReportName(reportName);
            reportSummary.setReportType(ReportTypeEnum.EXECUTION.getCode());
            reportSummary.setExecutionId(executionRecordId);
            reportSummary.setProjectId(1);
            reportSummary.setEnvironment(environment != null ? environment : "test");
            reportSummary.setStartTime(now);
            reportSummary.setEndTime(now);
            reportSummary.setDuration(0L);
            reportSummary.setTotalCases(totalCases);
            reportSummary.setExecutedCases(totalCases);
            reportSummary.setPassedCases(passed);
            reportSummary.setFailedCases(failed);
            reportSummary.setBrokenCases(0);
            reportSummary.setSkippedCases(skipped);
            reportSummary.setSuccessRate(successRate);
            reportSummary.setTotalDuration(0L);
            reportSummary.setAvgDuration(0L);
            reportSummary.setMaxDuration(0L);
            reportSummary.setMinDuration(0L);
            reportSummary.setReportStatus(failed > 0 ? "failed" : "completed");
            reportSummary.setFileFormat("html");
            reportSummary.setGeneratedBy(userId);
            reportSummary.setCreatedAt(now);
            reportSummary.setUpdatedAt(now);
            reportSummary.setIsDeleted(false);
            
            testExecutionMapper.insertTestReportSummary(reportSummary);
            
            // 更新所有相关测试结果的报告ID
            for (Long execId : executionIds) {
                TestCaseResult testCaseResult = testExecutionMapper.findTestCaseResultByExecutionId(execId);
                if (testCaseResult != null) {
                    testCaseResult.setReportId(reportSummary.getReportId());
                    testCaseResult.setExecutionRecordId(executionRecordId);
                    testExecutionMapper.updateTestCaseResult(testCaseResult);
                }
            }
            
            log.info("生成批量测试报告: reportId={}, reportName={}, totalCases={}, passed={}, failed={}", 
                    reportSummary.getReportId(), reportName, totalCases, passed, failed);
            
            return reportSummary.getReportId();
        } catch (Exception e) {
            log.error("生成批量测试报告失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 智能生成报告名称
     * 规则：
     * 1. 单个测试用例 -> 用例名称 + 时间戳
     * 2. 同一接口下的多个用例 -> 接口名称 + 时间戳
     * 3. 同一模块下的多个用例 -> 模块名称 + 时间戳
     * 4. 同一项目下的多个用例 -> 项目名称 + 时间戳
     * 5. 跨项目的用例 -> "批量测试报告" + 时间戳
     */
    private String generateSmartReportName(List<Integer> caseIds, LocalDateTime time) {
        if (caseIds == null || caseIds.isEmpty()) {
            return "测试报告_" + formatTimestamp(time);
        }
        
        if (caseIds.size() == 1) {
            TestCaseExecutionDTO testCase = testExecutionMapper.findTestCaseForExecution(caseIds.get(0));
            if (testCase != null && testCase.getName() != null) {
                return testCase.getName() + "_" + formatTimestamp(time);
            }
            return "测试用例报告_" + formatTimestamp(time);
        }
        
        List<Map<String, Object>> scopeInfoList = testExecutionMapper.findTestCaseScopeInfo(caseIds);
        if (scopeInfoList == null || scopeInfoList.isEmpty()) {
            return "批量测试报告_" + formatTimestamp(time);
        }
        
        Set<Integer> apiIds = new HashSet<>();
        Set<Integer> moduleIds = new HashSet<>();
        Set<Integer> projectIds = new HashSet<>();
        String apiName = null;
        String moduleName = null;
        String projectName = null;
        
        for (Map<String, Object> info : scopeInfoList) {
            Object apiId = info.get("apiId");
            Object moduleId = info.get("moduleId");
            Object projectId = info.get("projectId");
            
            if (apiId != null) {
                apiIds.add(((Number) apiId).intValue());
                if (apiName == null && info.get("apiName") != null) {
                    apiName = (String) info.get("apiName");
                }
            }
            if (moduleId != null) {
                moduleIds.add(((Number) moduleId).intValue());
                if (moduleName == null && info.get("moduleName") != null) {
                    moduleName = (String) info.get("moduleName");
                }
            }
            if (projectId != null) {
                projectIds.add(((Number) projectId).intValue());
                if (projectName == null && info.get("projectName") != null) {
                    projectName = (String) info.get("projectName");
                }
            }
        }
        
        if (apiIds.size() == 1 && apiName != null) {
            return apiName + "_" + formatTimestamp(time);
        }
        
        if (moduleIds.size() == 1 && moduleName != null) {
            return moduleName + "_" + formatTimestamp(time);
        }
        
        if (projectIds.size() == 1 && projectName != null) {
            return projectName + "_" + formatTimestamp(time);
        }
        
        return "批量测试报告_" + formatTimestamp(time);
    }
    
    private String formatTimestamp(LocalDateTime time) {
        return time.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    // ========== 模块执行相关方法 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModuleExecutionResultDTO executeModule(Integer moduleId, ExecuteModuleDTO executeDTO, Integer userId) {
        // 1. 验证模块
        Module module = validateModule(moduleId);
        
        // 2. 查询和过滤测试用例
        List<TestCase> testCases = getFilteredTestCases(moduleId, executeDTO);
        
        if (testCases.isEmpty()) {
            throw new RuntimeException("该模块下没有可执行的测试用例");
        }
        
        // 3. 设置默认参数
        setDefaultExecutionParams(executeDTO);
        
        // 4. 同步执行测试用例
        return executeTestCasesSync(module, testCases, executeDTO, userId);
    }

    @Override
    public ModuleExecutionResultDTO executeModuleAsync(Integer moduleId, ExecuteModuleDTO executeDTO, Integer userId) {
        // 1. 验证模块
        Module module = validateModule(moduleId);
        
        // 2. 查询和过滤测试用例
        List<TestCase> testCases = getFilteredTestCases(moduleId, executeDTO);
        
        if (testCases.isEmpty()) {
            throw new RuntimeException("该模块下没有可执行的测试用例");
        }
        
        // 3. 设置默认参数
        setDefaultExecutionParams(executeDTO);
        
        // 4. 生成任务ID
        String taskId = generateModuleTaskId();
        
        // 5. 创建任务状态
        ModuleExecutionResultDTO taskInfo = createModuleTaskInfo(taskId, module, testCases, executeDTO);
        
        // 6. 缓存任务信息
        moduleTaskStatusCache.put(taskId, taskInfo);
        try {
            redisComponent.setString(Constants.MODULE_EXECUTION_QUEUE + ":" + taskId, 
                objectMapper.writeValueAsString(taskInfo), Constants.MODULE_RESULT_CACHE_HOURS * 3600);
        } catch (Exception e) {
            // 忽略JSON序列化错误，继续执行
        }
        
        // 7. 异步执行
        CompletableFuture.runAsync(() -> {
            try {
                executeTestCasesAsync(taskId, module, testCases, executeDTO, userId);
            } catch (Exception e) {
                updateModuleTaskStatus(taskId, TaskExecutionStatusEnum.FAILED.getCode(), 
                    "执行失败: " + e.getMessage());
            }
        }, executorService);
        
        return taskInfo;
    }

    @Override
    public ModuleExecutionResultDTO getModuleTaskStatus(String taskId, Integer userId) {
        // 1. 从缓存获取任务状态
        ModuleExecutionResultDTO taskInfo = moduleTaskStatusCache.get(taskId);
        if (taskInfo == null) {
            // 从Redis获取
            String taskJson = redisComponent.getString(Constants.MODULE_EXECUTION_QUEUE + ":" + taskId);
            if (taskJson != null) {
                try {
                    taskInfo = objectMapper.readValue(taskJson, ModuleExecutionResultDTO.class);
                } catch (Exception e) {
                    throw new RuntimeException("任务不存在");
                }
            } else {
                throw new RuntimeException("任务不存在");
            }
        }
        
        return taskInfo;
    }

    @Override
    public boolean cancelModuleTask(String taskId, Integer userId) {
        ModuleExecutionResultDTO taskInfo = getModuleTaskStatus(taskId, userId);
        
        if (TaskExecutionStatusEnum.COMPLETED.getCode().equals(taskInfo.getStatus()) ||
            TaskExecutionStatusEnum.FAILED.getCode().equals(taskInfo.getStatus()) ||
            TaskExecutionStatusEnum.CANCELLED.getCode().equals(taskInfo.getStatus())) {
            return false;
        }
        
        // 更新任务状态为已取消
        updateModuleTaskStatus(taskId, TaskExecutionStatusEnum.CANCELLED.getCode(), "任务已被用户取消");
        
        return true;
    }

    /**
     * 验证模块
     */
    private Module validateModule(Integer moduleId) {
        Module module = testExecutionMapper.findModuleById(moduleId);
        if (module == null) {
            throw new RuntimeException("模块不存在");
        }
        
        if (!ModuleStatusEnum.ACTIVE.getCode().equals(module.getStatus())) {
            throw new RuntimeException("模块已禁用，无法执行测试");
        }
        
        return module;
    }

    /**
     * 获取过滤后的测试用例
     */
    private List<TestCase> getFilteredTestCases(Integer moduleId, ExecuteModuleDTO executeDTO) {
        List<String> priorityList = null;
        List<String> tagsList = null;
        Boolean enabledOnly = true;
        
        if (executeDTO.getCaseFilter() != null) {
            priorityList = executeDTO.getCaseFilter().getPriority();
            tagsList = executeDTO.getCaseFilter().getTags();
            enabledOnly = executeDTO.getCaseFilter().getEnabledOnly();
        }
        
        return testExecutionMapper.findTestCasesByModuleId(moduleId, priorityList, tagsList, enabledOnly);
    }

    /**
     * 设置默认执行参数
     */
    private void setDefaultExecutionParams(ExecuteModuleDTO executeDTO) {
        if (executeDTO.getConcurrency() == null) {
            executeDTO.setConcurrency(Constants.DEFAULT_CONCURRENCY);
        }
        
        if (executeDTO.getConcurrency() > Constants.MAX_CONCURRENCY) {
            throw new RuntimeException("并发数不能超过" + Constants.MAX_CONCURRENCY);
        }
        
        if (executeDTO.getConcurrency() < Constants.MIN_CONCURRENCY) {
            executeDTO.setConcurrency(Constants.MIN_CONCURRENCY);
        }
        
        if (executeDTO.getAsync() == null) {
            executeDTO.setAsync(Constants.DEFAULT_ASYNC_EXECUTION);
        }
        
        if (executeDTO.getTimeout() == null) {
            executeDTO.setTimeout(Constants.DEFAULT_EXECUTION_TIMEOUT);
        }
        
        if (executeDTO.getEnvironment() == null) {
            executeDTO.setEnvironment(Constants.DEFAULT_ENVIRONMENT);
        }
    }

    /**
     * 生成模块任务ID
     */
    private String generateModuleTaskId() {
        return Constants.MODULE_TASK_PREFIX + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 创建模块任务信息
     */
    private ModuleExecutionResultDTO createModuleTaskInfo(String taskId, Module module, 
                                                         List<TestCase> testCases, ExecuteModuleDTO executeDTO) {
        ModuleExecutionResultDTO taskInfo = new ModuleExecutionResultDTO();
        taskInfo.setTaskId(taskId);
        taskInfo.setModuleId(module.getModuleId());
        taskInfo.setModuleName(module.getName());
        taskInfo.setTotalCases(testCases.size());
        taskInfo.setFilteredCases(testCases.size());
        taskInfo.setStatus(TaskExecutionStatusEnum.QUEUED.getCode());
        taskInfo.setConcurrency(executeDTO.getConcurrency());
        taskInfo.setEstimatedDuration(testCases.size() * Constants.ESTIMATED_TIME_PER_CASE);
        taskInfo.setQueuePosition(1);
        taskInfo.setMonitorUrl("/api/tasks/" + taskId + "/status");
        taskInfo.setReportUrl("/api/reports/module/" + module.getModuleId() + "/executions/latest");
        
        return taskInfo;
    }

    /**
     * 同步执行测试用例
     */
    private ModuleExecutionResultDTO executeTestCasesSync(Module module, List<TestCase> testCases, 
                                                          ExecuteModuleDTO executeDTO, Integer userId) {
        LocalDateTime startTime = LocalDateTime.now();
        
        // 初始化共享变量池
        String executionId = "module_" + module.getModuleId() + "_" + System.currentTimeMillis();
        variablePoolService.initializePool(executionId);
        
        // 使用依赖解析服务对测试用例排序
        List<TestCase> sortedTestCases = dependencyResolverService.sortTestCasesByDependency(testCases);
        List<List<TestCase>> executionLayers = dependencyResolverService.getExecutionLayers(testCases);
        
        log.info("模块测试执行: moduleId={}, 总用例数={}, 执行层数={}", 
                module.getModuleId(), testCases.size(), executionLayers.size());
        
        // 创建执行记录
        TestExecutionRecord executionRecord = createExecutionRecord("module", module.getModuleId(), 
            module.getName(), userId, executeDTO.getExecutionType(), executeDTO.getEnvironment());
        executionRecord.setTotalCases(sortedTestCases.size());
        testExecutionRecordMapper.insertExecutionRecord(executionRecord);
        
        // 创建测试报告汇总
        Long reportId = createModuleTestReportSummary(module, sortedTestCases.size(), userId);
        
        // 关键修复：使用 caseResults 列表收集结果，避免依赖数据库查询
        List<Map<String, Object>> caseResults = new ArrayList<>();
        
        for (List<TestCase> layer : executionLayers) {
            log.info("执行第{}层测试用例, 用例数: {}", executionLayers.indexOf(layer) + 1, layer.size());
            
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            
            for (TestCase testCase : layer) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        ExecuteTestCaseDTO caseExecuteDTO = convertToTestCaseExecuteDTO(executeDTO);
                        caseExecuteDTO.setExecutionId(executionId);
                        
                        ExecutionResultDTO result = executeTestCaseWithVariables(
                            testCase.getCaseId(), caseExecuteDTO, userId, executionId, 
                            executionRecord.getRecordId(), reportId);
                        
                        // 注意：executeTestCaseWithVariables 方法内部已经插入了 TestCaseResult
                        // 收集结果到 caseResults 列表
                        Map<String, Object> caseResult = new HashMap<>();
                        caseResult.put("caseId", testCase.getCaseId());
                        caseResult.put("caseCode", testCase.getCaseCode());
                        caseResult.put("caseName", testCase.getName());
                        caseResult.put("status", result.getStatus());
                        caseResult.put("duration", result.getDuration());
                        if (result.getResponseStatus() != null) {
                            caseResult.put("responseStatus", result.getResponseStatus().intValue());
                        }
                        caseResult.put("failureMessage", result.getFailureMessage());
                        
                        synchronized (caseResults) {
                            caseResults.add(caseResult);
                        }
                        
                        if (result.getExtractedVariables() != null) {
                            variablePoolService.setVariables(executionId, result.getExtractedVariables());
                            log.info("提取变量到共享池: caseId={}, variables={}", 
                                    testCase.getCaseId(), result.getExtractedVariables().keySet());
                        }
                        
                    } catch (Exception e) {
                        recordModuleTestCaseFailure(reportId, testCase, e.getMessage(), userId, executionRecord.getRecordId());
                        
                        // 收集失败结果
                        Map<String, Object> caseResult = new HashMap<>();
                        caseResult.put("caseId", testCase.getCaseId());
                        caseResult.put("caseCode", testCase.getCaseCode());
                        caseResult.put("caseName", testCase.getName());
                        caseResult.put("status", "failed");
                        caseResult.put("failureMessage", e.getMessage());
                        
                        synchronized (caseResults) {
                            caseResults.add(caseResult);
                        }
                    }
                }, executorService);
                
                futures.add(future);
            }
            
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
        
        variablePoolService.clearPool(executionId);
        
        LocalDateTime endTime = LocalDateTime.now();
        
        // 关键修复：直接从 caseResults 列表计算统计结果
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        int broken = 0;
        
        synchronized (caseResults) {
            for (Map<String, Object> cr : caseResults) {
                String status = (String) cr.get("status");
                if ("passed".equals(status)) {
                    passed++;
                } else if ("failed".equals(status)) {
                    failed++;
                } else if ("skipped".equals(status)) {
                    skipped++;
                } else if ("broken".equals(status)) {
                    broken++;
                }
            }
        }
        
        int total = sortedTestCases.size();
        BigDecimal successRate = total > 0 ? BigDecimal.valueOf((double) passed / total * 100).setScale(2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        // 直接更新 TestReportSummaries 表
        TestReportSummary reportSummary = testExecutionMapper.findTestReportSummaryById(reportId);
        if (reportSummary != null) {
            reportSummary.setExecutedCases(total);
            reportSummary.setPassedCases(passed);
            reportSummary.setFailedCases(failed);
            reportSummary.setBrokenCases(broken);
            reportSummary.setSkippedCases(skipped);
            reportSummary.setSuccessRate(successRate);
            reportSummary.setStartTime(startTime);
            reportSummary.setEndTime(endTime);
            reportSummary.setDuration(java.time.Duration.between(startTime, endTime).toMillis());
            reportSummary.setReportStatus(failed > 0 || broken > 0 ? "failed" : "completed");
            reportSummary.setUpdatedAt(LocalDateTime.now());
            testExecutionMapper.updateTestReportSummary(reportSummary);
            log.info("更新模块测试报告统计: reportId={}, total={}, passed={}, failed={}, broken={}, skipped={}, successRate={}%", 
                    reportId, total, passed, failed, broken, skipped, successRate);
        }
        
        log.info("模块测试统计结果: reportId={}, passed={}, failed={}, skipped={}, broken={}, totalCases={}", 
                reportId, passed, failed, skipped, broken, sortedTestCases.size());
        
        // 更新执行记录的统计数据
        executionRecord.setEndTime(endTime);
        executionRecord.setDurationSeconds((int) java.time.Duration.between(startTime, endTime).toSeconds());
        executionRecord.setExecutedCases(passed + failed + skipped + broken);
        executionRecord.setPassedCases(passed);
        executionRecord.setFailedCases(failed + broken);
        executionRecord.setSkippedCases(skipped);
        executionRecord.setSuccessRate(sortedTestCases.size() > 0 ? 
            BigDecimal.valueOf((double) passed / sortedTestCases.size() * 100) : BigDecimal.ZERO);
        executionRecord.setStatus(failed + broken > 0 ? "failed" : "completed");
        executionRecord.setReportUrl("/api/reports/" + reportId + "/summary");
        testExecutionRecordMapper.updateExecutionRecord(executionRecord);
        
        // 调试日志：确认更新完成
        log.info("模块测试更新完成: recordId={}, passedCases={}, failedCases={}, status={}", 
                executionRecord.getRecordId(), passed, failed + broken, executionRecord.getStatus());
        
        ModuleExecutionResultDTO result = new ModuleExecutionResultDTO();
        result.setExecutionId(System.currentTimeMillis());
        result.setModuleId(module.getModuleId());
        result.setModuleName(module.getName());
        result.setStartTime(DateUtil.formatToISO8601(startTime));
        result.setEndTime(DateUtil.formatToISO8601(endTime));
        result.setTotalDuration(java.time.Duration.between(startTime, endTime).toMillis());
        result.setTotalCases(sortedTestCases.size());
        result.setPassed(passed);
        result.setFailed(failed);
        result.setSkipped(skipped);
        result.setBroken(broken);
        result.setSuccessRate(sortedTestCases.size() > 0 ? (double) passed / sortedTestCases.size() * 100 : 0.0);
        result.setReportId(reportId);
        result.setSummaryUrl("/api/reports/" + reportId + "/summary");
        
        return result;
    }

    /**
     * 异步执行测试用例
     */
    private void executeTestCasesAsync(String taskId, Module module, List<TestCase> testCases, 
                                      ExecuteModuleDTO executeDTO, Integer userId) {
        updateModuleTaskStatus(taskId, TaskExecutionStatusEnum.RUNNING.getCode(), "开始执行测试用例");
        
        LocalDateTime startTime = LocalDateTime.now();
        
        TestExecutionRecord executionRecord = createExecutionRecord("module", module.getModuleId(), 
            module.getName(), userId, executeDTO.getExecutionType(), executeDTO.getEnvironment());
        executionRecord.setTotalCases(testCases.size());
        testExecutionRecordMapper.insertExecutionRecord(executionRecord);
        
        Long reportId = createModuleTestReportSummary(module, testCases.size(), userId);
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (TestCase testCase : testCases) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    ExecuteTestCaseDTO caseExecuteDTO = convertToTestCaseExecuteDTO(executeDTO);
                    
                    ExecutionResultDTO result = executeTestCaseWithVariables(testCase.getCaseId(), caseExecuteDTO, userId, null,
                        executionRecord.getRecordId(), reportId);
                    
                    // 注意：executeTestCaseWithVariables 方法内部已经插入了 TestCaseResult
                    
                } catch (Exception e) {
                    recordModuleTestCaseFailure(reportId, testCase, e.getMessage(), userId, executionRecord.getRecordId());
                }
            }, executorService);
            
            futures.add(future);
        }
        
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        LocalDateTime endTime = LocalDateTime.now();
        
        updateReportSummaryStats(reportId, startTime, endTime);
        
        Map<String, Object> stats = testExecutionMapper.countResultsByReportId(reportId);
        
        // 关键修复：由于异步线程使用独立的数据库连接，主线程可能看不到异步线程插入的数据
        // 使用重试机制确保能够查询到数据
        int maxRetries = 3;
        int retryDelayMs = 100;
        
        for (int retry = 0; retry < maxRetries; retry++) {
            if (retry > 0) {
                try {
                    Thread.sleep(retryDelayMs * retry);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            stats = testExecutionMapper.countResultsByReportId(reportId);
            if (stats != null && stats.get("total") != null && ((Number) stats.get("total")).intValue() > 0) {
                break;
            }
            log.warn("统计查询为空, 重试 {}/{}", retry + 1, maxRetries);
        }
        
        int passed = stats != null && stats.get("passed") != null ? ((Number) stats.get("passed")).intValue() : 0;
        int failed = stats != null && stats.get("failed") != null ? ((Number) stats.get("failed")).intValue() : 0;
        int skipped = stats != null && stats.get("skipped") != null ? ((Number) stats.get("skipped")).intValue() : 0;
        int broken = stats != null && stats.get("broken") != null ? ((Number) stats.get("broken")).intValue() : 0;
        
        // 更新执行记录的统计数据
        executionRecord.setEndTime(endTime);
        executionRecord.setDurationSeconds((int) java.time.Duration.between(startTime, endTime).toSeconds());
        executionRecord.setExecutedCases(passed + failed + skipped + broken);
        executionRecord.setPassedCases(passed);
        executionRecord.setFailedCases(failed + broken);
        executionRecord.setSkippedCases(skipped);
        executionRecord.setSuccessRate(testCases.size() > 0 ? 
            BigDecimal.valueOf((double) passed / testCases.size() * 100) : BigDecimal.ZERO);
        executionRecord.setStatus(failed + broken > 0 ? "failed" : "completed");
        executionRecord.setReportUrl("/api/reports/" + reportId + "/summary");
        testExecutionRecordMapper.updateExecutionRecord(executionRecord);
        
        ModuleExecutionResultDTO taskInfo = getModuleTaskStatus(taskId, userId);
        taskInfo.setStatus(TaskExecutionStatusEnum.COMPLETED.getCode());
        taskInfo.setStartTime(DateUtil.formatToISO8601(startTime));
        taskInfo.setEndTime(DateUtil.formatToISO8601(endTime));
        taskInfo.setTotalDuration(java.time.Duration.between(startTime, endTime).toMillis());
        taskInfo.setPassed(passed);
        taskInfo.setFailed(failed);
        taskInfo.setSkipped(skipped);
        taskInfo.setBroken(broken);
        taskInfo.setSuccessRate(testCases.size() > 0 ? (double) passed / testCases.size() * 100 : 0.0);
        taskInfo.setReportId(reportId);
        taskInfo.setSummaryUrl("/api/reports/" + reportId + "/summary");
        
        updateModuleTaskStatus(taskId, TaskExecutionStatusEnum.COMPLETED.getCode(), "执行完成");
    }

    /**
     * 转换执行参数
     */
    private ExecuteTestCaseDTO convertToTestCaseExecuteDTO(ExecuteModuleDTO executeDTO) {
        ExecuteTestCaseDTO caseExecuteDTO = new ExecuteTestCaseDTO();
        caseExecuteDTO.setEnvironment(executeDTO.getEnvironment());
        caseExecuteDTO.setBaseUrl(executeDTO.getBaseUrl());
        caseExecuteDTO.setTimeout(executeDTO.getTimeout());
        caseExecuteDTO.setAuthOverride(executeDTO.getAuthOverride());
        caseExecuteDTO.setVariables(executeDTO.getVariables());
        caseExecuteDTO.setAsync(false); // 单个用例执行不使用异步
        
        return caseExecuteDTO;
    }

    /**
     * 创建模块测试报告汇总
     */
    private Long createModuleTestReportSummary(Module module, int totalCases, Integer userId) {
        TestReportSummary reportSummary = new TestReportSummary();
        String reportName = module.getName() + "_" + formatTimestamp(LocalDateTime.now());
        reportSummary.setReportName(reportName);
        reportSummary.setReportType(ReportTypeEnum.EXECUTION.getCode());
        reportSummary.setProjectId(module.getProjectId());
        reportSummary.setEnvironment("test");
        reportSummary.setStartTime(LocalDateTime.now());
        reportSummary.setEndTime(LocalDateTime.now());
        reportSummary.setDuration(0L);
        reportSummary.setTotalCases(totalCases);
        reportSummary.setExecutedCases(0);
        reportSummary.setPassedCases(0);
        reportSummary.setFailedCases(0);
        reportSummary.setBrokenCases(0);
        reportSummary.setSkippedCases(0);
        reportSummary.setSuccessRate(BigDecimal.valueOf(0.0));
        reportSummary.setTotalDuration(0L);
        reportSummary.setAvgDuration(0L);
        reportSummary.setMaxDuration(0L);
        reportSummary.setMinDuration(0L);
        reportSummary.setReportStatus(ReportStatusEnum.GENERATING.getCode());
        reportSummary.setFileFormat("html");
        reportSummary.setGeneratedBy(userId);
        
        testExecutionMapper.insertTestReportSummary(reportSummary);
        return reportSummary.getReportId();
    }

    /**
     * 记录模块测试用例结果
     */
    private void recordModuleTestCaseResult(Long reportId, TestCase testCase, 
                                           ExecutionResultDTO result, Integer userId, Long executionRecordId) {
        TestCaseResult testCaseResult = new TestCaseResult();
        testCaseResult.setExecutionRecordId(executionRecordId);
        testCaseResult.setReportId(reportId);
        testCaseResult.setTaskType(TaskTypeEnum.TEST_CASE.getCode());
        testCaseResult.setRefId(testCase.getCaseId());
        testCaseResult.setFullName(testCase.getName());
        testCaseResult.setStatus(result.getStatus());
        testCaseResult.setDuration(result.getDuration());
        testCaseResult.setStartTime(LocalDateTime.now());
        testCaseResult.setEndTime(LocalDateTime.now());
        testCaseResult.setEnvironment("test");
        testCaseResult.setSeverity(normalizeSeverity(testCase.getSeverity()));
        testCaseResult.setPriority(normalizePriority(testCase.getPriority()));
        
        testCaseResult.setCaseId(testCase.getCaseId());
        testCaseResult.setCaseCode(testCase.getCaseCode());
        testCaseResult.setCaseName(testCase.getName());
        testCaseResult.setTestLayer("API");
        testCaseResult.setTestType("POSITIVE");
        testCaseResult.setFlakyCount(0);
        testCaseResult.setRetestResult("NOT_RETESTED");
        
        testExecutionMapper.insertTestCaseResult(testCaseResult);
    }

    private void recordModuleTestCaseFailure(Long reportId, TestCase testCase, String errorMessage, Integer userId, Long executionRecordId) {
        TestCaseResult testCaseResult = new TestCaseResult();
        testCaseResult.setExecutionRecordId(executionRecordId);
        testCaseResult.setReportId(reportId);
        testCaseResult.setTaskType(TaskTypeEnum.TEST_CASE.getCode());
        testCaseResult.setRefId(testCase.getCaseId());
        testCaseResult.setFullName(testCase.getName());
        testCaseResult.setStatus(ExecutionStatusEnum.FAILED.getCode());
        testCaseResult.setDuration(0L);
        testCaseResult.setStartTime(LocalDateTime.now());
        testCaseResult.setEndTime(LocalDateTime.now());
        testCaseResult.setFailureMessage(errorMessage);
        testCaseResult.setEnvironment("test");
        testCaseResult.setSeverity(normalizeSeverity(testCase.getSeverity()));
        testCaseResult.setPriority(normalizePriority(testCase.getPriority()));
        
        testCaseResult.setCaseId(testCase.getCaseId());
        testCaseResult.setCaseCode(testCase.getCaseCode());
        testCaseResult.setCaseName(testCase.getName());
        testCaseResult.setTestLayer("API");
        testCaseResult.setTestType("POSITIVE");
        testCaseResult.setFlakyCount(0);
        testCaseResult.setRetestResult("NOT_RETESTED");
        
        testExecutionMapper.insertTestCaseResult(testCaseResult);
    }

    /**
     * 更新模块任务状态
     */
    private void updateModuleTaskStatus(String taskId, String status, String message) {
        ModuleExecutionResultDTO taskInfo = moduleTaskStatusCache.get(taskId);
        if (taskInfo != null) {
            taskInfo.setStatus(status);
            moduleTaskStatusCache.put(taskId, taskInfo);
            
            try {
                redisComponent.setString(Constants.MODULE_EXECUTION_QUEUE + ":" + taskId, 
                    objectMapper.writeValueAsString(taskInfo), Constants.MODULE_RESULT_CACHE_HOURS * 3600);
            } catch (Exception e) {
                // 忽略JSON序列化错误
            }
        }
    }

    // ========== 项目执行相关方法 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectExecutionResultDTO executeProject(Integer projectId, ExecuteProjectDTO executeDTO, Integer userId) {
        // 1. 验证项目
        Project project = validateProject(projectId);
        
        // 2. 查询和过滤测试用例
        List<TestCase> testCases = getFilteredProjectTestCases(projectId, executeDTO);
        
        if (testCases.isEmpty()) {
            throw new RuntimeException("该项目下没有可执行的测试用例");
        }
        
        // 3. 设置默认参数
        setDefaultProjectExecutionParams(executeDTO);
        
        // 4. 同步执行测试用例
        return executeProjectTestCasesSync(project, testCases, executeDTO, userId);
    }

    @Override
    public ProjectExecutionResultDTO executeProjectAsync(Integer projectId, ExecuteProjectDTO executeDTO, Integer userId) {
        // 1. 验证项目
        Project project = validateProject(projectId);
        
        // 2. 查询和过滤测试用例
        List<TestCase> testCases = getFilteredProjectTestCases(projectId, executeDTO);
        
        if (testCases.isEmpty()) {
            throw new RuntimeException("该项目下没有可执行的测试用例");
        }
        
        // 3. 设置默认参数
        setDefaultProjectExecutionParams(executeDTO);
        
        // 4. 生成任务ID
        String taskId = generateProjectTaskId();
        
        // 5. 创建任务状态
        ProjectExecutionResultDTO taskInfo = createProjectTaskInfo(taskId, project, testCases, executeDTO);
        
        // 6. 缓存任务信息
        projectTaskStatusCache.put(taskId, taskInfo);
        try {
            redisComponent.setString(Constants.PROJECT_EXECUTION_QUEUE + ":" + taskId, 
                objectMapper.writeValueAsString(taskInfo), Constants.PROJECT_RESULT_CACHE_HOURS * 3600);
        } catch (Exception e) {
            // 忽略JSON序列化错误，继续执行
        }
        
        // 7. 异步执行
        CompletableFuture.runAsync(() -> {
            try {
                executeProjectTestCasesAsync(taskId, project, testCases, executeDTO, userId);
            } catch (Exception e) {
                updateProjectTaskStatus(taskId, TaskExecutionStatusEnum.FAILED.getCode(), 
                    "执行失败: " + e.getMessage());
            }
        }, executorService);
        
        return taskInfo;
    }

    @Override
    public ProjectExecutionResultDTO getProjectTaskStatus(String taskId, Integer userId) {
        // 1. 从缓存获取任务状态
        ProjectExecutionResultDTO taskInfo = projectTaskStatusCache.get(taskId);
        if (taskInfo == null) {
            // 从Redis获取
            String taskJson = redisComponent.getString(Constants.PROJECT_EXECUTION_QUEUE + ":" + taskId);
            if (taskJson != null) {
                try {
                    taskInfo = objectMapper.readValue(taskJson, ProjectExecutionResultDTO.class);
                } catch (Exception e) {
                    throw new RuntimeException("任务不存在");
                }
            } else {
                throw new RuntimeException("任务不存在");
            }
        }
        
        return taskInfo;
    }

    @Override
    public boolean cancelProjectTask(String taskId, Integer userId) {
        ProjectExecutionResultDTO taskInfo = getProjectTaskStatus(taskId, userId);
        
        if (TaskExecutionStatusEnum.COMPLETED.getCode().equals(taskInfo.getStatus()) ||
            TaskExecutionStatusEnum.FAILED.getCode().equals(taskInfo.getStatus()) ||
            TaskExecutionStatusEnum.CANCELLED.getCode().equals(taskInfo.getStatus())) {
            return false;
        }
        
        // 更新任务状态为已取消
        updateProjectTaskStatus(taskId, TaskExecutionStatusEnum.CANCELLED.getCode(), "任务已被用户取消");
        
        return true;
    }

    /**
     * 验证项目
     */
    private Project validateProject(Integer projectId) {
        Project project = testExecutionMapper.findProjectById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }

        
        return project;
    }

    /**
     * 获取过滤后的项目测试用例
     */
    private List<TestCase> getFilteredProjectTestCases(Integer projectId, ExecuteProjectDTO executeDTO) {
        List<Integer> moduleIds = null;
        List<String> priorityList = null;
        List<String> tagsList = null;
        Boolean enabledOnly = true;
        
        if (executeDTO.getModuleFilter() != null) {
            moduleIds = executeDTO.getModuleFilter().getModuleIds();
        }
        
        if (executeDTO.getCaseFilter() != null) {
            priorityList = executeDTO.getCaseFilter().getPriority();
            tagsList = executeDTO.getCaseFilter().getTags();
            enabledOnly = executeDTO.getCaseFilter().getEnabledOnly();
        }
        
        return testExecutionMapper.findTestCasesByProjectId(projectId, moduleIds, priorityList, tagsList, enabledOnly);
    }

    /**
     * 设置默认项目执行参数
     */
    private void setDefaultProjectExecutionParams(ExecuteProjectDTO executeDTO) {
        if (executeDTO.getConcurrency() == null) {
            executeDTO.setConcurrency(Constants.PROJECT_DEFAULT_CONCURRENCY);
        }
        
        if (executeDTO.getConcurrency() > Constants.PROJECT_MAX_CONCURRENCY) {
            throw new RuntimeException("并发数不能超过" + Constants.PROJECT_MAX_CONCURRENCY);
        }
        
        if (executeDTO.getConcurrency() < Constants.PROJECT_MIN_CONCURRENCY) {
            executeDTO.setConcurrency(Constants.PROJECT_MIN_CONCURRENCY);
        }
        
        if (executeDTO.getAsync() == null) {
            executeDTO.setAsync(true); // 项目执行默认异步
        }
        
        if (executeDTO.getTimeout() == null) {
            executeDTO.setTimeout(Constants.DEFAULT_EXECUTION_TIMEOUT);
        }
        
        if (executeDTO.getEnvironment() == null) {
            executeDTO.setEnvironment(Constants.DEFAULT_ENVIRONMENT);
        }
        
        if (executeDTO.getExecutionStrategy() == null) {
            executeDTO.setExecutionStrategy(Constants.DEFAULT_EXECUTION_STRATEGY);
        }
    }

    /**
     * 生成项目任务ID
     */
    private String generateProjectTaskId() {
        return Constants.PROJECT_TASK_PREFIX + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 创建项目任务信息
     */
    private ProjectExecutionResultDTO createProjectTaskInfo(String taskId, Project project, 
                                                           List<TestCase> testCases, ExecuteProjectDTO executeDTO) {
        ProjectExecutionResultDTO taskInfo = new ProjectExecutionResultDTO();
        taskInfo.setTaskId(taskId);
        taskInfo.setProjectId(project.getProjectId());
        taskInfo.setProjectName(project.getName());
        taskInfo.setTotalCases(testCases.size());
        taskInfo.setFilteredCases(testCases.size());
        taskInfo.setStatus(TaskExecutionStatusEnum.QUEUED.getCode());
        taskInfo.setConcurrency(executeDTO.getConcurrency());
        taskInfo.setEstimatedDuration(testCases.size() * Constants.PROJECT_ESTIMATED_TIME_PER_CASE);
        taskInfo.setQueuePosition(1);
        taskInfo.setMonitorUrl("/api/tasks/" + taskId + "/status");
        taskInfo.setReportUrl("/api/reports/project/" + project.getProjectId() + "/executions/latest");
        taskInfo.setCancelUrl("/api/tasks/" + taskId + "/cancel");
        
        return taskInfo;
    }

    /**
     * 同步执行项目测试用例
     */
    private ProjectExecutionResultDTO executeProjectTestCasesSync(Project project, List<TestCase> testCases, 
                                                                 ExecuteProjectDTO executeDTO, Integer userId) {
        LocalDateTime startTime = LocalDateTime.now();
        
        String executionId = "project_" + project.getProjectId() + "_" + System.currentTimeMillis();
        variablePoolService.initializePool(executionId);
        
        List<TestCase> sortedTestCases = dependencyResolverService.sortTestCasesByDependency(testCases);
        List<List<TestCase>> executionLayers = dependencyResolverService.getExecutionLayers(testCases);
        
        log.info("项目测试执行: projectId={}, 总用例数={}, 执行层数={}", 
                project.getProjectId(), testCases.size(), executionLayers.size());
        
        TestExecutionRecord executionRecord = createExecutionRecord("project", project.getProjectId(), 
            project.getName(), userId, executeDTO.getExecutionType(), executeDTO.getEnvironment());
        executionRecord.setTotalCases(sortedTestCases.size());
        testExecutionRecordMapper.insertExecutionRecord(executionRecord);
        
        Long reportId = createProjectTestReportSummary(project, sortedTestCases.size(), userId);
        
        // 关键修复：使用 caseResults 列表收集结果，避免依赖数据库查询
        List<Map<String, Object>> caseResults = new ArrayList<>();
        
        for (List<TestCase> layer : executionLayers) {
            log.info("执行第{}层测试用例, 用例数: {}", executionLayers.indexOf(layer) + 1, layer.size());
            
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            
            for (TestCase testCase : layer) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        ExecuteTestCaseDTO caseExecuteDTO = convertToTestCaseExecuteDTO(executeDTO);
                        caseExecuteDTO.setExecutionId(executionId);
                        
                        ExecutionResultDTO result = executeTestCaseWithVariables(
                            testCase.getCaseId(), caseExecuteDTO, userId, executionId,
                            executionRecord.getRecordId(), reportId);
                        
                        // 注意：executeTestCaseWithVariables 方法内部已经插入了 TestCaseResult
                        
                        // 收集结果到 caseResults 列表
                        Map<String, Object> caseResult = new HashMap<>();
                        caseResult.put("caseId", testCase.getCaseId());
                        caseResult.put("caseCode", testCase.getCaseCode());
                        caseResult.put("caseName", testCase.getName());
                        caseResult.put("status", result.getStatus());
                        caseResult.put("duration", result.getDuration());
                        if (result.getResponseStatus() != null) {
                            caseResult.put("responseStatus", result.getResponseStatus().intValue());
                        }
                        caseResult.put("failureMessage", result.getFailureMessage());
                        
                        synchronized (caseResults) {
                            caseResults.add(caseResult);
                        }
                        
                        if (result.getExtractedVariables() != null) {
                            variablePoolService.setVariables(executionId, result.getExtractedVariables());
                            log.info("提取变量到共享池: caseId={}, variables={}", 
                                    testCase.getCaseId(), result.getExtractedVariables().keySet());
                        }
                    } catch (Exception e) {
                        recordProjectTestCaseFailure(reportId, testCase, e.getMessage(), userId, executionRecord.getRecordId());
                        
                        // 收集失败结果
                        Map<String, Object> caseResult = new HashMap<>();
                        caseResult.put("caseId", testCase.getCaseId());
                        caseResult.put("caseCode", testCase.getCaseCode());
                        caseResult.put("caseName", testCase.getName());
                        caseResult.put("status", "failed");
                        caseResult.put("failureMessage", e.getMessage());
                        
                        synchronized (caseResults) {
                            caseResults.add(caseResult);
                        }
                    }
                }, executorService);
                futures.add(future);
            }
            
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
        
        variablePoolService.clearPool(executionId);
        
        LocalDateTime endTime = LocalDateTime.now();
        
        // 关键修复：直接从 caseResults 列表计算统计结果
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        int broken = 0;
        
        synchronized (caseResults) {
            for (Map<String, Object> cr : caseResults) {
                String status = (String) cr.get("status");
                if ("passed".equals(status)) {
                    passed++;
                } else if ("failed".equals(status)) {
                    failed++;
                } else if ("skipped".equals(status)) {
                    skipped++;
                } else if ("broken".equals(status)) {
                    broken++;
                }
            }
        }
        
        int total = sortedTestCases.size();
        BigDecimal successRate = total > 0 ? BigDecimal.valueOf((double) passed / total * 100).setScale(2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        // 直接更新 TestReportSummaries 表
        TestReportSummary reportSummary = testExecutionMapper.findTestReportSummaryById(reportId);
        if (reportSummary != null) {
            reportSummary.setExecutedCases(total);
            reportSummary.setPassedCases(passed);
            reportSummary.setFailedCases(failed);
            reportSummary.setBrokenCases(broken);
            reportSummary.setSkippedCases(skipped);
            reportSummary.setSuccessRate(successRate);
            reportSummary.setStartTime(startTime);
            reportSummary.setEndTime(endTime);
            reportSummary.setDuration(java.time.Duration.between(startTime, endTime).toMillis());
            reportSummary.setReportStatus(failed > 0 || broken > 0 ? "failed" : "completed");
            reportSummary.setUpdatedAt(LocalDateTime.now());
            testExecutionMapper.updateTestReportSummary(reportSummary);
            log.info("更新项目测试报告统计: reportId={}, total={}, passed={}, failed={}, broken={}, skipped={}, successRate={}%", 
                    reportId, total, passed, failed, broken, skipped, successRate);
        }
        
        log.info("项目测试统计结果: reportId={}, passed={}, failed={}, skipped={}, broken={}, totalCases={}", 
                reportId, passed, failed, skipped, broken, sortedTestCases.size());
        
        // 更新执行记录的统计数据
        executionRecord.setEndTime(endTime);
        executionRecord.setDurationSeconds((int) java.time.Duration.between(startTime, endTime).toSeconds());
        executionRecord.setExecutedCases(passed + failed + skipped + broken);
        executionRecord.setPassedCases(passed);
        executionRecord.setFailedCases(failed + broken);
        executionRecord.setSkippedCases(skipped);
        executionRecord.setSuccessRate(sortedTestCases.size() > 0 ? 
            BigDecimal.valueOf((double) passed / sortedTestCases.size() * 100) : BigDecimal.ZERO);
        executionRecord.setStatus(failed + broken > 0 ? "failed" : "completed");
        executionRecord.setReportUrl("/api/reports/" + reportId + "/summary");
        testExecutionRecordMapper.updateExecutionRecord(executionRecord);
        
        // 调试日志：确认更新完成
        log.info("项目测试更新完成: recordId={}, passedCases={}, failedCases={}, status={}", 
                executionRecord.getRecordId(), passed, failed + broken, executionRecord.getStatus());
        
        ProjectExecutionResultDTO result = new ProjectExecutionResultDTO();
        result.setExecutionId(System.currentTimeMillis());
        result.setProjectId(project.getProjectId());
        result.setProjectName(project.getName());
        result.setStartTime(startTime);
        result.setEndTime(endTime);
        result.setTotalDuration(java.time.Duration.between(startTime, endTime).toMillis());
        result.setTotalCases(sortedTestCases.size());
        result.setPassed(passed);
        result.setFailed(failed);
        result.setSkipped(skipped);
        result.setBroken(broken);
        result.setSuccessRate(sortedTestCases.size() > 0 ? BigDecimal.valueOf((double) passed / sortedTestCases.size() * 100) : BigDecimal.ZERO);
        result.setReportId(reportId);
        result.setSummaryUrl("/api/reports/" + reportId + "/summary");
        result.setDownloadUrl("/api/reports/" + reportId + "/export");
        
        return result;
    }

    /**
     * 异步执行项目测试用例
     */
    private void executeProjectTestCasesAsync(String taskId, Project project, List<TestCase> testCases, 
                                             ExecuteProjectDTO executeDTO, Integer userId) {
        updateProjectTaskStatus(taskId, TaskExecutionStatusEnum.RUNNING.getCode(), "开始执行测试用例");
        
        LocalDateTime startTime = LocalDateTime.now();
        
        TestExecutionRecord executionRecord = createExecutionRecord("project", project.getProjectId(), 
            project.getName(), userId, executeDTO.getExecutionType(), executeDTO.getEnvironment());
        executionRecord.setTotalCases(testCases.size());
        testExecutionRecordMapper.insertExecutionRecord(executionRecord);
        
        Long reportId = createProjectTestReportSummary(project, testCases.size(), userId);
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (TestCase testCase : testCases) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    ExecuteTestCaseDTO caseExecuteDTO = convertToTestCaseExecuteDTO(executeDTO);
                    ExecutionResultDTO result = executeTestCaseWithVariables(testCase.getCaseId(), caseExecuteDTO, userId, null,
                        executionRecord.getRecordId(), reportId);
                    // 注意：executeTestCaseWithVariables 方法内部已经插入了 TestCaseResult
                } catch (Exception e) {
                    recordProjectTestCaseFailure(reportId, testCase, e.getMessage(), userId, executionRecord.getRecordId());
                }
            }, executorService);
            futures.add(future);
        }
        
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        LocalDateTime endTime = LocalDateTime.now();
        
        updateReportSummaryStats(reportId, startTime, endTime);
        
        Map<String, Object> stats = testExecutionMapper.countResultsByReportId(reportId);
        
        // 关键修复：由于异步线程使用独立的数据库连接，主线程可能看不到异步线程插入的数据
        // 使用重试机制确保能够查询到数据
        int maxRetries = 3;
        int retryDelayMs = 100;
        
        for (int retry = 0; retry < maxRetries; retry++) {
            if (retry > 0) {
                try {
                    Thread.sleep(retryDelayMs * retry);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            stats = testExecutionMapper.countResultsByReportId(reportId);
            if (stats != null && stats.get("total") != null && ((Number) stats.get("total")).intValue() > 0) {
                break;
            }
            log.warn("统计查询为空, 重试 {}/{}", retry + 1, maxRetries);
        }
        
        int passed = stats != null && stats.get("passed") != null ? ((Number) stats.get("passed")).intValue() : 0;
        int failed = stats != null && stats.get("failed") != null ? ((Number) stats.get("failed")).intValue() : 0;
        int skipped = stats != null && stats.get("skipped") != null ? ((Number) stats.get("skipped")).intValue() : 0;
        int broken = stats != null && stats.get("broken") != null ? ((Number) stats.get("broken")).intValue() : 0;
        
        // 更新执行记录的统计数据
        executionRecord.setEndTime(endTime);
        executionRecord.setDurationSeconds((int) java.time.Duration.between(startTime, endTime).toSeconds());
        executionRecord.setExecutedCases(passed + failed + skipped + broken);
        executionRecord.setPassedCases(passed);
        executionRecord.setFailedCases(failed + broken);
        executionRecord.setSkippedCases(skipped);
        executionRecord.setSuccessRate(testCases.size() > 0 ? 
            BigDecimal.valueOf((double) passed / testCases.size() * 100) : BigDecimal.ZERO);
        executionRecord.setStatus(failed + broken > 0 ? "failed" : "completed");
        executionRecord.setReportUrl("/api/reports/" + reportId + "/summary");
        testExecutionRecordMapper.updateExecutionRecord(executionRecord);
        
        ProjectExecutionResultDTO taskInfo = getProjectTaskStatus(taskId, userId);
        taskInfo.setStatus(TaskExecutionStatusEnum.COMPLETED.getCode());
        taskInfo.setStartTime(startTime);
        taskInfo.setEndTime(endTime);
        taskInfo.setTotalDuration(java.time.Duration.between(startTime, endTime).toMillis());
        taskInfo.setPassed(passed);
        taskInfo.setFailed(failed);
        taskInfo.setSkipped(skipped);
        taskInfo.setBroken(broken);
        taskInfo.setSuccessRate(testCases.size() > 0 ? BigDecimal.valueOf((double) passed / testCases.size() * 100) : BigDecimal.ZERO);
        taskInfo.setReportId(reportId);
        taskInfo.setSummaryUrl("/api/reports/" + reportId + "/summary");
        taskInfo.setDownloadUrl("/api/reports/" + reportId + "/export");
        
        updateProjectTaskStatus(taskId, TaskExecutionStatusEnum.COMPLETED.getCode(), "执行完成");
    }

    /**
     * 转换项目执行参数为用例执行参数
     */
    private ExecuteTestCaseDTO convertToTestCaseExecuteDTO(ExecuteProjectDTO executeDTO) {
        ExecuteTestCaseDTO caseExecuteDTO = new ExecuteTestCaseDTO();
        caseExecuteDTO.setEnvironment(executeDTO.getEnvironment());
        caseExecuteDTO.setBaseUrl(executeDTO.getBaseUrl());
        caseExecuteDTO.setTimeout(executeDTO.getTimeout());
        // 转换JsonNode为Map<String, Object>
        if (executeDTO.getAuthOverride() != null) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> authOverride = objectMapper.convertValue(executeDTO.getAuthOverride(), Map.class);
                caseExecuteDTO.setAuthOverride(authOverride);
            } catch (Exception e) {
                // 忽略转换错误
            }
        }
        // 转换Map<String, String>为Map<String, Object>
        if (executeDTO.getVariables() != null) {
            Map<String, Object> variables = new HashMap<>(executeDTO.getVariables());
            caseExecuteDTO.setVariables(variables);
        }
        caseExecuteDTO.setAsync(false); // 单个用例执行不使用异步
        
        return caseExecuteDTO;
    }

    /**
     * 创建项目测试报告汇总
     */
    private Long createProjectTestReportSummary(Project project, int totalCases, Integer userId) {
        TestReportSummary reportSummary = new TestReportSummary();
        String reportName = project.getName() + "_" + formatTimestamp(LocalDateTime.now());
        reportSummary.setReportName(reportName);
        reportSummary.setReportType(ReportTypeEnum.EXECUTION.getCode());
        reportSummary.setProjectId(project.getProjectId());
        reportSummary.setEnvironment("test");
        reportSummary.setStartTime(LocalDateTime.now());
        reportSummary.setEndTime(LocalDateTime.now());
        reportSummary.setDuration(0L);
        reportSummary.setTotalCases(totalCases);
        reportSummary.setExecutedCases(0);
        reportSummary.setPassedCases(0);
        reportSummary.setFailedCases(0);
        reportSummary.setBrokenCases(0);
        reportSummary.setSkippedCases(0);
        reportSummary.setSuccessRate(BigDecimal.valueOf(0.0));
        reportSummary.setTotalDuration(0L);
        reportSummary.setAvgDuration(0L);
        reportSummary.setMaxDuration(0L);
        reportSummary.setMinDuration(0L);
        reportSummary.setReportStatus(ReportStatusEnum.GENERATING.getCode());
        reportSummary.setFileFormat("html");
        reportSummary.setGeneratedBy(userId);
        
        testExecutionMapper.insertTestReportSummary(reportSummary);
        return reportSummary.getReportId();
    }

    private void recordProjectTestCaseResult(Long reportId, TestCase testCase, 
                                           ExecutionResultDTO result, Integer userId, Long executionRecordId) {
        TestCaseResult testCaseResult = new TestCaseResult();
        testCaseResult.setExecutionRecordId(executionRecordId);
        testCaseResult.setReportId(reportId);
        testCaseResult.setTaskType(TaskTypeEnum.TEST_CASE.getCode());
        testCaseResult.setRefId(testCase.getCaseId());
        testCaseResult.setFullName(testCase.getName());
        testCaseResult.setStatus(result.getStatus());
        testCaseResult.setDuration(result.getDuration());
        testCaseResult.setStartTime(LocalDateTime.now());
        testCaseResult.setEndTime(LocalDateTime.now());
        testCaseResult.setEnvironment("test");
        testCaseResult.setSeverity(normalizeSeverity(testCase.getSeverity()));
        testCaseResult.setPriority(normalizePriority(testCase.getPriority()));
        
        testCaseResult.setCaseId(testCase.getCaseId());
        testCaseResult.setCaseCode(testCase.getCaseCode());
        testCaseResult.setCaseName(testCase.getName());
        testCaseResult.setTestLayer("API");
        testCaseResult.setTestType("POSITIVE");
        testCaseResult.setFlakyCount(0);
        testCaseResult.setRetestResult("NOT_RETESTED");
        
        testExecutionMapper.insertTestCaseResult(testCaseResult);
    }

    private void recordProjectTestCaseFailure(Long reportId, TestCase testCase, String errorMessage, Integer userId, Long executionRecordId) {
        TestCaseResult testCaseResult = new TestCaseResult();
        testCaseResult.setExecutionRecordId(executionRecordId);
        testCaseResult.setReportId(reportId);
        testCaseResult.setTaskType(TaskTypeEnum.TEST_CASE.getCode());
        testCaseResult.setRefId(testCase.getCaseId());
        testCaseResult.setFullName(testCase.getName());
        testCaseResult.setStatus(ExecutionStatusEnum.FAILED.getCode());
        testCaseResult.setDuration(0L);
        testCaseResult.setStartTime(LocalDateTime.now());
        testCaseResult.setEndTime(LocalDateTime.now());
        testCaseResult.setFailureMessage(errorMessage);
        testCaseResult.setEnvironment("test");
        testCaseResult.setSeverity(normalizeSeverity(testCase.getSeverity()));
        testCaseResult.setPriority(normalizePriority(testCase.getPriority()));
        
        testCaseResult.setCaseId(testCase.getCaseId());
        testCaseResult.setCaseCode(testCase.getCaseCode());
        testCaseResult.setCaseName(testCase.getName());
        testCaseResult.setTestLayer("API");
        testCaseResult.setTestType("POSITIVE");
        testCaseResult.setFlakyCount(0);
        testCaseResult.setRetestResult("NOT_RETESTED");
        
        testExecutionMapper.insertTestCaseResult(testCaseResult);
    }

    /**
     * 更新项目任务状态
     */
    private void updateProjectTaskStatus(String taskId, String status, String message) {
        ProjectExecutionResultDTO taskInfo = projectTaskStatusCache.get(taskId);
        if (taskInfo != null) {
            taskInfo.setStatus(status);
            projectTaskStatusCache.put(taskId, taskInfo);
            
            try {
                redisComponent.setString(Constants.PROJECT_EXECUTION_QUEUE + ":" + taskId, 
                    objectMapper.writeValueAsString(taskInfo), Constants.PROJECT_RESULT_CACHE_HOURS * 3600);
            } catch (Exception e) {
                // 忽略JSON序列化错误
            }
        }
    }

    // ========== 接口执行相关方法 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiExecutionResultDTO executeApi(Integer apiId, ExecuteApiDTO executeDTO, Integer userId) {
        // 1. 验证接口
        Api api = validateApi(apiId);
        
        // 2. 查询和过滤测试用例
        List<TestCase> testCases = getFilteredApiTestCases(apiId, executeDTO);
        
        if (testCases.isEmpty()) {
            throw new RuntimeException("该接口下没有可执行的测试用例");
        }
        
        // 3. 设置默认参数
        setDefaultApiExecutionParams(executeDTO);
        
        // 4. 同步执行测试用例
        return executeApiTestCasesSync(api, testCases, executeDTO, userId);
    }

    @Override
    public ApiExecutionResultDTO executeApiAsync(Integer apiId, ExecuteApiDTO executeDTO, Integer userId) {
        // 1. 验证接口
        Api api = validateApi(apiId);
        
        // 2. 查询和过滤测试用例
        List<TestCase> testCases = getFilteredApiTestCases(apiId, executeDTO);
        
        if (testCases.isEmpty()) {
            throw new RuntimeException("该接口下没有可执行的测试用例");
        }
        
        // 3. 设置默认参数
        setDefaultApiExecutionParams(executeDTO);
        
        // 4. 生成任务ID
        String taskId = generateApiTaskId();
        
        // 5. 创建任务状态
        ApiExecutionResultDTO taskInfo = createApiTaskInfo(taskId, api, testCases, executeDTO);
        
        // 6. 缓存任务信息
        apiTaskStatusCache.put(taskId, taskInfo);
        try {
            redisComponent.setString(Constants.API_EXECUTION_QUEUE + ":" + taskId, 
                objectMapper.writeValueAsString(taskInfo), Constants.API_RESULT_CACHE_HOURS * 3600);
        } catch (Exception e) {
            // 忽略JSON序列化错误，继续执行
        }
        
        // 7. 异步执行
        CompletableFuture.runAsync(() -> {
            try {
                executeApiTestCasesAsync(taskId, api, testCases, executeDTO, userId);
            } catch (Exception e) {
                updateApiTaskStatus(taskId, TaskExecutionStatusEnum.FAILED.getCode(), 
                    "执行失败: " + e.getMessage());
            }
        }, executorService);
        
        return taskInfo;
    }

    @Override
    public ApiExecutionResultDTO getApiTaskStatus(String taskId, Integer userId) {
        // 1. 从缓存获取任务状态
        ApiExecutionResultDTO taskInfo = apiTaskStatusCache.get(taskId);
        if (taskInfo == null) {
            // 从Redis获取
            String taskJson = redisComponent.getString(Constants.API_EXECUTION_QUEUE + ":" + taskId);
            if (taskJson != null) {
                try {
                    taskInfo = objectMapper.readValue(taskJson, ApiExecutionResultDTO.class);
                } catch (Exception e) {
                    throw new RuntimeException("任务不存在");
                }
            } else {
                throw new RuntimeException("任务不存在");
            }
        }
        
        return taskInfo;
    }

    @Override
    public boolean cancelApiTask(String taskId, Integer userId) {
        ApiExecutionResultDTO taskInfo = getApiTaskStatus(taskId, userId);
        
        if (TaskExecutionStatusEnum.COMPLETED.getCode().equals(taskInfo.getStatus()) ||
            TaskExecutionStatusEnum.FAILED.getCode().equals(taskInfo.getStatus()) ||
            TaskExecutionStatusEnum.CANCELLED.getCode().equals(taskInfo.getStatus())) {
            return false;
        }
        
        // 更新任务状态为已取消
        updateApiTaskStatus(taskId, TaskExecutionStatusEnum.CANCELLED.getCode(), "任务已被用户取消");
        
        return true;
    }

    /**
     * 验证接口
     */
    private Api validateApi(Integer apiId) {
        Api api = testExecutionMapper.findApiById(apiId);
        if (api == null) {
            throw new RuntimeException("接口不存在");
        }
        
        if (!"active".equals(api.getStatus())) {
            throw new RuntimeException("接口已禁用，无法执行测试");
        }
        
        return api;
    }

    /**
     * 获取过滤后的接口测试用例
     */
    private List<TestCase> getFilteredApiTestCases(Integer apiId, ExecuteApiDTO executeDTO) {
        List<String> priorityList = null;
        List<String> tagsList = null;
        Boolean enabledOnly = true;
        String executionOrder = Constants.DEFAULT_EXECUTION_ORDER;
        
        if (executeDTO.getCaseFilter() != null) {
            priorityList = executeDTO.getCaseFilter().getPriority();
            tagsList = executeDTO.getCaseFilter().getTags();
            enabledOnly = executeDTO.getCaseFilter().getEnabledOnly();
        }
        
        if (executeDTO.getExecutionOrder() != null) {
            executionOrder = executeDTO.getExecutionOrder();
        }
        
        return testExecutionMapper.findTestCasesByApiId(apiId, priorityList, tagsList, enabledOnly, executionOrder);
    }

    /**
     * 设置默认接口执行参数
     */
    private void setDefaultApiExecutionParams(ExecuteApiDTO executeDTO) {
        if (executeDTO.getConcurrency() == null) {
            executeDTO.setConcurrency(Constants.API_DEFAULT_CONCURRENCY);
        }
        
        if (executeDTO.getConcurrency() > Constants.API_MAX_CONCURRENCY) {
            throw new RuntimeException("并发数不能超过" + Constants.API_MAX_CONCURRENCY);
        }
        
        if (executeDTO.getConcurrency() < Constants.API_MIN_CONCURRENCY) {
            executeDTO.setConcurrency(Constants.API_MIN_CONCURRENCY);
        }
        
        if (executeDTO.getAsync() == null) {
            executeDTO.setAsync(false); // 接口执行默认同步
        }
        
        if (executeDTO.getTimeout() == null) {
            executeDTO.setTimeout(Constants.DEFAULT_EXECUTION_TIMEOUT);
        }
        
        if (executeDTO.getEnvironment() == null) {
            executeDTO.setEnvironment(Constants.DEFAULT_ENVIRONMENT);
        }
        
        if (executeDTO.getExecutionOrder() == null) {
            executeDTO.setExecutionOrder(Constants.DEFAULT_EXECUTION_ORDER);
        }
    }

    /**
     * 生成接口任务ID
     */
    private String generateApiTaskId() {
        return Constants.API_TASK_PREFIX + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 创建接口任务信息
     */
    private ApiExecutionResultDTO createApiTaskInfo(String taskId, Api api, 
                                                   List<TestCase> testCases, ExecuteApiDTO executeDTO) {
        ApiExecutionResultDTO taskInfo = new ApiExecutionResultDTO();
        taskInfo.setTaskId(taskId);
        taskInfo.setApiId(api.getApiId());
        taskInfo.setApiName(api.getName());
        taskInfo.setApiMethod(api.getMethod());
        taskInfo.setApiPath(api.getPath());
        taskInfo.setTotalCases(testCases.size());
        taskInfo.setFilteredCases(testCases.size());
        taskInfo.setStatus(TaskExecutionStatusEnum.QUEUED.getCode());
        taskInfo.setConcurrency(executeDTO.getConcurrency());
        taskInfo.setEstimatedDuration(testCases.size() * Constants.API_ESTIMATED_TIME_PER_CASE);
        taskInfo.setQueuePosition(1);
        taskInfo.setMonitorUrl("/api/tasks/" + taskId + "/status");
        taskInfo.setReportUrl("/api/reports/api/" + api.getApiId() + "/executions/latest");
        
        return taskInfo;
    }

    /**
     * 同步执行接口测试用例
     */
    private ApiExecutionResultDTO executeApiTestCasesSync(Api api, List<TestCase> testCases, 
                                                         ExecuteApiDTO executeDTO, Integer userId) {
        LocalDateTime startTime = LocalDateTime.now();
        
        // 初始化共享变量池
        String executionId = "api_" + api.getApiId() + "_" + System.currentTimeMillis();
        variablePoolService.initializePool(executionId);
        
        // 使用依赖解析服务对测试用例排序
        List<TestCase> sortedTestCases = dependencyResolverService.sortTestCasesByDependency(testCases);
        List<List<TestCase>> executionLayers = dependencyResolverService.getExecutionLayers(testCases);
        
        log.info("接口测试执行: apiId={}, 总用例数={}, 执行层数={}", 
                api.getApiId(), testCases.size(), executionLayers.size());
        
        // 1. 创建接口级别的TestExecutionRecord（总记录）
        TestExecutionRecord apiExecutionRecord = new TestExecutionRecord();
        apiExecutionRecord.setExecutionScope("api");
        apiExecutionRecord.setRefId(api.getApiId());
        apiExecutionRecord.setScopeName(api.getName());
        apiExecutionRecord.setExecutedBy(userId);
        apiExecutionRecord.setExecutionType(executeDTO.getExecutionType() != null ? executeDTO.getExecutionType() : "manual");
        apiExecutionRecord.setEnvironment(executeDTO.getEnvironment() != null ? executeDTO.getEnvironment() : "test");
        apiExecutionRecord.setStatus("running");
        apiExecutionRecord.setStartTime(startTime);
        apiExecutionRecord.setTotalCases(sortedTestCases.size());
        apiExecutionRecord.setExecutedCases(0);
        apiExecutionRecord.setPassedCases(0);
        apiExecutionRecord.setFailedCases(0);
        apiExecutionRecord.setSkippedCases(0);
        apiExecutionRecord.setSuccessRate(BigDecimal.ZERO);
        apiExecutionRecord.setIsDeleted(false);
        
        testExecutionRecordMapper.insertExecutionRecord(apiExecutionRecord);
        Long executionRecordId = apiExecutionRecord.getRecordId();
        
        // 2. 创建测试报告汇总
        Long reportId = createApiTestReportSummary(api, sortedTestCases.size(), userId);
        
        String reportName = null;
        TestReportSummary reportSummary = testExecutionMapper.findTestReportSummaryById(reportId);
        if (reportSummary != null) {
            reportName = reportSummary.getReportName();
        }
        
        List<ApiExecutionResultDTO.CaseResult> caseResults = new ArrayList<>();
        
        final Map<String, Object>[] responseDataHolder = new Map[]{null};
        
        for (List<TestCase> layer : executionLayers) {
            log.info("执行第{}层测试用例, 用例数: {}", executionLayers.indexOf(layer) + 1, layer.size());
            
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            
            for (TestCase testCase : layer) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        ExecuteTestCaseDTO caseExecuteDTO = convertToTestCaseExecuteDTO(executeDTO);
                        caseExecuteDTO.setExecutionId(executionId);
                        
                        ExecutionResultDTO result = executeTestCaseWithVariables(
                            testCase.getCaseId(), caseExecuteDTO, userId, executionId,
                            executionRecordId, reportId);
                        
                        // 注意：executeTestCaseWithVariables 方法内部已经插入了 TestCaseResult
                        // 这里不再重复插入，避免数据重复
                        
                        if (result.getExtractedVariables() != null) {
                            variablePoolService.setVariables(executionId, result.getExtractedVariables());
                            log.info("提取变量到共享池: caseId={}, variables={}", 
                                    testCase.getCaseId(), result.getExtractedVariables().keySet());
                        }
                        
                        synchronized (responseDataHolder) {
                            if (responseDataHolder[0] == null && result.getResponseStatus() != null) {
                                responseDataHolder[0] = new HashMap<>();
                                responseDataHolder[0].put("httpStatus", result.getResponseStatus());
                                responseDataHolder[0].put("responseCode", result.getStatus());
                                responseDataHolder[0].put("body", result.getResponseBody());
                                responseDataHolder[0].put("headers", result.getResponseHeaders());
                                responseDataHolder[0].put("assertionResults", result.getAssertionDetails());
                                responseDataHolder[0].put("extractedVariables", result.getExtractedVariables());
                            }
                        }
                        
                        ApiExecutionResultDTO.CaseResult caseResult = new ApiExecutionResultDTO.CaseResult();
                        caseResult.setCaseId(testCase.getCaseId());
                        caseResult.setCaseCode(testCase.getCaseCode());
                        caseResult.setCaseName(testCase.getName());
                        caseResult.setStatus(result.getStatus());
                        caseResult.setDuration(result.getDuration());
                        caseResult.setResponseStatus(result.getResponseStatus());
                        if (ExecutionStatusEnum.FAILED.getCode().equals(result.getStatus())) {
                            caseResult.setFailureMessage(result.getFailureMessage());
                        }
                        caseResult.setLogsLink("/api/test-results/" + result.getExecutionId() + "/logs");
                        
                        synchronized (caseResults) {
                            caseResults.add(caseResult);
                        }
                        
                    } catch (Exception e) {
                        recordApiTestCaseFailure(reportId, executionRecordId, testCase, e.getMessage(), userId);
                        
                        ApiExecutionResultDTO.CaseResult caseResult = new ApiExecutionResultDTO.CaseResult();
                        caseResult.setCaseId(testCase.getCaseId());
                        caseResult.setCaseCode(testCase.getCaseCode());
                        caseResult.setCaseName(testCase.getName());
                        caseResult.setStatus(ExecutionStatusEnum.FAILED.getCode());
                        caseResult.setDuration(0L);
                        caseResult.setFailureMessage(e.getMessage());
                        
                        synchronized (caseResults) {
                            caseResults.add(caseResult);
                        }
                    }
                }, executorService);
                futures.add(future);
            }
            
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
        
        variablePoolService.clearPool(executionId);
        
        // 关键修复：执行一个查询来刷新 MyBatis 的一级缓存，确保主线程能看到异步线程插入的数据
        // 通过查询 TestCaseResults 表来触发缓存刷新
        testExecutionMapper.findTestCaseResultsByReportId(reportId);
        
        LocalDateTime endTime = LocalDateTime.now();
        
        // 关键修复：直接从 caseResults 列表计算统计结果，而不是调用 updateReportSummaryStats
        // 因为异步线程使用独立的数据库连接，updateReportSummaryStats 查询数据库会返回0
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        int broken = 0;
        
        synchronized (caseResults) {
            for (ApiExecutionResultDTO.CaseResult cr : caseResults) {
                if ("passed".equals(cr.getStatus())) {
                    passed++;
                } else if ("failed".equals(cr.getStatus())) {
                    failed++;
                } else if ("skipped".equals(cr.getStatus())) {
                    skipped++;
                } else if ("broken".equals(cr.getStatus())) {
                    broken++;
                }
            }
        }
        
        log.info("API测试统计结果(从caseResults计算): reportId={}, passed={}, failed={}, skipped={}, broken={}, totalCases={}", 
                reportId, passed, failed, skipped, broken, testCases.size());
        
        // 直接从 caseResults 计算统计结果并更新 TestReportSummaries 表
        // 不再调用 updateReportSummaryStats，因为该方法查询数据库会返回0
        int total = testCases.size();
        BigDecimal successRate = total > 0 ? BigDecimal.valueOf((double) passed / total * 100).setScale(2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        // 复用之前查询的 reportSummary 变量
        if (reportSummary != null) {
            reportSummary.setExecutedCases(total);
            reportSummary.setPassedCases(passed);
            reportSummary.setFailedCases(failed);
            reportSummary.setBrokenCases(broken);
            reportSummary.setSkippedCases(skipped);
            reportSummary.setSuccessRate(successRate);
            reportSummary.setStartTime(startTime);
            reportSummary.setEndTime(endTime);
            reportSummary.setDuration(java.time.Duration.between(startTime, endTime).toMillis());
            reportSummary.setReportStatus(failed > 0 || broken > 0 ? "failed" : "completed");
            reportSummary.setUpdatedAt(LocalDateTime.now());
            testExecutionMapper.updateTestReportSummary(reportSummary);
            log.info("更新测试报告统计: reportId={}, total={}, passed={}, failed={}, broken={}, skipped={}, successRate={}%", 
                    reportId, total, passed, failed, broken, skipped, successRate);
        }
        
        // 调试日志：输出统计结果
        log.info("API测试统计结果: reportId={}, passed={}, failed={}, skipped={}, broken={}, totalCases={}", 
                reportId, passed, failed, skipped, broken, testCases.size());
        log.info("API测试 recordId={}, 当前passedCases={}, failedCases={}", 
                apiExecutionRecord.getRecordId(), passed, failed + broken);
        
        // 5. 更新接口级别的TestExecutionRecord统计信息
        apiExecutionRecord.setEndTime(endTime);
        apiExecutionRecord.setDurationSeconds((int)java.time.Duration.between(startTime, endTime).toSeconds());
        apiExecutionRecord.setExecutedCases(caseResults.size());
        apiExecutionRecord.setPassedCases(passed);
        apiExecutionRecord.setFailedCases(failed + broken);
        apiExecutionRecord.setSkippedCases(skipped);
        apiExecutionRecord.setSuccessRate(testCases.size() > 0 ? 
            BigDecimal.valueOf((double) passed / testCases.size() * 100).setScale(2, java.math.RoundingMode.HALF_UP) : 
            BigDecimal.ZERO);
        apiExecutionRecord.setStatus(failed + broken > 0 ? "failed" : "completed");
        apiExecutionRecord.setReportUrl("/api/reports/" + reportId);
        
        // 保存响应数据到executionConfig中，用于前端展示
        if (responseDataHolder[0] != null) {
            try {
                Map<String, Object> executionConfig = new HashMap<>();
                executionConfig.put("responseData", responseDataHolder[0]);
                apiExecutionRecord.setExecutionConfig(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(executionConfig));
            } catch (Exception e) {
                log.warn("保存响应数据到executionConfig失败: {}", e.getMessage());
            }
        }
        
        testExecutionRecordMapper.updateExecutionRecord(apiExecutionRecord);
        
        // 调试日志：确认更新后的值
        log.info("API测试更新完成: recordId={}, passedCases={}, failedCases={}, status={}", 
                apiExecutionRecord.getRecordId(), apiExecutionRecord.getPassedCases(), 
                apiExecutionRecord.getFailedCases(), apiExecutionRecord.getStatus());
        
        // 6. 构建汇总统计
        Map<String, Object> summary = new HashMap<>();
        Map<String, Object> byPriority = new HashMap<>();
        Map<String, Object> byStatus = new HashMap<>();
        
        byStatus.put("passed", passed);
        byStatus.put("failed", failed);
        byStatus.put("skipped", skipped);
        byStatus.put("broken", broken);
        
        summary.put("by_priority", byPriority);
        summary.put("by_status", byStatus);
        
        // 7. 构建返回结果
        ApiExecutionResultDTO result = new ApiExecutionResultDTO();
        result.setExecutionId(executionRecordId);
        result.setApiId(api.getApiId());
        result.setApiName(api.getName());
        result.setApiMethod(api.getMethod());
        result.setApiPath(api.getPath());
        result.setStartTime(startTime);
        result.setEndTime(endTime);
        result.setTotalDuration(java.time.Duration.between(startTime, endTime).toMillis());
        result.setTotalCases(testCases.size());
        result.setPassed(passed);
        result.setFailed(failed);
        result.setSkipped(skipped);
        result.setBroken(broken);
        result.setSuccessRate(testCases.size() > 0 ? BigDecimal.valueOf((double) passed / testCases.size() * 100) : BigDecimal.ZERO);
        result.setCaseResults(caseResults);
        result.setSummary(summary);
        result.setReportId(reportId);
        result.setReportName(reportName);
        result.setDetailUrl("/api/test-results/" + executionRecordId + "/details");
        
        // 添加执行信息（新增）
        result.setExecutionScope("api");
        result.setExecutionType(executeDTO != null && executeDTO.getExecutionType() != null ? executeDTO.getExecutionType() : "manual");
        result.setEnvironment(executeDTO != null && executeDTO.getEnvironment() != null ? executeDTO.getEnvironment() : "test");
        
        return result;
    }

    /**
     * 异步执行接口测试用例
     */
    private void executeApiTestCasesAsync(String taskId, Api api, List<TestCase> testCases, 
                                         ExecuteApiDTO executeDTO, Integer userId) {
        updateApiTaskStatus(taskId, TaskExecutionStatusEnum.RUNNING.getCode(), "开始执行测试用例");
        
        LocalDateTime startTime = LocalDateTime.now();
        
        // 1. 创建接口级别的TestExecutionRecord（总记录）
        TestExecutionRecord apiExecutionRecord = new TestExecutionRecord();
        apiExecutionRecord.setExecutionScope("api");
        apiExecutionRecord.setRefId(api.getApiId());
        apiExecutionRecord.setScopeName(api.getName());
        apiExecutionRecord.setExecutedBy(userId);
        apiExecutionRecord.setExecutionType(executeDTO.getExecutionType() != null ? executeDTO.getExecutionType() : "manual");
        apiExecutionRecord.setEnvironment(executeDTO.getEnvironment() != null ? executeDTO.getEnvironment() : "test");
        apiExecutionRecord.setStatus("running");
        apiExecutionRecord.setStartTime(startTime);
        apiExecutionRecord.setTotalCases(testCases.size());
        apiExecutionRecord.setExecutedCases(0);
        apiExecutionRecord.setPassedCases(0);
        apiExecutionRecord.setFailedCases(0);
        apiExecutionRecord.setSkippedCases(0);
        apiExecutionRecord.setSuccessRate(BigDecimal.ZERO);
        apiExecutionRecord.setIsDeleted(false);
        
        testExecutionRecordMapper.insertExecutionRecord(apiExecutionRecord);
        Long executionRecordId = apiExecutionRecord.getRecordId();
        
        Long reportId = createApiTestReportSummary(api, testCases.size(), userId);
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (TestCase testCase : testCases) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    ExecuteTestCaseDTO caseExecuteDTO = convertToTestCaseExecuteDTO(executeDTO);
                    ExecutionResultDTO result = executeTestCaseWithVariables(testCase.getCaseId(), caseExecuteDTO, userId, null,
                        executionRecordId, reportId);
                    // 保存测试用例执行结果到数据库
                    recordApiTestCaseResult(reportId, executionRecordId, testCase, result, userId);
                } catch (Exception e) {
                    recordApiTestCaseFailure(reportId, executionRecordId, testCase, e.getMessage(), userId);
                }
            }, executorService);
            futures.add(future);
        }
        
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        LocalDateTime endTime = LocalDateTime.now();
        
        updateReportSummaryStats(reportId, startTime, endTime);
        
        Map<String, Object> stats = testExecutionMapper.countResultsByReportId(reportId);
        
        // 关键修复：由于异步线程使用独立的数据库连接，主线程可能看不到异步线程插入的数据
        // 使用重试机制确保能够查询到数据
        int maxRetries = 3;
        int retryDelayMs = 100;
        
        for (int retry = 0; retry < maxRetries; retry++) {
            if (retry > 0) {
                try {
                    Thread.sleep(retryDelayMs * retry);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            stats = testExecutionMapper.countResultsByReportId(reportId);
            if (stats != null && stats.get("total") != null && ((Number) stats.get("total")).intValue() > 0) {
                break;
            }
            log.warn("统计查询为空, 重试 {}/{}", retry + 1, maxRetries);
        }
        
        int passed = stats != null && stats.get("passed") != null ? ((Number) stats.get("passed")).intValue() : 0;
        int failed = stats != null && stats.get("failed") != null ? ((Number) stats.get("failed")).intValue() : 0;
        int skipped = stats != null && stats.get("skipped") != null ? ((Number) stats.get("skipped")).intValue() : 0;
        int broken = stats != null && stats.get("broken") != null ? ((Number) stats.get("broken")).intValue() : 0;
        
        // 更新执行记录的统计数据
        apiExecutionRecord.setEndTime(endTime);
        apiExecutionRecord.setDurationSeconds((int) java.time.Duration.between(startTime, endTime).toSeconds());
        apiExecutionRecord.setExecutedCases(passed + failed + skipped + broken);
        apiExecutionRecord.setPassedCases(passed);
        apiExecutionRecord.setFailedCases(failed + broken);
        apiExecutionRecord.setSkippedCases(skipped);
        apiExecutionRecord.setSuccessRate(testCases.size() > 0 ? 
            BigDecimal.valueOf((double) passed / testCases.size() * 100).setScale(2, java.math.RoundingMode.HALF_UP) : 
            BigDecimal.ZERO);
        apiExecutionRecord.setStatus(failed + broken > 0 ? "failed" : "completed");
        apiExecutionRecord.setReportUrl("/api/reports/" + reportId);
        testExecutionRecordMapper.updateExecutionRecord(apiExecutionRecord);
        
        ApiExecutionResultDTO taskInfo = getApiTaskStatus(taskId, userId);
        taskInfo.setStatus(TaskExecutionStatusEnum.COMPLETED.getCode());
        taskInfo.setStartTime(startTime);
        taskInfo.setEndTime(endTime);
        taskInfo.setTotalDuration(java.time.Duration.between(startTime, endTime).toMillis());
        taskInfo.setPassed(passed);
        taskInfo.setFailed(failed);
        taskInfo.setSkipped(skipped);
        taskInfo.setBroken(broken);
        taskInfo.setSuccessRate(testCases.size() > 0 ? BigDecimal.valueOf((double) passed / testCases.size() * 100) : BigDecimal.ZERO);
        taskInfo.setReportId(reportId);
        taskInfo.setDetailUrl("/api/test-results/" + taskInfo.getExecutionId() + "/details");
        
        updateApiTaskStatus(taskId, TaskExecutionStatusEnum.COMPLETED.getCode(), "执行完成");
    }

    /**
     * 转换接口执行参数为用例执行参数
     */
    private ExecuteTestCaseDTO convertToTestCaseExecuteDTO(ExecuteApiDTO executeDTO) {
        ExecuteTestCaseDTO caseExecuteDTO = new ExecuteTestCaseDTO();
        caseExecuteDTO.setEnvironment(executeDTO.getEnvironment());
        caseExecuteDTO.setBaseUrl(executeDTO.getBaseUrl());
        caseExecuteDTO.setTimeout(executeDTO.getTimeout());
        // 转换JsonNode为Map<String, Object>
        if (executeDTO.getAuthOverride() != null) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> authOverride = objectMapper.convertValue(executeDTO.getAuthOverride(), Map.class);
                caseExecuteDTO.setAuthOverride(authOverride);
            } catch (Exception e) {
                // 忽略转换错误
            }
        }
        // 转换Map<String, String>为Map<String, Object>
        if (executeDTO.getVariables() != null) {
            Map<String, Object> variables = new HashMap<>(executeDTO.getVariables());
            caseExecuteDTO.setVariables(variables);
        }
        caseExecuteDTO.setAsync(false); // 单个用例执行不使用异步
        
        return caseExecuteDTO;
    }

    /**
     * 创建接口测试报告汇总
     */
    private Long createApiTestReportSummary(Api api, int totalCases, Integer userId) {
        TestReportSummary reportSummary = new TestReportSummary();
        String reportName = api.getName() + "_" + formatTimestamp(LocalDateTime.now());
        reportSummary.setReportName(reportName);
        reportSummary.setReportType(ReportTypeEnum.EXECUTION.getCode());
        reportSummary.setExecutionId(System.currentTimeMillis()); // 使用时间戳作为执行ID
        
        // 通过接口ID查询所属项目ID
        Integer projectId = testExecutionMapper.findProjectIdByApiId(api.getApiId());
        reportSummary.setProjectId(projectId != null ? projectId : 1); // 如果查询不到，使用默认值
        
        reportSummary.setEnvironment("test");
        reportSummary.setStartTime(LocalDateTime.now());
        reportSummary.setEndTime(LocalDateTime.now());
        reportSummary.setDuration(0L);
        reportSummary.setTotalCases(totalCases);
        reportSummary.setExecutedCases(0);
        reportSummary.setPassedCases(0);
        reportSummary.setFailedCases(0);
        reportSummary.setBrokenCases(0);
        reportSummary.setSkippedCases(0);
        reportSummary.setSuccessRate(BigDecimal.valueOf(0.0));
        reportSummary.setTotalDuration(0L);
        reportSummary.setAvgDuration(0L);
        reportSummary.setMaxDuration(0L);
        reportSummary.setMinDuration(0L);
        reportSummary.setReportStatus(ReportStatusEnum.GENERATING.getCode());
        reportSummary.setFileFormat("html");
        reportSummary.setGeneratedBy(userId);
        reportSummary.setCreatedAt(LocalDateTime.now());
        reportSummary.setUpdatedAt(LocalDateTime.now());
        reportSummary.setIsDeleted(false);
        
        testExecutionMapper.insertTestReportSummary(reportSummary);
        return reportSummary.getReportId();
    }

    /**
     * 记录接口测试用例结果
     */
    private void recordApiTestCaseResult(Long reportId, Long executionRecordId, TestCase testCase, 
                                       ExecutionResultDTO result, Integer userId) {
        // 创建测试结果记录，关联接口级别的execution_record_id
        TestCaseResult testCaseResult = new TestCaseResult();
        testCaseResult.setExecutionRecordId(executionRecordId);
        testCaseResult.setReportId(reportId);
        testCaseResult.setTaskType(TaskTypeEnum.TEST_CASE.getCode());
        testCaseResult.setRefId(testCase.getCaseId());
        testCaseResult.setFullName(testCase.getName());
        testCaseResult.setStatus(result.getStatus());
        testCaseResult.setDuration(result.getDuration());
        testCaseResult.setStartTime(result.getStartTime() != null ? result.getStartTime() : LocalDateTime.now());
        testCaseResult.setEndTime(result.getEndTime() != null ? result.getEndTime() : LocalDateTime.now());
        testCaseResult.setFailureMessage(result.getFailureMessage());
        testCaseResult.setEnvironment("test");
        testCaseResult.setSeverity(normalizeSeverity(testCase.getSeverity()));
        testCaseResult.setPriority(normalizePriority(testCase.getPriority()));
        testCaseResult.setIsDeleted(false);
        
        // 设置新增字段
        testCaseResult.setCaseId(testCase.getCaseId());
        testCaseResult.setCaseCode(testCase.getCaseCode());
        testCaseResult.setCaseName(testCase.getName());
        testCaseResult.setTestLayer("API");
        testCaseResult.setTestType("POSITIVE");
        testCaseResult.setFlakyCount(0);
        testCaseResult.setRetestResult("NOT_RETESTED");
        
        testExecutionMapper.insertTestCaseResult(testCaseResult);
    }

    /**
     * 记录接口测试用例失败
     */
    private void recordApiTestCaseFailure(Long reportId, Long executionRecordId, TestCase testCase, String errorMessage, Integer userId) {
        // 创建测试结果记录，关联接口级别的execution_record_id
        TestCaseResult testCaseResult = new TestCaseResult();
        testCaseResult.setExecutionRecordId(executionRecordId);
        testCaseResult.setReportId(reportId);
        testCaseResult.setTaskType(TaskTypeEnum.TEST_CASE.getCode());
        testCaseResult.setRefId(testCase.getCaseId());
        testCaseResult.setFullName(testCase.getName());
        testCaseResult.setStatus(ExecutionStatusEnum.FAILED.getCode());
        testCaseResult.setDuration(0L);
        testCaseResult.setStartTime(LocalDateTime.now());
        testCaseResult.setEndTime(LocalDateTime.now());
        testCaseResult.setFailureMessage(errorMessage);
        testCaseResult.setEnvironment("test");
        testCaseResult.setSeverity(normalizeSeverity(testCase.getSeverity()));
        testCaseResult.setPriority(normalizePriority(testCase.getPriority()));
        testCaseResult.setIsDeleted(false);
        
        testCaseResult.setCaseId(testCase.getCaseId());
        testCaseResult.setCaseCode(testCase.getCaseCode());
        testCaseResult.setCaseName(testCase.getName());
        testCaseResult.setTestLayer("API");
        testCaseResult.setTestType("POSITIVE");
        testCaseResult.setFlakyCount(0);
        testCaseResult.setRetestResult("NOT_RETESTED");
        
        testExecutionMapper.insertTestCaseResult(testCaseResult);
    }

    /**
     * 更新接口任务状态
     */
    private void updateApiTaskStatus(String taskId, String status, String message) {
        ApiExecutionResultDTO taskInfo = apiTaskStatusCache.get(taskId);
        if (taskInfo != null) {
            taskInfo.setStatus(status);
            apiTaskStatusCache.put(taskId, taskInfo);
            
            try {
                redisComponent.setString(Constants.API_EXECUTION_QUEUE + ":" + taskId, 
                    objectMapper.writeValueAsString(taskInfo), Constants.API_RESULT_CACHE_HOURS * 3600);
            } catch (Exception e) {
                // 忽略JSON序列化错误
            }
        }
    }

    // ========== 测试套件执行相关方法 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TestSuiteExecutionResultDTO executeTestSuite(Integer suiteId, ExecuteTestSuiteDTO executeDTO, Integer userId) {
        // 1. 验证测试套件
        TestSuite testSuite = validateTestSuite(suiteId);
        
        // 2. 查询和过滤测试用例
        List<TestCase> testCases = getFilteredSuiteTestCases(suiteId, executeDTO);
        
        if (testCases.isEmpty()) {
            throw new RuntimeException("该测试套件下没有可执行的测试用例");
        }
        
        // 3. 设置默认参数
        setDefaultSuiteExecutionParams(executeDTO);
        
        // 4. 同步执行测试用例
        return executeSuiteTestCasesSync(testSuite, testCases, executeDTO, userId);
    }

    @Override
    public TestSuiteExecutionResultDTO executeTestSuiteAsync(Integer suiteId, ExecuteTestSuiteDTO executeDTO, Integer userId) {
        // 1. 验证测试套件
        TestSuite testSuite = validateTestSuite(suiteId);
        
        // 2. 查询和过滤测试用例
        List<TestCase> testCases = getFilteredSuiteTestCases(suiteId, executeDTO);
        
        if (testCases.isEmpty()) {
            throw new RuntimeException("该测试套件下没有可执行的测试用例");
        }
        
        // 3. 设置默认参数
        setDefaultSuiteExecutionParams(executeDTO);
        
        // 4. 生成任务ID
        String taskId = generateSuiteTaskId();
        
        // 5. 创建任务状态
        TestSuiteExecutionResultDTO taskInfo = createSuiteTaskInfo(taskId, testSuite, testCases, executeDTO);
        
        // 6. 缓存任务信息
        suiteTaskStatusCache.put(taskId, taskInfo);
        try {
            redisComponent.setString(Constants.SUITE_EXECUTION_QUEUE + ":" + taskId, 
                objectMapper.writeValueAsString(taskInfo), Constants.SUITE_RESULT_CACHE_HOURS * 3600);
        } catch (Exception e) {
            // 忽略JSON序列化错误，继续执行
        }
        
        // 7. 异步执行
        CompletableFuture.runAsync(() -> {
            try {
                executeSuiteTestCasesAsync(taskId, testSuite, testCases, executeDTO, userId);
            } catch (Exception e) {
                updateSuiteTaskStatus(taskId, TaskExecutionStatusEnum.FAILED.getCode(), 
                    "执行失败: " + e.getMessage());
            }
        }, executorService);
        
        return taskInfo;
    }

    @Override
    public TestSuiteExecutionResultDTO getTestSuiteTaskStatus(String taskId, Integer userId) {
        // 1. 从缓存获取任务状态
        TestSuiteExecutionResultDTO taskInfo = suiteTaskStatusCache.get(taskId);
        if (taskInfo == null) {
            // 从Redis获取
            String taskJson = redisComponent.getString(Constants.SUITE_EXECUTION_QUEUE + ":" + taskId);
            if (taskJson != null) {
                try {
                    taskInfo = objectMapper.readValue(taskJson, TestSuiteExecutionResultDTO.class);
                } catch (Exception e) {
                    throw new RuntimeException("任务不存在");
                }
            } else {
                throw new RuntimeException("任务不存在");
            }
        }
        
        return taskInfo;
    }

    @Override
    public boolean cancelTestSuiteTask(String taskId, Integer userId) {
        TestSuiteExecutionResultDTO taskInfo = getTestSuiteTaskStatus(taskId, userId);
        
        if (TaskExecutionStatusEnum.COMPLETED.getCode().equals(taskInfo.getStatus()) ||
            TaskExecutionStatusEnum.FAILED.getCode().equals(taskInfo.getStatus()) ||
            TaskExecutionStatusEnum.CANCELLED.getCode().equals(taskInfo.getStatus())) {
            return false;
        }
        
        // 更新任务状态为已取消
        updateSuiteTaskStatus(taskId, TaskExecutionStatusEnum.CANCELLED.getCode(), "任务已被用户取消");
        
        return true;
    }

    /**
     * 验证测试套件
     */
    private TestSuite validateTestSuite(Integer suiteId) {
        TestSuite testSuite = testExecutionMapper.findTestSuiteById(suiteId);
        if (testSuite == null) {
            throw new RuntimeException("测试套件不存在");
        }
        
        if (!"active".equals(testSuite.getStatus())) {
            throw new RuntimeException("测试套件已禁用，无法执行");
        }
        
        return testSuite;
    }

    /**
     * 获取过滤后的测试套件测试用例
     */
    private List<TestCase> getFilteredSuiteTestCases(Integer suiteId, ExecuteTestSuiteDTO executeDTO) {
        List<String> priorityList = null;
        List<String> tagsList = null;
        Boolean enabledOnly = true;
        
        if (executeDTO.getCaseFilter() != null) {
            priorityList = executeDTO.getCaseFilter().getPriority();
            tagsList = executeDTO.getCaseFilter().getTags();
            enabledOnly = executeDTO.getCaseFilter().getEnabledOnly();
        }
        
        return testExecutionMapper.findTestCasesBySuiteId(suiteId, priorityList, tagsList, enabledOnly);
    }

    /**
     * 设置默认测试套件执行参数
     */
    private void setDefaultSuiteExecutionParams(ExecuteTestSuiteDTO executeDTO) {
        if (executeDTO.getConcurrency() == null) {
            executeDTO.setConcurrency(Constants.SUITE_DEFAULT_CONCURRENCY);
        }
        
        if (executeDTO.getConcurrency() > Constants.SUITE_MAX_CONCURRENCY) {
            throw new RuntimeException("并发数不能超过" + Constants.SUITE_MAX_CONCURRENCY);
        }
        
        if (executeDTO.getConcurrency() < Constants.SUITE_MIN_CONCURRENCY) {
            executeDTO.setConcurrency(Constants.SUITE_MIN_CONCURRENCY);
        }
        
        if (executeDTO.getAsync() == null) {
            executeDTO.setAsync(true); // 测试套件执行默认异步
        }
        
        if (executeDTO.getTimeout() == null) {
            executeDTO.setTimeout(Constants.DEFAULT_EXECUTION_TIMEOUT);
        }
        
        if (executeDTO.getEnvironment() == null) {
            executeDTO.setEnvironment(Constants.DEFAULT_ENVIRONMENT);
        }
        
        if (executeDTO.getExecutionStrategy() == null) {
            executeDTO.setExecutionStrategy(Constants.DEFAULT_SUITE_EXECUTION_STRATEGY);
        }
        
        if (executeDTO.getStopOnFailure() == null) {
            executeDTO.setStopOnFailure(false);
        }
        
        // 设置重试配置默认值
        if (executeDTO.getRetryConfig() == null) {
            executeDTO.setRetryConfig(new ExecuteTestSuiteDTO.RetryConfig());
        }
        
        if (executeDTO.getRetryConfig().getMaxAttempts() == null) {
            executeDTO.getRetryConfig().setMaxAttempts(Constants.DEFAULT_MAX_RETRY_ATTEMPTS);
        }
        
        if (executeDTO.getRetryConfig().getDelayMs() == null) {
            executeDTO.getRetryConfig().setDelayMs(Constants.DEFAULT_RETRY_DELAY_MS);
        }
        
        // 设置报告配置默认值
        if (executeDTO.getReportConfig() == null) {
            executeDTO.setReportConfig(new ExecuteTestSuiteDTO.ReportConfig());
        }
    }

    /**
     * 生成测试套件任务ID
     */
    private String generateSuiteTaskId() {
        return Constants.SUITE_TASK_PREFIX + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 创建测试套件任务信息
     */
    private TestSuiteExecutionResultDTO createSuiteTaskInfo(String taskId, TestSuite testSuite, 
                                                           List<TestCase> testCases, ExecuteTestSuiteDTO executeDTO) {
        TestSuiteExecutionResultDTO taskInfo = new TestSuiteExecutionResultDTO();
        taskInfo.setTaskId(taskId);
        taskInfo.setSuiteId(testSuite.getSuiteId());
        taskInfo.setSuiteName(testSuite.getName());
        taskInfo.setSuiteCode(testSuite.getSuiteCode());
        taskInfo.setTotalCases(testCases.size());
        taskInfo.setFilteredCases(testCases.size());
        taskInfo.setEstimatedCases(testCases.size());
        taskInfo.setStatus(TaskExecutionStatusEnum.QUEUED.getCode());
        taskInfo.setConcurrency(executeDTO.getConcurrency());
        taskInfo.setEstimatedDuration(testCases.size() * Constants.SUITE_ESTIMATED_TIME_PER_CASE);
        taskInfo.setQueuePosition(1);
        taskInfo.setExecutionPlanUrl("/api/tasks/" + taskId + "/plan");
        taskInfo.setMonitorUrl("/api/tasks/" + taskId + "/status");
        taskInfo.setReportUrl("/api/reports/suites/" + testSuite.getSuiteId() + "/executions/latest");
        taskInfo.setCancelUrl("/api/tasks/" + taskId + "/cancel");
        
        return taskInfo;
    }

    /**
     * 同步执行测试套件测试用例
     */
    private TestSuiteExecutionResultDTO executeSuiteTestCasesSync(TestSuite testSuite, List<TestCase> testCases, 
                                                                 ExecuteTestSuiteDTO executeDTO, Integer userId) {
        LocalDateTime startTime = LocalDateTime.now();
        
        TestExecutionRecord executionRecord = createExecutionRecord("suite", testSuite.getSuiteId(), 
            testSuite.getName(), userId, executeDTO.getExecutionType(), executeDTO.getEnvironment());
        executionRecord.setTotalCases(testCases.size());
        testExecutionRecordMapper.insertExecutionRecord(executionRecord);
        
        Long reportId = createSuiteTestReportSummary(testSuite, testCases.size(), userId);
        
        if (Constants.EXECUTION_STRATEGY_SEQUENTIAL.equals(executeDTO.getExecutionStrategy())) {
            int seqPassed = 0, seqFailed = 0, seqSkipped = 0;
            for (TestCase testCase : testCases) {
                try {
                    ExecuteTestCaseDTO caseExecuteDTO = convertToTestCaseExecuteDTO(executeDTO);
                    ExecutionResultDTO result = executeTestCaseWithVariables(testCase.getCaseId(), caseExecuteDTO, userId, null,
                        executionRecord.getRecordId(), reportId);
                    
                    // 注意：executeTestCaseWithVariables 方法内部已经插入了 TestCaseResult
                    
                    if (ExecutionStatusEnum.PASSED.getCode().equals(result.getStatus())) {
                        seqPassed++;
                    } else if (ExecutionStatusEnum.FAILED.getCode().equals(result.getStatus())) {
                        seqFailed++;
                        if (executeDTO.getStopOnFailure()) {
                            break;
                        }
                    } else if (ExecutionStatusEnum.SKIPPED.getCode().equals(result.getStatus())) {
                        seqSkipped++;
                    }
                } catch (Exception e) {
                    recordSuiteTestCaseFailure(reportId, testCase, e.getMessage(), userId, executionRecord.getRecordId());
                    seqFailed++;
                    if (executeDTO.getStopOnFailure()) {
                        break;
                    }
                }
            }
        } else {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            
            for (TestCase testCase : testCases) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        ExecuteTestCaseDTO caseExecuteDTO = convertToTestCaseExecuteDTO(executeDTO);
                        ExecutionResultDTO result = executeTestCaseWithVariables(testCase.getCaseId(), caseExecuteDTO, userId, null,
                            executionRecord.getRecordId(), reportId);
                        // 注意：executeTestCaseWithVariables 方法内部已经插入了 TestCaseResult
                    } catch (Exception e) {
                        recordSuiteTestCaseFailure(reportId, testCase, e.getMessage(), userId, executionRecord.getRecordId());
                    }
                }, executorService);
                futures.add(future);
            }
            
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
        
        LocalDateTime endTime = LocalDateTime.now();
        
        // 关键修复：执行一个查询来刷新 MyBatis 的一级缓存
        testExecutionMapper.findTestCaseResultsByReportId(reportId);
        
        // 关键修复：由于异步线程使用独立的数据库连接，主线程可能看不到异步线程插入的数据
        // 使用重试机制确保能够查询到数据
        Map<String, Object> stats = null;
        int maxRetries = 3;
        int retryDelayMs = 100;
        
        for (int retry = 0; retry < maxRetries; retry++) {
            if (retry > 0) {
                try {
                    Thread.sleep(retryDelayMs * retry);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            stats = testExecutionMapper.countResultsByReportId(reportId);
            if (stats != null && stats.get("total") != null && ((Number) stats.get("total")).intValue() > 0) {
                break;
            }
            log.warn("Suite测试统计查询为空, 重试 {}/{}", retry + 1, maxRetries);
        }
        
        // 关键修复：不再调用 updateReportSummaryStats，因为该方法会覆盖正确的结果
        // 直接用重试查询到的结果来更新 TestReportSummaries
        if (stats == null) {
            stats = new HashMap<>();
        }
        
        int passed = stats.get("passed") != null ? ((Number) stats.get("passed")).intValue() : 0;
        int failed = stats.get("failed") != null ? ((Number) stats.get("failed")).intValue() : 0;
        int skipped = stats.get("skipped") != null ? ((Number) stats.get("skipped")).intValue() : 0;
        int broken = stats.get("broken") != null ? ((Number) stats.get("broken")).intValue() : 0;
        int total = testCases.size();
        
        Long totalDuration = stats.get("totalDuration") != null ? ((Number) stats.get("totalDuration")).longValue() : 0L;
        Long avgDuration = stats.get("avgDuration") != null ? ((Number) stats.get("avgDuration")).longValue() : 0L;
        Long maxDuration = stats.get("maxDuration") != null ? ((Number) stats.get("maxDuration")).longValue() : 0L;
        Long minDuration = stats.get("minDuration") != null ? ((Number) stats.get("minDuration")).longValue() : 0L;
        
        BigDecimal successRate = total > 0 ? BigDecimal.valueOf((double) passed / total * 100).setScale(2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        // 直接更新 TestReportSummaries 表，不调用 updateReportSummaryStats
        TestReportSummary reportSummary = testExecutionMapper.findTestReportSummaryById(reportId);
        if (reportSummary != null) {
            reportSummary.setExecutedCases(total);
            reportSummary.setPassedCases(passed);
            reportSummary.setFailedCases(failed);
            reportSummary.setBrokenCases(broken);
            reportSummary.setSkippedCases(skipped);
            reportSummary.setSuccessRate(successRate);
            reportSummary.setTotalDuration(totalDuration);
            reportSummary.setAvgDuration(avgDuration);
            reportSummary.setMaxDuration(maxDuration);
            reportSummary.setMinDuration(minDuration > 0 ? minDuration : 0L);
            reportSummary.setStartTime(startTime);
            reportSummary.setEndTime(endTime);
            reportSummary.setDuration(java.time.Duration.between(startTime, endTime).toMillis());
            reportSummary.setReportStatus(failed > 0 || broken > 0 ? "failed" : "completed");
            reportSummary.setUpdatedAt(LocalDateTime.now());
            testExecutionMapper.updateTestReportSummary(reportSummary);
            log.info("更新Suite测试报告统计: reportId={}, total={}, passed={}, failed={}, broken={}, skipped={}, successRate={}%", 
                    reportId, total, passed, failed, broken, skipped, successRate);
        }
        
        log.info("Suite测试统计结果: reportId={}, passed={}, failed={}, skipped={}, broken={}, totalCases={}", 
                reportId, passed, failed, skipped, broken, testCases.size());
        
        // 更新执行记录的统计数据
        executionRecord.setEndTime(endTime);
        executionRecord.setDurationSeconds((int) java.time.Duration.between(startTime, endTime).toSeconds());
        executionRecord.setExecutedCases(passed + failed + skipped + broken);
        executionRecord.setPassedCases(passed);
        executionRecord.setFailedCases(failed + broken);
        executionRecord.setSkippedCases(skipped);
        executionRecord.setSuccessRate(testCases.size() > 0 ? 
            BigDecimal.valueOf((double) passed / testCases.size() * 100) : BigDecimal.ZERO);
        executionRecord.setStatus(failed + broken > 0 ? "failed" : "completed");
        executionRecord.setReportUrl("/api/reports/" + reportId + "/summary");
        testExecutionRecordMapper.updateExecutionRecord(executionRecord);
        
        Map<String, Object> details = buildSuiteExecutionDetails(testCases);
        
        // 构建执行计划信息
        TestSuiteExecutionResultDTO.ExecutionPlan executionPlan = new TestSuiteExecutionResultDTO.ExecutionPlan();
        executionPlan.setStrategy(executeDTO.getExecutionStrategy());
        executionPlan.setConcurrency(executeDTO.getConcurrency());
        executionPlan.setDependencyLevels(1);
        executionPlan.setBatches(1);
        
        TestSuiteExecutionResultDTO result = new TestSuiteExecutionResultDTO();
        result.setExecutionId(System.currentTimeMillis());
        result.setSuiteId(testSuite.getSuiteId());
        result.setSuiteName(testSuite.getName());
        result.setSuiteCode(testSuite.getSuiteCode());
        result.setStartTime(startTime);
        result.setEndTime(endTime);
        result.setTotalDuration(java.time.Duration.between(startTime, endTime).toMillis());
        result.setTotalCases(testCases.size());
        result.setExecutedCases(testCases.size());
        result.setPassed(passed);
        result.setFailed(failed);
        result.setSkipped(skipped);
        result.setRetried(0);
        result.setSuccessRate(testCases.size() > 0 ? BigDecimal.valueOf((double) passed / testCases.size() * 100) : BigDecimal.ZERO);
        result.setDetails(details);
        result.setExecutionPlan(executionPlan);
        result.setReportId(reportId);
        result.setSummaryUrl("/api/reports/" + reportId + "/summary");
        result.setDownloadUrl("/api/reports/" + reportId + "/export");
        result.setArtifactsUrl("/api/reports/" + reportId + "/artifacts");
        
        return result;
    }

    /**
     * 异步执行测试套件测试用例
     */
    private void executeSuiteTestCasesAsync(String taskId, TestSuite testSuite, List<TestCase> testCases, 
                                           ExecuteTestSuiteDTO executeDTO, Integer userId) {
        updateSuiteTaskStatus(taskId, TaskExecutionStatusEnum.RUNNING.getCode(), "开始执行测试用例");
        
        LocalDateTime startTime = LocalDateTime.now();
        
        TestExecutionRecord executionRecord = createExecutionRecord("suite", testSuite.getSuiteId(), 
            testSuite.getName(), userId, executeDTO.getExecutionType(), executeDTO.getEnvironment());
        executionRecord.setTotalCases(testCases.size());
        testExecutionRecordMapper.insertExecutionRecord(executionRecord);
        
        Long reportId = createSuiteTestReportSummary(testSuite, testCases.size(), userId);
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (TestCase testCase : testCases) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    ExecuteTestCaseDTO caseExecuteDTO = convertToTestCaseExecuteDTO(executeDTO);
                    ExecutionResultDTO result = executeTestCaseWithVariables(testCase.getCaseId(), caseExecuteDTO, userId, null,
                        executionRecord.getRecordId(), reportId);
                    // 注意：executeTestCaseWithVariables 方法内部已经插入了 TestCaseResult
                } catch (Exception e) {
                    recordSuiteTestCaseFailure(reportId, testCase, e.getMessage(), userId, executionRecord.getRecordId());
                }
            }, executorService);
            futures.add(future);
        }
        
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        LocalDateTime endTime = LocalDateTime.now();
        
        updateReportSummaryStats(reportId, startTime, endTime);
        
        Map<String, Object> stats = testExecutionMapper.countResultsByReportId(reportId);
        
        // 关键修复：由于异步线程使用独立的数据库连接，主线程可能看不到异步线程插入的数据
        // 使用重试机制确保能够查询到数据
        int maxRetries = 3;
        int retryDelayMs = 100;
        
        for (int retry = 0; retry < maxRetries; retry++) {
            if (retry > 0) {
                try {
                    Thread.sleep(retryDelayMs * retry);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            stats = testExecutionMapper.countResultsByReportId(reportId);
            if (stats != null && stats.get("total") != null && ((Number) stats.get("total")).intValue() > 0) {
                break;
            }
            log.warn("统计查询为空, 重试 {}/{}", retry + 1, maxRetries);
        }
        
        int passed = stats != null && stats.get("passed") != null ? ((Number) stats.get("passed")).intValue() : 0;
        int failed = stats != null && stats.get("failed") != null ? ((Number) stats.get("failed")).intValue() : 0;
        int skipped = stats != null && stats.get("skipped") != null ? ((Number) stats.get("skipped")).intValue() : 0;
        int broken = stats != null && stats.get("broken") != null ? ((Number) stats.get("broken")).intValue() : 0;
        
        // 更新执行记录的统计数据
        executionRecord.setEndTime(endTime);
        executionRecord.setDurationSeconds((int) java.time.Duration.between(startTime, endTime).toSeconds());
        executionRecord.setExecutedCases(passed + failed + skipped + broken);
        executionRecord.setPassedCases(passed);
        executionRecord.setFailedCases(failed + broken);
        executionRecord.setSkippedCases(skipped);
        executionRecord.setSuccessRate(testCases.size() > 0 ? 
            BigDecimal.valueOf((double) passed / testCases.size() * 100) : BigDecimal.ZERO);
        executionRecord.setStatus(failed + broken > 0 ? "failed" : "completed");
        executionRecord.setReportUrl("/api/reports/" + reportId + "/summary");
        testExecutionRecordMapper.updateExecutionRecord(executionRecord);
        
        TestSuiteExecutionResultDTO taskInfo = getTestSuiteTaskStatus(taskId, userId);
        taskInfo.setStatus(TaskExecutionStatusEnum.COMPLETED.getCode());
        taskInfo.setStartTime(startTime);
        taskInfo.setEndTime(endTime);
        taskInfo.setTotalDuration(java.time.Duration.between(startTime, endTime).toMillis());
        taskInfo.setPassed(passed);
        taskInfo.setFailed(failed);
        taskInfo.setSkipped(skipped);
        taskInfo.setRetried(0);
        taskInfo.setSuccessRate(testCases.size() > 0 ? BigDecimal.valueOf((double) passed / testCases.size() * 100) : BigDecimal.ZERO);
        taskInfo.setReportId(reportId);
        taskInfo.setSummaryUrl("/api/reports/" + reportId + "/summary");
        taskInfo.setDownloadUrl("/api/reports/" + reportId + "/export");
        taskInfo.setArtifactsUrl("/api/reports/" + reportId + "/artifacts");
        
        updateSuiteTaskStatus(taskId, TaskExecutionStatusEnum.COMPLETED.getCode(), "执行完成");
    }

    /**
     * 转换测试套件执行参数为用例执行参数
     */
    private ExecuteTestCaseDTO convertToTestCaseExecuteDTO(ExecuteTestSuiteDTO executeDTO) {
        ExecuteTestCaseDTO caseExecuteDTO = new ExecuteTestCaseDTO();
        caseExecuteDTO.setEnvironment(executeDTO.getEnvironment());
        caseExecuteDTO.setBaseUrl(executeDTO.getBaseUrl());
        caseExecuteDTO.setTimeout(executeDTO.getTimeout());
        // 转换JsonNode为Map<String, Object>
        if (executeDTO.getAuthOverride() != null) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> authOverride = objectMapper.convertValue(executeDTO.getAuthOverride(), Map.class);
                caseExecuteDTO.setAuthOverride(authOverride);
            } catch (Exception e) {
                // 忽略转换错误
            }
        }
        // 转换Map<String, String>为Map<String, Object>
        if (executeDTO.getVariables() != null) {
            Map<String, Object> variables = new HashMap<>(executeDTO.getVariables());
            caseExecuteDTO.setVariables(variables);
        }
        caseExecuteDTO.setAsync(false); // 单个用例执行不使用异步
        
        return caseExecuteDTO;
    }

    /**
     * 创建测试套件测试报告汇总
     */
    private Long createSuiteTestReportSummary(TestSuite testSuite, int totalCases, Integer userId) {
        TestReportSummary reportSummary = new TestReportSummary();
        String reportName = testSuite.getName() + "_" + formatTimestamp(LocalDateTime.now());
        reportSummary.setReportName(reportName);
        reportSummary.setReportType(ReportTypeEnum.EXECUTION.getCode());
        reportSummary.setProjectId(testSuite.getProjectId());
        reportSummary.setEnvironment("test");
        reportSummary.setStartTime(LocalDateTime.now());
        reportSummary.setEndTime(LocalDateTime.now());
        reportSummary.setDuration(0L);
        reportSummary.setTotalCases(totalCases);
        reportSummary.setExecutedCases(0);
        reportSummary.setPassedCases(0);
        reportSummary.setFailedCases(0);
        reportSummary.setBrokenCases(0);
        reportSummary.setSkippedCases(0);
        reportSummary.setSuccessRate(BigDecimal.valueOf(0.0));
        reportSummary.setTotalDuration(0L);
        reportSummary.setAvgDuration(0L);
        reportSummary.setMaxDuration(0L);
        reportSummary.setMinDuration(0L);
        reportSummary.setReportStatus(ReportStatusEnum.GENERATING.getCode());
        reportSummary.setFileFormat("html");
        reportSummary.setGeneratedBy(userId);
        
        testExecutionMapper.insertTestReportSummary(reportSummary);
        return reportSummary.getReportId();
    }

    private void recordSuiteTestCaseResult(Long reportId, TestCase testCase, 
                                         ExecutionResultDTO result, Integer userId, Long executionRecordId) {
        TestCaseResult testCaseResult = new TestCaseResult();
        testCaseResult.setExecutionRecordId(executionRecordId);
        testCaseResult.setReportId(reportId);
        testCaseResult.setTaskType(TaskTypeEnum.TEST_CASE.getCode());
        testCaseResult.setRefId(testCase.getCaseId());
        testCaseResult.setFullName(testCase.getName());
        testCaseResult.setStatus(result.getStatus());
        testCaseResult.setDuration(result.getDuration());
        testCaseResult.setStartTime(LocalDateTime.now());
        testCaseResult.setEndTime(LocalDateTime.now());
        testCaseResult.setEnvironment("test");
        testCaseResult.setSeverity(normalizeSeverity(testCase.getSeverity()));
        testCaseResult.setPriority(normalizePriority(testCase.getPriority()));
        
        testCaseResult.setCaseId(testCase.getCaseId());
        testCaseResult.setCaseCode(testCase.getCaseCode());
        testCaseResult.setCaseName(testCase.getName());
        testCaseResult.setTestLayer("API");
        testCaseResult.setTestType("POSITIVE");
        testCaseResult.setFlakyCount(0);
        testCaseResult.setRetestResult("NOT_RETESTED");
        
        testExecutionMapper.insertTestCaseResult(testCaseResult);
    }

    private void recordSuiteTestCaseFailure(Long reportId, TestCase testCase, String errorMessage, Integer userId, Long executionRecordId) {
        TestCaseResult testCaseResult = new TestCaseResult();
        testCaseResult.setExecutionRecordId(executionRecordId);
        testCaseResult.setReportId(reportId);
        testCaseResult.setTaskType(TaskTypeEnum.TEST_CASE.getCode());
        testCaseResult.setRefId(testCase.getCaseId());
        testCaseResult.setFullName(testCase.getName());
        testCaseResult.setStatus(ExecutionStatusEnum.FAILED.getCode());
        testCaseResult.setDuration(0L);
        testCaseResult.setStartTime(LocalDateTime.now());
        testCaseResult.setEndTime(LocalDateTime.now());
        testCaseResult.setFailureMessage(errorMessage);
        testCaseResult.setEnvironment("test");
        testCaseResult.setSeverity(normalizeSeverity(testCase.getSeverity()));
        testCaseResult.setPriority(normalizePriority(testCase.getPriority()));
        
        testCaseResult.setCaseId(testCase.getCaseId());
        testCaseResult.setCaseCode(testCase.getCaseCode());
        testCaseResult.setCaseName(testCase.getName());
        testCaseResult.setTestLayer("API");
        testCaseResult.setTestType("POSITIVE");
        testCaseResult.setFlakyCount(0);
        testCaseResult.setRetestResult("NOT_RETESTED");
        
        testExecutionMapper.insertTestCaseResult(testCaseResult);
    }

    /**
     * 构建测试套件执行详细统计信息
     */
    private Map<String, Object> buildSuiteExecutionDetails(List<TestCase> testCases) {
        Map<String, Object> details = new HashMap<>();
        Map<String, Object> byPriority = new HashMap<>();
        Map<String, Object> byStatus = new HashMap<>();
        
        // 按优先级统计
        Map<String, Integer> priorityCount = new HashMap<>();
        for (TestCase testCase : testCases) {
            String priority = testCase.getPriority();
            priorityCount.put(priority, priorityCount.getOrDefault(priority, 0) + 1);
        }
        
        for (Map.Entry<String, Integer> entry : priorityCount.entrySet()) {
            Map<String, Object> priorityStats = new HashMap<>();
            priorityStats.put("total", entry.getValue());
            priorityStats.put("passed", 0);
            priorityStats.put("failed", 0);
            byPriority.put(entry.getKey(), priorityStats);
        }
        
        // 按状态统计
        byStatus.put("passed", 0);
        byStatus.put("failed", 0);
        byStatus.put("skipped", 0);
        
        details.put("by_priority", byPriority);
        details.put("by_status", byStatus);
        
        return details;
    }

    /**
     * 更新测试套件任务状态
     */
    private void updateSuiteTaskStatus(String taskId, String status, String message) {
        TestSuiteExecutionResultDTO taskInfo = suiteTaskStatusCache.get(taskId);
        if (taskInfo != null) {
            taskInfo.setStatus(status);
            suiteTaskStatusCache.put(taskId, taskInfo);
            
            try {
                redisComponent.setString(Constants.SUITE_EXECUTION_QUEUE + ":" + taskId, 
                    objectMapper.writeValueAsString(taskInfo), Constants.SUITE_RESULT_CACHE_HOURS * 3600);
            } catch (Exception e) {
                // 忽略JSON序列化错误
            }
        }
    }

    // ========== 测试结果查询相关方法实现 ==========

    @Override
    public com.victor.iatms.entity.dto.TestResultPageResultDTO getTestResults(
            com.victor.iatms.entity.query.TestResultQuery query, Integer userId) {
        try {
            // 参数校验
            validateTestResultQuery(query);

            // 查询测试结果列表
            List<TestCaseResult> results = testExecutionMapper.findTestResults(query);
            
            // 查询总数
            Long total = testExecutionMapper.countTestResults(query);
            
            // 查询统计摘要
            com.victor.iatms.entity.dto.TestResultSummaryDTO summary = 
                testExecutionMapper.getTestResultSummary(query);
            
            // 如果没有数据，返回空的统计
            if (summary == null) {
                summary = new com.victor.iatms.entity.dto.TestResultSummaryDTO();
            }

            // 转换为DTO列表
            List<com.victor.iatms.entity.dto.TestResultDTO> dtoList = convertToTestResultDTOList(results);

            // 构建分页结果
            com.victor.iatms.entity.dto.TestResultPageResultDTO pageResult = 
                new com.victor.iatms.entity.dto.TestResultPageResultDTO(
                    total, dtoList, query.getPage(), query.getPageSize(), summary);

            return pageResult;

        } catch (RuntimeException e) {
            log.error("获取测试结果列表失败: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("获取测试结果列表异常: {}", e.getMessage(), e);
            throw new RuntimeException("获取测试结果列表失败");
        }
    }

    /**
     * 校验测试结果查询参数
     */
    private void validateTestResultQuery(com.victor.iatms.entity.query.TestResultQuery query) {
        // 校验分页参数
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(20);
        }
        if (query.getPageSize() > 100) {
            query.setPageSize(100);
        }
        
        // 计算分页偏移量
        int offset = (query.getPage() - 1) * query.getPageSize();
        query.setOffset(offset);

        // 校验任务类型
        if (query.getTaskType() != null && !query.getTaskType().isEmpty()) {
            TaskTypeEnum taskType = TaskTypeEnum.getByCode(query.getTaskType());
            if (taskType == null) {
                throw new RuntimeException("无效的任务类型");
            }
        }

        // 校验执行状态
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            ExecutionStatusEnum status = ExecutionStatusEnum.getByCode(query.getStatus());
            if (status == null) {
                throw new RuntimeException("无效的执行状态");
            }
        }

        // 校验严重程度
        if (query.getSeverity() != null && !query.getSeverity().isEmpty()) {
            com.victor.iatms.entity.enums.ResultSeverityEnum severity = 
                com.victor.iatms.entity.enums.ResultSeverityEnum.fromCode(query.getSeverity());
            if (severity == null) {
                throw new RuntimeException("无效的严重程度");
            }
        }

        // 校验优先级（支持多个）
        if (query.getPriority() != null && !query.getPriority().isEmpty()) {
            String[] priorities = query.getPriority().split(",");
            for (String p : priorities) {
                com.victor.iatms.entity.enums.PriorityEnum priority = 
                    com.victor.iatms.entity.enums.PriorityEnum.fromCode(p.trim());
                if (priority == null) {
                    throw new RuntimeException("无效的优先级: " + p);
                }
            }
        }

        // 校验时间范围
        if (query.getStartTimeBegin() != null && query.getStartTimeEnd() != null) {
            if (query.getStartTimeBegin().isAfter(query.getStartTimeEnd())) {
                throw new RuntimeException("开始时间不能晚于结束时间");
            }
        }

        // 校验执行时长范围
        if (query.getDurationMin() != null && query.getDurationMax() != null) {
            if (query.getDurationMin() > query.getDurationMax()) {
                throw new RuntimeException("最小时长不能大于最大时长");
            }
        }

        // 设置默认排序
        if (query.getSortBy() == null || query.getSortBy().isEmpty()) {
            query.setSortBy("start_time");
        }
        if (query.getSortOrder() == null || query.getSortOrder().isEmpty()) {
            query.setSortOrder("desc");
        }

        // 校验排序字段
        List<String> validSortFields = Arrays.asList("start_time", "duration", "priority", "severity");
        if (!validSortFields.contains(query.getSortBy())) {
            throw new RuntimeException("无效的排序字段");
        }

        // 校验排序顺序
        if (!query.getSortOrder().equals("asc") && !query.getSortOrder().equals("desc")) {
            query.setSortOrder("desc");
        }
    }

    /**
     * 转换为TestResultDTO列表
     */
    private List<com.victor.iatms.entity.dto.TestResultDTO> convertToTestResultDTOList(
            List<TestCaseResult> results) {
        List<com.victor.iatms.entity.dto.TestResultDTO> dtoList = new ArrayList<>();
        
        for (TestCaseResult result : results) {
            com.victor.iatms.entity.dto.TestResultDTO dto = 
                new com.victor.iatms.entity.dto.TestResultDTO();
            
            dto.setResultId(result.getResultId());
            dto.setReportId(result.getReportId());
            dto.setExecutionId(result.getExecutionId());
            dto.setTaskType(result.getTaskType());
            dto.setRefId(result.getRefId());
            dto.setRefName(getRefName(result.getTaskType(), result.getRefId()));
            dto.setFullName(result.getFullName());
            dto.setStatus(result.getStatus());
            dto.setDuration(result.getDuration());
            
            // 格式化时间
            if (result.getStartTime() != null) {
                dto.setStartTime(DateUtil.formatToISO8601(result.getStartTime()));
            }
            if (result.getEndTime() != null) {
                dto.setEndTime(DateUtil.formatToISO8601(result.getEndTime()));
            }
            
            dto.setPriority(result.getPriority());
            dto.setSeverity(result.getSeverity());
            dto.setEnvironment(result.getEnvironment() != null ? result.getEnvironment() : "test");
            dto.setFailureMessage(result.getFailureMessage());
            dto.setFailureType(result.getFailureType());
            dto.setRetryCount(result.getRetryCount());
            dto.setBrowser(result.getBrowser());
            dto.setOs(result.getOs());
            
            // 生成链接
            dto.setLogsLink("/api/test-results/" + result.getResultId() + "/logs");
            dto.setScreenshotLink(result.getScreenshotLink() != null ? 
                "/api/test-results/" + result.getResultId() + "/screenshot" : null);
            
            dtoList.add(dto);
        }
        
        return dtoList;
    }

    /**
     * 根据任务类型和引用ID获取引用名称
     */
    private String getRefName(String taskType, Integer refId) {
        if (taskType == null || refId == null) {
            return null;
        }
        
        try {
            switch (taskType) {
                case "test_case":
                    // 查询测试用例名称（这里简化处理，实际应该查询数据库）
                    return "测试用例-" + refId;
                case "test_suite":
                    TestSuite suite = testExecutionMapper.findTestSuiteById(refId);
                    return suite != null ? suite.getName() : "测试套件-" + refId;
                case "module":
                    Module module = testExecutionMapper.findModuleById(refId);
                    return module != null ? module.getName() : "模块-" + refId;
                case "project":
                    Project project = testExecutionMapper.findProjectById(refId);
                    return project != null ? project.getName() : "项目-" + refId;
                case "api_monitor":
                    Api api = testExecutionMapper.findApiById(refId);
                    return api != null ? api.getName() : "API-" + refId;
                default:
                    return "未知-" + refId;
            }
        } catch (Exception e) {
            log.warn("获取引用名称失败: taskType={}, refId={}", taskType, refId, e);
            return taskType + "-" + refId;
        }
    }

    @Override
    public com.victor.iatms.entity.dto.TestResultDetailDTO getTestResultDetail(
            Long resultId, Boolean includeSteps, Boolean includeAssertions, 
            Boolean includeArtifacts, Boolean includeEnvironment, Integer userId) {
        try {
            // 查询测试结果
            TestCaseResult result = testExecutionMapper.findTestResultById(resultId);
            if (result == null) {
                throw new RuntimeException("测试结果不存在");
            }

            // 构建详情DTO
            com.victor.iatms.entity.dto.TestResultDetailDTO detailDTO = 
                new com.victor.iatms.entity.dto.TestResultDetailDTO();

            // 1. 构建基本信息
            detailDTO.setResultInfo(buildResultInfo(result));

            // 2. 构建执行上下文
            detailDTO.setExecutionContext(buildExecutionContext(result));

            // 3. 构建测试步骤（根据参数决定是否包含）
            if (includeSteps == null || includeSteps) {
                detailDTO.setTestSteps(buildTestSteps(result));
            }

            // 4. 构建断言结果（根据参数决定是否包含）
            if (includeAssertions == null || includeAssertions) {
                detailDTO.setAssertions(buildAssertions(result));
            }

            // 5. 构建附件信息（根据参数决定是否包含）
            if (includeArtifacts != null && includeArtifacts) {
                detailDTO.setArtifacts(buildArtifacts(result));
            }

            // 6. 构建环境信息（根据参数决定是否包含）
            if (includeEnvironment == null || includeEnvironment) {
                detailDTO.setEnvironment(buildEnvironmentInfo(result));
            }

            // 7. 构建性能指标
            detailDTO.setPerformance(buildPerformance(result));

            return detailDTO;

        } catch (RuntimeException e) {
            log.error("获取测试结果详情失败: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("获取测试结果详情异常: {}", e.getMessage(), e);
            throw new RuntimeException("获取测试结果详情失败");
        }
    }

    /**
     * 构建结果基本信息
     */
    private com.victor.iatms.entity.dto.TestResultInfoDTO buildResultInfo(TestCaseResult result) {
        com.victor.iatms.entity.dto.TestResultInfoDTO info = 
            new com.victor.iatms.entity.dto.TestResultInfoDTO();
        
        info.setResultId(result.getResultId());
        info.setReportId(result.getReportId());
        info.setExecutionId(result.getExecutionId());
        info.setTaskType(result.getTaskType());
        info.setRefId(result.getRefId());
        info.setRefName(getRefName(result.getTaskType(), result.getRefId()));
        info.setFullName(result.getFullName());
        info.setStatus(result.getStatus());
        info.setDuration(result.getDuration());
        
        if (result.getStartTime() != null) {
            info.setStartTime(DateUtil.formatToISO8601(result.getStartTime()));
        }
        if (result.getEndTime() != null) {
            info.setEndTime(DateUtil.formatToISO8601(result.getEndTime()));
        }
        
        info.setPriority(result.getPriority());
        info.setSeverity(result.getSeverity());
        info.setRetryCount(result.getRetryCount());
        info.setFlaky(result.getFlaky());
        
        return info;
    }

    /**
     * 构建执行上下文
     */
    private com.victor.iatms.entity.dto.ExecutionContextDTO buildExecutionContext(TestCaseResult result) {
        com.victor.iatms.entity.dto.ExecutionContextDTO context = 
            new com.victor.iatms.entity.dto.ExecutionContextDTO();
        
        context.setEnvironment(result.getEnvironment() != null ? result.getEnvironment() : "test");
        
        // 解析parameters_json获取请求信息
        if (result.getParametersJson() != null) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> params = objectMapper.readValue(
                    result.getParametersJson(), Map.class);
                
                context.setBaseUrl((String) params.get("base_url"));
                context.setRequestUrl((String) params.get("request_url"));
                context.setRequestMethod((String) params.get("request_method"));
                context.setRequestHeaders((Map<String, Object>) params.get("request_headers"));
                context.setRequestBody((String) params.get("request_body"));
                context.setResponseStatus((Integer) params.get("response_status"));
                context.setResponseHeaders((Map<String, Object>) params.get("response_headers"));
                context.setResponseBody((String) params.get("response_body"));
                context.setResponseSize(params.get("response_size") != null ? 
                    ((Number) params.get("response_size")).longValue() : null);
                context.setVariables((Map<String, Object>) params.get("variables"));
            } catch (Exception e) {
                log.warn("解析执行参数失败: resultId={}", result.getResultId(), e);
            }
        }
        
        return context;
    }

    /**
     * 构建测试步骤
     */
    private List<com.victor.iatms.entity.dto.TestStepDTO> buildTestSteps(TestCaseResult result) {
        List<com.victor.iatms.entity.dto.TestStepDTO> steps = new ArrayList<>();
        
        if (result.getStepsJson() != null) {
            try {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> stepsData = objectMapper.readValue(
                    result.getStepsJson(), List.class);
                
                for (int i = 0; i < stepsData.size(); i++) {
                    Map<String, Object> stepData = stepsData.get(i);
                    com.victor.iatms.entity.dto.TestStepDTO step = 
                        new com.victor.iatms.entity.dto.TestStepDTO();
                    
                    step.setStepId(i + 1);
                    step.setName((String) stepData.get("name"));
                    step.setDescription((String) stepData.get("description"));
                    step.setStatus((String) stepData.get("status"));
                    step.setDuration(stepData.get("duration") != null ? 
                        ((Number) stepData.get("duration")).longValue() : null);
                    step.setStartTime((String) stepData.get("start_time"));
                    step.setEndTime((String) stepData.get("end_time"));
                    step.setParameters((Map<String, Object>) stepData.get("parameters"));
                    step.setLogs((List<String>) stepData.get("logs"));
                    
                    steps.add(step);
                }
            } catch (Exception e) {
                log.warn("解析测试步骤失败: resultId={}", result.getResultId(), e);
            }
        }
        
        return steps;
    }

    /**
     * 构建断言结果
     */
    private List<com.victor.iatms.entity.dto.AssertionDTO> buildAssertions(TestCaseResult result) {
        List<com.victor.iatms.entity.dto.AssertionDTO> assertions = new ArrayList<>();
        
        // 这里可以从steps_json或单独的assertions字段中解析
        // 简化实现：返回空列表或模拟数据
        
        return assertions;
    }

    /**
     * 构建附件信息
     */
    private List<com.victor.iatms.entity.dto.ArtifactDTO> buildArtifacts(TestCaseResult result) {
        List<com.victor.iatms.entity.dto.ArtifactDTO> artifacts = new ArrayList<>();
        
        if (result.getAttachmentsJson() != null) {
            try {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> attachmentsData = objectMapper.readValue(
                    result.getAttachmentsJson(), List.class);
                
                for (Map<String, Object> attachmentData : attachmentsData) {
                    com.victor.iatms.entity.dto.ArtifactDTO artifact = 
                        new com.victor.iatms.entity.dto.ArtifactDTO();
                    
                    artifact.setType((String) attachmentData.get("type"));
                    artifact.setName((String) attachmentData.get("name"));
                    artifact.setUrl((String) attachmentData.get("url"));
                    artifact.setSize(attachmentData.get("size") != null ? 
                        ((Number) attachmentData.get("size")).longValue() : null);
                    
                    artifacts.add(artifact);
                }
            } catch (Exception e) {
                log.warn("解析附件信息失败: resultId={}", result.getResultId(), e);
            }
        }
        
        // 添加日志和截图链接作为附件
        if (result.getLogsLink() != null) {
            com.victor.iatms.entity.dto.ArtifactDTO logArtifact = 
                new com.victor.iatms.entity.dto.ArtifactDTO();
            logArtifact.setType("log");
            logArtifact.setName("execution.log");
            logArtifact.setUrl(result.getLogsLink());
            artifacts.add(logArtifact);
        }
        
        if (result.getScreenshotLink() != null) {
            com.victor.iatms.entity.dto.ArtifactDTO screenshotArtifact = 
                new com.victor.iatms.entity.dto.ArtifactDTO();
            screenshotArtifact.setType("screenshot");
            screenshotArtifact.setName("screenshot.png");
            screenshotArtifact.setUrl(result.getScreenshotLink());
            artifacts.add(screenshotArtifact);
        }
        
        if (result.getVideoLink() != null) {
            com.victor.iatms.entity.dto.ArtifactDTO videoArtifact = 
                new com.victor.iatms.entity.dto.ArtifactDTO();
            videoArtifact.setType("video");
            videoArtifact.setName("recording.mp4");
            videoArtifact.setUrl(result.getVideoLink());
            artifacts.add(videoArtifact);
        }
        
        return artifacts;
    }

    /**
     * 构建环境信息
     */
    private com.victor.iatms.entity.dto.EnvironmentInfoDTO buildEnvironmentInfo(TestCaseResult result) {
        com.victor.iatms.entity.dto.EnvironmentInfoDTO env = 
            new com.victor.iatms.entity.dto.EnvironmentInfoDTO();
        
        env.setBrowser(result.getBrowser());
        env.setOs(result.getOs());
        env.setDevice(result.getDevice());
        
        // 其他环境信息可以从parameters_json或其他字段中解析
        env.setScreenResolution("1920x1080"); // 默认值或从其他地方获取
        env.setLanguage("zh-CN");
        env.setTimezone("UTC+8");
        
        return env;
    }

    /**
     * 构建性能指标
     */
    private com.victor.iatms.entity.dto.PerformanceDTO buildPerformance(TestCaseResult result) {
        com.victor.iatms.entity.dto.PerformanceDTO performance = 
            new com.victor.iatms.entity.dto.PerformanceDTO();
        
        performance.setResponseTime(result.getDuration());
        
        // 计算吞吐量（请求/秒）
        if (result.getDuration() != null && result.getDuration() > 0) {
            BigDecimal throughput = BigDecimal.valueOf(1000.0)
                .divide(BigDecimal.valueOf(result.getDuration()), 2, java.math.RoundingMode.HALF_UP);
            performance.setThroughput(throughput);
        }
        
        // 其他性能指标可以从parameters_json或监控系统获取
        performance.setMemoryUsage(256L); // 示例值
        performance.setCpuUsage(new BigDecimal("15.5")); // 示例值
        
        return performance;
    }

    @Override
    public com.victor.iatms.entity.dto.TestStatisticsDTO getTestStatistics(
            String timeRange, LocalDateTime startTime, LocalDateTime endTime,
            Integer projectId, Integer moduleId, Integer apiId, String environment,
            String groupBy, Boolean includeTrend, Boolean includeComparison, Integer userId) {
        try {
            // 1. 解析时间范围
            LocalDateTime[] timeRange$ = parseTimeRange(timeRange, startTime, endTime);
            LocalDateTime finalStartTime = timeRange$[0];
            LocalDateTime finalEndTime = timeRange$[1];

            // 2. 构建统计DTO
            com.victor.iatms.entity.dto.TestStatisticsDTO statisticsDTO = 
                new com.victor.iatms.entity.dto.TestStatisticsDTO();

            // 3. 获取总体统计摘要
            com.victor.iatms.entity.dto.StatisticsSummaryDTO summary = 
                testExecutionMapper.getStatisticsSummary(
                    finalStartTime, finalEndTime, projectId, moduleId, apiId, environment);
            
            if (summary != null) {
                summary.setStartTime(DateUtil.formatToISO8601(finalStartTime));
                summary.setEndTime(DateUtil.formatToISO8601(finalEndTime));
            }
            statisticsDTO.setSummary(summary);

            // 4. 获取趋势数据（如果需要）
            if (includeTrend == null || includeTrend) {
                String trendGroupBy = groupBy != null && 
                    Arrays.asList("hour", "day", "week", "month").contains(groupBy) ? groupBy : "day";
                List<com.victor.iatms.entity.dto.TrendDataDTO> trendData = 
                    testExecutionMapper.getTrendData(
                        finalStartTime, finalEndTime, trendGroupBy, 
                        projectId, moduleId, apiId, environment);
                statisticsDTO.setTrendData(trendData);
            }

            // 5. 获取分组数据
            if (groupBy != null && Arrays.asList("priority", "severity").contains(groupBy)) {
                List<com.victor.iatms.entity.dto.GroupDataDTO> groupData = 
                    testExecutionMapper.getGroupData(
                        finalStartTime, finalEndTime, groupBy, 
                        projectId, moduleId, apiId, environment);
                statisticsDTO.setGroupData(groupData);
            }

            // 6. 获取主要问题统计
            List<com.victor.iatms.entity.dto.TopIssueDTO> topIssues = 
                testExecutionMapper.getTopIssues(
                    finalStartTime, finalEndTime, projectId, moduleId, apiId, environment, 10);
            statisticsDTO.setTopIssues(topIssues);

            // 7. 计算同比环比数据（如果需要）
            if (includeComparison != null && includeComparison) {
                com.victor.iatms.entity.dto.ComparisonDataDTO comparisonData = 
                    buildComparisonData(finalStartTime, finalEndTime, projectId, environment, summary);
                statisticsDTO.setComparisonData(comparisonData);
            }

            // 8. 构建执行指标
            com.victor.iatms.entity.dto.ExecutionMetricsDTO executionMetrics = 
                buildExecutionMetrics(summary, finalStartTime, finalEndTime);
            statisticsDTO.setExecutionMetrics(executionMetrics);

            return statisticsDTO;

        } catch (RuntimeException e) {
            log.error("获取测试统计信息失败: {}", e.getMessage(), e);
            // 返回默认值而不是抛出异常
            com.victor.iatms.entity.dto.TestStatisticsDTO defaultDTO = 
                new com.victor.iatms.entity.dto.TestStatisticsDTO();
            defaultDTO.setSummary(null);
            defaultDTO.setTrendData(new java.util.ArrayList<>());
            defaultDTO.setGroupData(new java.util.ArrayList<>());
            defaultDTO.setTopIssues(new java.util.ArrayList<>());
            defaultDTO.setComparisonData(null);
            defaultDTO.setExecutionMetrics(null);
            return defaultDTO;
        } catch (Exception e) {
            log.error("获取测试统计信息异常: {}", e.getMessage(), e);
            // 返回默认值而不是抛出异常
            com.victor.iatms.entity.dto.TestStatisticsDTO defaultDTO = 
                new com.victor.iatms.entity.dto.TestStatisticsDTO();
            defaultDTO.setSummary(null);
            defaultDTO.setTrendData(new java.util.ArrayList<>());
            defaultDTO.setGroupData(new java.util.ArrayList<>());
            defaultDTO.setTopIssues(new java.util.ArrayList<>());
            defaultDTO.setComparisonData(null);
            defaultDTO.setExecutionMetrics(null);
            return defaultDTO;
        }
    }

    /**
     * 解析时间范围
     */
    private LocalDateTime[] parseTimeRange(String timeRange, LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime finalStartTime;
        LocalDateTime finalEndTime = LocalDateTime.now();

        if ("custom".equals(timeRange)) {
            // 自定义时间范围
            if (startTime == null || endTime == null) {
                throw new RuntimeException("自定义时间范围时，必须提供start_time和end_time参数");
            }
            if (startTime.isAfter(endTime)) {
                throw new RuntimeException("开始时间不能晚于结束时间");
            }
            finalStartTime = startTime;
            finalEndTime = endTime;
        } else {
            // 预设时间范围
            switch (timeRange != null ? timeRange : "7d") {
                case "1d":
                    finalStartTime = finalEndTime.minusDays(1);
                    break;
                case "7d":
                    finalStartTime = finalEndTime.minusDays(7);
                    break;
                case "30d":
                    finalStartTime = finalEndTime.minusDays(30);
                    break;
                case "90d":
                    finalStartTime = finalEndTime.minusDays(90);
                    break;
                default:
                    finalStartTime = finalEndTime.minusDays(7);
            }
        }

        return new LocalDateTime[]{finalStartTime, finalEndTime};
    }

    /**
     * 构建同比环比数据
     */
    private com.victor.iatms.entity.dto.ComparisonDataDTO buildComparisonData(
            LocalDateTime startTime, LocalDateTime endTime, 
            Integer projectId, String environment,
            com.victor.iatms.entity.dto.StatisticsSummaryDTO currentSummary) {
        
        com.victor.iatms.entity.dto.ComparisonDataDTO comparisonData = 
            new com.victor.iatms.entity.dto.ComparisonDataDTO();

        // 计算上一周期
        long daysDiff = java.time.Duration.between(startTime, endTime).toDays();
        LocalDateTime prevStartTime = startTime.minusDays(daysDiff);
        LocalDateTime prevEndTime = endTime.minusDays(daysDiff);

        com.victor.iatms.entity.dto.StatisticsSummaryDTO prevSummary = 
            testExecutionMapper.getStatisticsSummary(
                prevStartTime, prevEndTime, projectId, null, null, environment);

        if (prevSummary != null && prevSummary.getSuccessRate() != null) {
            com.victor.iatms.entity.dto.ComparisonDataDTO.PeriodComparisonDTO previousPeriod = 
                new com.victor.iatms.entity.dto.ComparisonDataDTO.PeriodComparisonDTO();
            
            previousPeriod.setSuccessRate(prevSummary.getSuccessRate());
            
            BigDecimal changePercent = currentSummary.getSuccessRate()
                .subtract(prevSummary.getSuccessRate());
            previousPeriod.setChangePercent(changePercent);
            
            String trend = changePercent.compareTo(BigDecimal.ZERO) > 0 ? "up" : 
                          changePercent.compareTo(BigDecimal.ZERO) < 0 ? "down" : "stable";
            previousPeriod.setTrend(trend);
            
            comparisonData.setPreviousPeriod(previousPeriod);
        }

        // 计算同比（去年同期）- 简化实现
        LocalDateTime yearAgoStart = startTime.minusYears(1);
        LocalDateTime yearAgoEnd = endTime.minusYears(1);

        com.victor.iatms.entity.dto.StatisticsSummaryDTO yearAgoSummary = 
            testExecutionMapper.getStatisticsSummary(
                yearAgoStart, yearAgoEnd, projectId, null, null, environment);

        if (yearAgoSummary != null && yearAgoSummary.getSuccessRate() != null) {
            com.victor.iatms.entity.dto.ComparisonDataDTO.PeriodComparisonDTO yearOverYear = 
                new com.victor.iatms.entity.dto.ComparisonDataDTO.PeriodComparisonDTO();
            
            yearOverYear.setSuccessRate(yearAgoSummary.getSuccessRate());
            
            BigDecimal changePercent = currentSummary.getSuccessRate()
                .subtract(yearAgoSummary.getSuccessRate());
            yearOverYear.setChangePercent(changePercent);
            
            String trend = changePercent.compareTo(BigDecimal.ZERO) > 0 ? "up" : 
                          changePercent.compareTo(BigDecimal.ZERO) < 0 ? "down" : "stable";
            yearOverYear.setTrend(trend);
            
            comparisonData.setYearOverYear(yearOverYear);
        }

        return comparisonData;
    }

    /**
     * 构建执行指标
     */
    private com.victor.iatms.entity.dto.ExecutionMetricsDTO buildExecutionMetrics(
            com.victor.iatms.entity.dto.StatisticsSummaryDTO summary,
            LocalDateTime startTime, LocalDateTime endTime) {
        
        com.victor.iatms.entity.dto.ExecutionMetricsDTO metrics = 
            new com.victor.iatms.entity.dto.ExecutionMetricsDTO();

        // 总执行耗时
        if (summary != null && summary.getAvgDuration() != null && summary.getTotalExecutions() != null) {
            metrics.setTotalDuration(summary.getAvgDuration() * summary.getTotalExecutions());
        }

        // 计算吞吐量（次/分钟）
        long minutesDiff = java.time.Duration.between(startTime, endTime).toMinutes();
        if (minutesDiff > 0 && summary != null && summary.getTotalExecutions() != null) {
            BigDecimal throughput = BigDecimal.valueOf(summary.getTotalExecutions())
                .divide(BigDecimal.valueOf(minutesDiff), 2, java.math.RoundingMode.HALF_UP);
            metrics.setThroughput(throughput);
        }

        // 可靠性（成功率）
        if (summary != null && summary.getSuccessRate() != null) {
            metrics.setReliability(summary.getSuccessRate());
        }

        // 平均并发数和峰值并发数（简化实现，使用估算值）
        metrics.setAvgConcurrency(new BigDecimal("8.5"));
        metrics.setPeakConcurrency(15);

        return metrics;
    }

    @Override
    public com.victor.iatms.entity.dto.WeeklyExecutionDTO getWeeklyExecution(
            Integer projectId, Integer moduleId, String environment,
            Boolean includeDailyTrend, Boolean includeTopFailures, Boolean includePerformance, Integer userId) {
        try {
            // 1. 计算近七天时间范围
            java.time.LocalDateTime endTime = java.time.LocalDateTime.now();
            java.time.LocalDateTime startTime = endTime.minusDays(7);

            // 2. 构建近七天执行情况DTO
            com.victor.iatms.entity.dto.WeeklyExecutionDTO weeklyExecution = 
                new com.victor.iatms.entity.dto.WeeklyExecutionDTO();

            // 3. 设置日期范围信息
            com.victor.iatms.entity.dto.DateRangeDTO dateRange = 
                new com.victor.iatms.entity.dto.DateRangeDTO();
            dateRange.setStartDate(startTime.toLocalDate().toString());
            dateRange.setEndDate(endTime.toLocalDate().toString());
            dateRange.setDays(7);
            weeklyExecution.setDateRange(dateRange);

            // 4. 获取总体统计摘要
            com.victor.iatms.entity.dto.SummaryDTO summary = 
                testExecutionMapper.getWeeklySummary(startTime, endTime, projectId, moduleId, environment);
            
            if (summary != null) {
                // 计算与上周的对比变化
                java.time.LocalDateTime lastWeekStart = startTime.minusDays(7);
                java.time.LocalDateTime lastWeekEnd = endTime.minusDays(7);
                
                com.victor.iatms.entity.dto.SummaryDTO lastWeekSummary = 
                    testExecutionMapper.getLastWeekSummary(lastWeekStart, lastWeekEnd, projectId, moduleId, environment);
                
                if (lastWeekSummary != null) {
                    com.victor.iatms.entity.dto.ChangeFromLastWeekDTO change = 
                        new com.victor.iatms.entity.dto.ChangeFromLastWeekDTO();
                    
                    // 计算成功率变化
                    if (summary.getSuccessRate() != null && lastWeekSummary.getSuccessRate() != null) {
                        java.math.BigDecimal successRateChange = summary.getSuccessRate()
                            .subtract(lastWeekSummary.getSuccessRate());
                        change.setSuccessRateChange(successRateChange);
                        
                        String trend = successRateChange.compareTo(java.math.BigDecimal.ZERO) > 0 ? "up" : 
                                     successRateChange.compareTo(java.math.BigDecimal.ZERO) < 0 ? "down" : "stable";
                        change.setTrend(trend);
                    }
                    
                    // 计算执行次数变化
                    if (summary.getTotalExecutions() != null && lastWeekSummary.getTotalExecutions() != null) {
                        java.math.BigDecimal executionsChange = java.math.BigDecimal.valueOf(summary.getTotalExecutions())
                            .subtract(java.math.BigDecimal.valueOf(lastWeekSummary.getTotalExecutions()))
                            .divide(java.math.BigDecimal.valueOf(lastWeekSummary.getTotalExecutions()), 4, java.math.RoundingMode.HALF_UP)
                            .multiply(java.math.BigDecimal.valueOf(100));
                        change.setExecutionsChange(executionsChange);
                    }
                    
                    summary.setChangeFromLastWeek(change);
                }
            }
            weeklyExecution.setSummary(summary);

            // 5. 获取每日趋势数据（如果需要）
            if (includeDailyTrend == null || includeDailyTrend) {
                List<com.victor.iatms.entity.dto.DailyTrendDTO> dailyTrend = 
                    testExecutionMapper.getWeeklyDailyTrend(startTime, endTime, projectId, moduleId, environment);
                weeklyExecution.setDailyTrend(dailyTrend);
            }

            // 6. 获取项目统计排行（前5）
            List<com.victor.iatms.entity.dto.ProjectStatsDTO> projectStats = 
                testExecutionMapper.getWeeklyProjectStats(startTime, endTime, projectId, environment);
            weeklyExecution.setProjectStats(projectStats);

            // 7. 获取模块统计排行（前5）
            List<com.victor.iatms.entity.dto.ModuleStatsDTO> moduleStats = 
                testExecutionMapper.getWeeklyModuleStats(startTime, endTime, projectId, moduleId, environment);
            weeklyExecution.setModuleStats(moduleStats);

            // 8. 获取主要失败原因（如果需要）
            if (includeTopFailures == null || includeTopFailures) {
                List<com.victor.iatms.entity.dto.TopFailureDTO> topFailures = 
                    testExecutionMapper.getWeeklyTopFailures(startTime, endTime, projectId, moduleId, environment, 5);
                weeklyExecution.setTopFailures(topFailures);
            }

            // 9. 获取性能指标（如果需要）
            if (includePerformance != null && includePerformance) {
                com.victor.iatms.entity.dto.PerformanceMetricsDTO performanceMetrics = 
                    testExecutionMapper.getWeeklyPerformanceMetrics(startTime, endTime, projectId, moduleId, environment);
                weeklyExecution.setPerformanceMetrics(performanceMetrics);
            }

            // 10. 构建质量趋势对比
            com.victor.iatms.entity.dto.QualityTrendDTO qualityTrend = 
                buildQualityTrend(startTime, endTime, projectId, moduleId, environment);
            weeklyExecution.setQualityTrend(qualityTrend);

            return weeklyExecution;

        } catch (RuntimeException e) {
            log.error("获取近七天测试执行情况失败: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("获取近七天测试执行情况异常: {}", e.getMessage(), e);
            throw new RuntimeException("获取近七天测试执行情况失败");
        }
    }

    /**
     * 构建质量趋势对比
     */
    private com.victor.iatms.entity.dto.QualityTrendDTO buildQualityTrend(
            java.time.LocalDateTime startTime, java.time.LocalDateTime endTime,
            Integer projectId, Integer moduleId, String environment) {
        
        com.victor.iatms.entity.dto.QualityTrendDTO qualityTrend = 
            new com.victor.iatms.entity.dto.QualityTrendDTO();

        // 获取本周每日成功率
        List<com.victor.iatms.entity.dto.DailyTrendDTO> currentWeekTrend = 
            testExecutionMapper.getWeeklyDailyTrend(startTime, endTime, projectId, moduleId, environment);
        
        List<java.math.BigDecimal> currentWeekSuccess = new java.util.ArrayList<>();
        for (com.victor.iatms.entity.dto.DailyTrendDTO trend : currentWeekTrend) {
            if (trend.getSuccessRate() != null) {
                currentWeekSuccess.add(trend.getSuccessRate());
            }
        }
        qualityTrend.setCurrentWeekSuccess(currentWeekSuccess);

        // 获取上周同期数据
        java.time.LocalDateTime lastWeekStart = startTime.minusDays(7);
        java.time.LocalDateTime lastWeekEnd = endTime.minusDays(7);
        
        List<com.victor.iatms.entity.dto.DailyTrendDTO> lastWeekTrend = 
            testExecutionMapper.getWeeklyDailyTrend(lastWeekStart, lastWeekEnd, projectId, moduleId, environment);
        
        List<java.math.BigDecimal> lastWeekSuccess = new java.util.ArrayList<>();
        for (com.victor.iatms.entity.dto.DailyTrendDTO trend : lastWeekTrend) {
            if (trend.getSuccessRate() != null) {
                lastWeekSuccess.add(trend.getSuccessRate());
            }
        }
        qualityTrend.setLastWeekSuccess(lastWeekSuccess);

        // 判断是否有改善（简化实现）
        boolean improvement = false;
        if (!currentWeekSuccess.isEmpty() && !lastWeekSuccess.isEmpty()) {
            java.math.BigDecimal currentAvg = currentWeekSuccess.stream()
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
                .divide(java.math.BigDecimal.valueOf(currentWeekSuccess.size()), 2, java.math.RoundingMode.HALF_UP);
            
            java.math.BigDecimal lastAvg = lastWeekSuccess.stream()
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
                .divide(java.math.BigDecimal.valueOf(lastWeekSuccess.size()), 2, java.math.RoundingMode.HALF_UP);
            
            improvement = currentAvg.compareTo(lastAvg) > 0;
        }
        qualityTrend.setImprovement(improvement);

        return qualityTrend;
    }

    @Override
    public com.victor.iatms.entity.dto.DashboardSummaryDTO getDashboardSummary(
            String timeRange, Boolean includeRecentActivity, Boolean includePendingTasks, 
            Boolean includeQuickActions, Integer userId) {
        try {
            // 1. 计算时间范围
            java.time.LocalDateTime endTime = java.time.LocalDateTime.now();
            java.time.LocalDateTime startTime;
            
            switch (timeRange != null ? timeRange : "7d") {
                case "1d":
                    startTime = endTime.minusDays(1);
                    break;
                case "7d":
                    startTime = endTime.minusDays(7);
                    break;
                case "30d":
                    startTime = endTime.minusDays(30);
                    break;
                default:
                    startTime = endTime.minusDays(7);
            }

            // 2. 构建个人测试概况DTO
            com.victor.iatms.entity.dto.DashboardSummaryDTO dashboardSummary = 
                new com.victor.iatms.entity.dto.DashboardSummaryDTO();

            // 3. 获取用户基本信息（简化，不查数据库）
            dashboardSummary.setUserInfo(null);

            // 4. 获取执行统计信息（简化查询）
            try {
                com.victor.iatms.entity.dto.ExecutionStatsDTO executionStats = 
                    testExecutionMapper.getUserExecutionStats();
                if (executionStats != null) {
                    executionStats.setTrend("up");
                    executionStats.setChangePercent(new java.math.BigDecimal("3.2"));
                }
                dashboardSummary.setExecutionStats(executionStats);
            } catch (Exception e) {
                log.warn("获取执行统计失败: {}", e.getMessage());
                dashboardSummary.setExecutionStats(null);
            }

            // 5. 获取项目统计概览（简化查询）
            try {
                List<com.victor.iatms.entity.dto.ProjectStatsDTO> projectStats = 
                    testExecutionMapper.getUserProjectStats();
                dashboardSummary.setProjectStats(projectStats);
            } catch (Exception e) {
                log.warn("获取项目统计失败: {}", e.getMessage());
                dashboardSummary.setProjectStats(new java.util.ArrayList<>());
            }

            // 6. 获取最近活动记录
            try {
                List<com.victor.iatms.entity.po.Log> logs = logMapper.getUserRecentActivity(userId, 10);
                List<com.victor.iatms.entity.dto.RecentActivityDTO> recentActivities = new java.util.ArrayList<>();

                if (logs != null) {
                    for (com.victor.iatms.entity.po.Log log : logs) {
                        com.victor.iatms.entity.dto.RecentActivityDTO activity = new com.victor.iatms.entity.dto.RecentActivityDTO();
                        activity.setActivityId(log.getLogId());
                        activity.setType(log.getOperationType());
                        activity.setDescription(log.getDescription());
                        activity.setTargetId(log.getTargetId());
                        activity.setTargetName(log.getTargetName());
                        activity.setTimestamp(log.getTimestamp() != null ? log.getTimestamp().toString() : null);
                        recentActivities.add(activity);
                    }
                }
                dashboardSummary.setRecentActivity(recentActivities);
            } catch (Exception e) {
                log.warn("获取最近活动失败: {}", e.getMessage());
                dashboardSummary.setRecentActivity(new java.util.ArrayList<>());
            }

            // 7. 获取待办事项（简化，不查数据库）
            dashboardSummary.setPendingTasks(new java.util.ArrayList<>());

            // 8. 获取快捷操作
            try {
                List<com.victor.iatms.entity.dto.QuickActionDTO> quickActions = buildQuickActions();
                dashboardSummary.setQuickActions(quickActions);
            } catch (Exception e) {
                log.warn("获取快捷操作失败: {}", e.getMessage());
                dashboardSummary.setQuickActions(new java.util.ArrayList<>());
            }

            // 9. 获取系统状态信息
            try {
                com.victor.iatms.entity.dto.SystemStatusDTO systemStatus = testExecutionMapper.getSystemStatus();

                // 补充实时系统监控数据
                if (systemStatus == null) {
                    systemStatus = new com.victor.iatms.entity.dto.SystemStatusDTO();
                }

                // 设置真实的CPU、内存、磁盘使用率
                systemStatus.setCpuUsage(com.victor.iatms.utils.SystemMonitorUtils.getCpuUsage());
                systemStatus.setMemoryUsage(com.victor.iatms.utils.SystemMonitorUtils.getMemoryUsage());
                systemStatus.setDiskUsage(com.victor.iatms.utils.SystemMonitorUtils.getDiskUsage());
                systemStatus.setOsName(com.victor.iatms.utils.SystemMonitorUtils.getOsName());
                systemStatus.setUptime(com.victor.iatms.utils.SystemMonitorUtils.getUptime());

                // 如果数据库中没有健康状态，则使用实时计算的健康状态
                if (systemStatus.getSystemHealth() == null) {
                    systemStatus.setSystemHealth(com.victor.iatms.utils.SystemMonitorUtils.getSystemHealth());
                }

                dashboardSummary.setSystemStatus(systemStatus);
            } catch (Exception e) {
                log.warn("获取系统状态失败: {}", e.getMessage());
                dashboardSummary.setSystemStatus(null);
            }

            // 10. 获取质量健康评分（简化，不查数据库）
            dashboardSummary.setHealthScore(null);

            return dashboardSummary;

        } catch (RuntimeException e) {
            log.error("获取个人测试概况失败: {}", e.getMessage(), e);
            // 返回默认值而不是抛出异常
            com.victor.iatms.entity.dto.DashboardSummaryDTO defaultDTO = 
                new com.victor.iatms.entity.dto.DashboardSummaryDTO();
            defaultDTO.setUserInfo(null);
            defaultDTO.setExecutionStats(null);
            defaultDTO.setProjectStats(new java.util.ArrayList<>());
            defaultDTO.setRecentActivity(new java.util.ArrayList<>());
            defaultDTO.setPendingTasks(new java.util.ArrayList<>());
            defaultDTO.setQuickActions(new java.util.ArrayList<>());
            defaultDTO.setSystemStatus(null);
            defaultDTO.setHealthScore(null);
            return defaultDTO;
        } catch (Exception e) {
            log.error("获取个人测试概况异常: {}", e.getMessage(), e);
            // 返回默认值而不是抛出异常
            com.victor.iatms.entity.dto.DashboardSummaryDTO defaultDTO = 
                new com.victor.iatms.entity.dto.DashboardSummaryDTO();
            defaultDTO.setUserInfo(null);
            defaultDTO.setExecutionStats(null);
            defaultDTO.setProjectStats(new java.util.ArrayList<>());
            defaultDTO.setRecentActivity(new java.util.ArrayList<>());
            defaultDTO.setPendingTasks(new java.util.ArrayList<>());
            defaultDTO.setQuickActions(new java.util.ArrayList<>());
            defaultDTO.setSystemStatus(null);
            defaultDTO.setHealthScore(null);
            return defaultDTO;
        }
    }

    /**
     * 构建快捷操作
     */
    private List<com.victor.iatms.entity.dto.QuickActionDTO> buildQuickActions() {
        List<com.victor.iatms.entity.dto.QuickActionDTO> quickActions = new java.util.ArrayList<>();

        // 快速执行
        com.victor.iatms.entity.dto.QuickActionDTO quickExecute = 
            new com.victor.iatms.entity.dto.QuickActionDTO();
        quickExecute.setName("快速执行");
        quickExecute.setIcon("play-circle");
        quickExecute.setUrl("/quick-execute");
        quickExecute.setDescription("快速执行测试用例");
        quickActions.add(quickExecute);

        // 创建用例
        com.victor.iatms.entity.dto.QuickActionDTO createCase = 
            new com.victor.iatms.entity.dto.QuickActionDTO();
        createCase.setName("创建用例");
        createCase.setIcon("plus-circle");
        createCase.setUrl("/test-cases/create");
        createCase.setDescription("创建新的测试用例");
        quickActions.add(createCase);

        // 查看报告
        com.victor.iatms.entity.dto.QuickActionDTO viewReports = 
            new com.victor.iatms.entity.dto.QuickActionDTO();
        viewReports.setName("查看报告");
        viewReports.setIcon("bar-chart");
        viewReports.setUrl("/reports");
        viewReports.setDescription("查看测试报告");
        quickActions.add(viewReports);

        // 数据统计
        com.victor.iatms.entity.dto.QuickActionDTO statistics = 
            new com.victor.iatms.entity.dto.QuickActionDTO();
        statistics.setName("数据统计");
        statistics.setIcon("pie-chart");
        statistics.setUrl("/statistics");
        statistics.setDescription("查看数据统计");
        quickActions.add(statistics);

        return quickActions;
    }

    // ========== 测试执行记录相关辅助方法 ==========

    /**
     * 创建测试执行记录
     */
    private TestExecutionRecord createExecutionRecord(String executionScope, Integer refId, 
            String scopeName, Integer userId, String executionType, String environment) {
        TestExecutionRecord record = new TestExecutionRecord();
        record.setExecutionScope(executionScope);
        record.setRefId(refId);
        record.setScopeName(scopeName);
        record.setExecutedBy(userId);
        record.setExecutionType(executionType != null ? executionType : "manual");
        record.setEnvironment(environment != null ? environment : Constants.DEFAULT_ENVIRONMENT);
        record.setStatus("running");
        record.setStartTime(LocalDateTime.now());
        record.setTotalCases(0);
        record.setExecutedCases(0);
        record.setPassedCases(0);
        record.setFailedCases(0);
        record.setSkippedCases(0);
        record.setSuccessRate(BigDecimal.ZERO);
        record.setIsDeleted(false);
        
        return record;
    }

    /**
     * 更新执行记录为完成状态（单个测试用例）
     */
    private void updateExecutionRecordOnCompletion(TestExecutionRecord record, 
            TestCaseExecutionDTO result, Long reportId) {
        record.setEndTime(LocalDateTime.now());
        record.setDurationSeconds((int) java.time.Duration.between(
            record.getStartTime(), record.getEndTime()).toSeconds());
        record.setTotalCases(1);
        record.setExecutedCases(1);
        
        // 根据执行状态统计
        String status = result.getExecutionStatus();
        if (ExecutionStatusEnum.PASSED.getCode().equals(status)) {
            record.setPassedCases(1);
            record.setFailedCases(0);
            record.setSkippedCases(0);
            record.setSuccessRate(BigDecimal.valueOf(100.00));
        } else if (ExecutionStatusEnum.FAILED.getCode().equals(status)) {
            record.setPassedCases(0);
            record.setFailedCases(1);
            record.setSkippedCases(0);
            record.setSuccessRate(BigDecimal.ZERO);
        } else if (ExecutionStatusEnum.SKIPPED.getCode().equals(status)) {
            record.setPassedCases(0);
            record.setFailedCases(0);
            record.setSkippedCases(1);
            record.setSuccessRate(BigDecimal.ZERO);
        }
        
        record.setStatus("completed");
        
        if (reportId != null) {
            record.setReportUrl("/api/reports/" + reportId);
        }
        
        // 保存响应数据到executionConfig中，用于前端展示
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> executionConfig = new HashMap<>();
            
            // 构建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("httpStatus", result.getHttpResponseStatus());
            responseData.put("responseCode", result.getExecutionStatus());
            responseData.put("body", result.getHttpResponseBody());
            responseData.put("headers", result.getHttpResponseHeaders());
            
            // 转换断言结果
            if (result.getAssertionResults() != null) {
                List<Map<String, Object>> assertionResults = new ArrayList<>();
                for (TestCaseExecutionDTO.AssertionResultDTO assertion : result.getAssertionResults()) {
                    Map<String, Object> assertionMap = new HashMap<>();
                    assertionMap.put("assertionType", assertion.getAssertionType());
                    assertionMap.put("expectedValue", assertion.getExpectedValue());
                    assertionMap.put("actualValue", assertion.getActualValue());
                    assertionMap.put("passed", assertion.getPassed());
                    assertionMap.put("message", assertion.getMessage());
                    assertionMap.put("errorMessage", assertion.getErrorMessage());
                    assertionResults.add(assertionMap);
                }
                responseData.put("assertionResults", assertionResults);
            }
            
            // 添加提取的变量
            responseData.put("extractedVariables", result.getExtractedValues());
            
            executionConfig.put("responseData", responseData);
            record.setExecutionConfig(mapper.writeValueAsString(executionConfig));
        } catch (Exception e) {
            log.warn("保存响应数据到executionConfig失败: {}", e.getMessage());
        }
    }

    /**
     * 更新执行记录为失败状态
     */
    private void updateExecutionRecordOnFailure(TestExecutionRecord record, String errorMessage) {
        record.setEndTime(LocalDateTime.now());
        record.setDurationSeconds((int) java.time.Duration.between(
            record.getStartTime(), record.getEndTime()).toSeconds());
        record.setStatus("failed");
        record.setErrorMessage(errorMessage != null && errorMessage.length() > 500 ? 
            errorMessage.substring(0, 500) : errorMessage);
    }

    /**
     * 更新执行记录为取消状态
     */
    private void updateExecutionRecordOnCancellation(TestExecutionRecord record) {
        record.setEndTime(LocalDateTime.now());
        record.setDurationSeconds((int) java.time.Duration.between(
            record.getStartTime(), record.getEndTime()).toSeconds());
        record.setStatus("cancelled");
    }

    /**
     * 批量更新执行记录统计信息
     */
    private void updateExecutionRecordStats(TestExecutionRecord record, 
            int totalCases, int executedCases, int passedCases, 
            int failedCases, int skippedCases) {
        record.setTotalCases(totalCases);
        record.setExecutedCases(executedCases);
        record.setPassedCases(passedCases);
        record.setFailedCases(failedCases);
        record.setSkippedCases(skippedCases);
        
        if (executedCases > 0) {
            double successRate = (double) passedCases / executedCases * 100;
            record.setSuccessRate(BigDecimal.valueOf(successRate).setScale(2, java.math.RoundingMode.HALF_UP));
        } else {
            record.setSuccessRate(BigDecimal.ZERO);
        }
    }

    @Override
    public List<Map<String, Object>> getTestCaseResultsByReportId(Long reportId) {
        // 查询测试结果列表
        List<TestCaseResult> testCaseResults = testExecutionMapper.findTestCaseResultsByReportId(reportId);
        
        // 转换为Map列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (TestCaseResult result : testCaseResults) {
            Map<String, Object> map = new HashMap<>();
            map.put("resultId", result.getResultId());
            map.put("executionRecordId", result.getExecutionRecordId());
            map.put("reportId", result.getReportId());
            map.put("executionId", result.getExecutionId());
            map.put("taskType", result.getTaskType());
            map.put("refId", result.getRefId());
            map.put("fullName", result.getFullName());
            map.put("status", result.getStatus());
            map.put("duration", result.getDuration());
            map.put("startTime", result.getStartTime());
            map.put("endTime", result.getEndTime());
            map.put("failureMessage", result.getFailureMessage());
            map.put("failureTrace", result.getFailureTrace());
            map.put("failureType", result.getFailureType());
            map.put("errorCode", result.getErrorCode());
            map.put("stepsJson", result.getStepsJson());
            map.put("parametersJson", result.getParametersJson());
            map.put("attachmentsJson", result.getAttachmentsJson());
            map.put("logsLink", result.getLogsLink());
            map.put("screenshotLink", result.getScreenshotLink());
            map.put("videoLink", result.getVideoLink());
            map.put("environment", result.getEnvironment());
            map.put("browser", result.getBrowser());
            map.put("os", result.getOs());
            map.put("device", result.getDevice());
            map.put("tagsJson", result.getTagsJson());
            map.put("severity", result.getSeverity());
            map.put("priority", result.getPriority());
            map.put("retryCount", result.getRetryCount());
            map.put("flaky", result.getFlaky());
            map.put("createdAt", result.getCreatedAt());
            map.put("caseId", result.getCaseId());
            map.put("caseCode", result.getCaseCode());
            map.put("caseName", result.getCaseName());
            map.put("moduleName", result.getModuleName());
            map.put("apiName", result.getApiName());
            map.put("suiteName", result.getSuiteName());
            map.put("testLayer", result.getTestLayer());
            map.put("testType", result.getTestType());
            resultList.add(map);
        }
        
        return resultList;
    }
}

