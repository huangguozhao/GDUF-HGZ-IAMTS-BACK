package com.victor.iatms.service;

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

/**
 * 测试执行服务接口
 */
public interface TestExecutionService {

    /**
     * 执行单个测试用例
     * @param caseId 用例ID
     * @param executeDTO 执行参数
     * @param userId 当前用户ID
     * @return 执行结果
     */
    ExecutionResultDTO executeTestCase(Integer caseId, ExecuteTestCaseDTO executeDTO, Integer userId);

    /**
     * 异步执行测试用例
     * @param caseId 用例ID
     * @param executeDTO 执行参数
     * @param userId 当前用户ID
     * @return 任务信息
     */
    ExecutionResultDTO executeTestCaseAsync(Integer caseId, ExecuteTestCaseDTO executeDTO, Integer userId);

    /**
     * 查询任务状态
     * @param taskId 任务ID
     * @param userId 当前用户ID
     * @return 任务状态
     */
    ExecutionResultDTO getTaskStatus(String taskId, Integer userId);

    /**
     * 取消任务执行
     * @param taskId 任务ID
     * @param userId 当前用户ID
     * @return 是否成功
     */
    boolean cancelTask(String taskId, Integer userId);

    /**
     * 获取执行结果详情
     * @param executionId 执行ID
     * @param userId 当前用户ID
     * @return 执行结果详情
     */
    ExecutionResultDTO getExecutionResult(Long executionId, Integer userId);

    /**
     * 获取执行日志
     * @param executionId 执行ID
     * @param userId 当前用户ID
     * @return 执行日志
     */
    String getExecutionLogs(Long executionId, Integer userId);

    /**
     * 生成测试报告
     * @param executionId 执行ID
     * @param userId 当前用户ID
     * @return 报告ID
     */
    Long generateTestReport(Long executionId, Integer userId);

    // ========== 模块执行相关方法 ==========

    /**
     * 执行模块测试
     * @param moduleId 模块ID
     * @param executeDTO 执行参数
     * @param userId 用户ID
     * @return 执行结果
     */
    ModuleExecutionResultDTO executeModule(Integer moduleId, ExecuteModuleDTO executeDTO, Integer userId);

    /**
     * 异步执行模块测试
     * @param moduleId 模块ID
     * @param executeDTO 执行参数
     * @param userId 用户ID
     * @return 任务信息
     */
    ModuleExecutionResultDTO executeModuleAsync(Integer moduleId, ExecuteModuleDTO executeDTO, Integer userId);

    /**
     * 查询模块任务状态
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 任务状态
     */
    ModuleExecutionResultDTO getModuleTaskStatus(String taskId, Integer userId);

    /**
     * 取消模块任务执行
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 是否取消成功
     */
    boolean cancelModuleTask(String taskId, Integer userId);

    // ========== 项目执行相关方法 ==========

    /**
     * 执行项目测试
     * @param projectId 项目ID
     * @param executeDTO 执行参数
     * @param userId 用户ID
     * @return 执行结果
     */
    ProjectExecutionResultDTO executeProject(Integer projectId, ExecuteProjectDTO executeDTO, Integer userId);

    /**
     * 异步执行项目测试
     * @param projectId 项目ID
     * @param executeDTO 执行参数
     * @param userId 用户ID
     * @return 任务信息
     */
    ProjectExecutionResultDTO executeProjectAsync(Integer projectId, ExecuteProjectDTO executeDTO, Integer userId);

    /**
     * 查询项目任务状态
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 任务状态
     */
    ProjectExecutionResultDTO getProjectTaskStatus(String taskId, Integer userId);

        /**
         * 取消项目任务执行
         * @param taskId 任务ID
         * @param userId 用户ID
         * @return 是否取消成功
         */
        boolean cancelProjectTask(String taskId, Integer userId);

        // ========== 接口执行相关方法 ==========

        /**
         * 执行接口测试
         * @param apiId 接口ID
         * @param executeDTO 执行参数
         * @param userId 用户ID
         * @return 执行结果
         */
        ApiExecutionResultDTO executeApi(Integer apiId, ExecuteApiDTO executeDTO, Integer userId);

        /**
         * 异步执行接口测试
         * @param apiId 接口ID
         * @param executeDTO 执行参数
         * @param userId 用户ID
         * @return 任务信息
         */
        ApiExecutionResultDTO executeApiAsync(Integer apiId, ExecuteApiDTO executeDTO, Integer userId);

        /**
         * 查询接口任务状态
         * @param taskId 任务ID
         * @param userId 用户ID
         * @return 任务状态
         */
        ApiExecutionResultDTO getApiTaskStatus(String taskId, Integer userId);

        /**
         * 取消接口任务执行
         * @param taskId 任务ID
         * @param userId 用户ID
         * @return 是否取消成功
         */
        boolean cancelApiTask(String taskId, Integer userId);

        // ========== 测试套件执行相关方法 ==========

        /**
         * 执行测试套件
         * @param suiteId 测试套件ID
         * @param executeDTO 执行参数
         * @param userId 用户ID
         * @return 执行结果
         */
        TestSuiteExecutionResultDTO executeTestSuite(Integer suiteId, ExecuteTestSuiteDTO executeDTO, Integer userId);

        /**
         * 异步执行测试套件
         * @param suiteId 测试套件ID
         * @param executeDTO 执行参数
         * @param userId 用户ID
         * @return 任务信息
         */
        TestSuiteExecutionResultDTO executeTestSuiteAsync(Integer suiteId, ExecuteTestSuiteDTO executeDTO, Integer userId);

        /**
         * 查询测试套件任务状态
         * @param taskId 任务ID
         * @param userId 用户ID
         * @return 任务状态
         */
        TestSuiteExecutionResultDTO getTestSuiteTaskStatus(String taskId, Integer userId);

        /**
         * 取消测试套件任务执行
         * @param taskId 任务ID
         * @param userId 用户ID
         * @return 是否取消成功
         */
        boolean cancelTestSuiteTask(String taskId, Integer userId);

        // ========== 测试结果查询相关方法 ==========

        /**
         * 分页获取测试结果列表
         * @param query 查询参数
         * @param userId 用户ID
         * @return 测试结果分页数据
         */
        com.victor.iatms.entity.dto.TestResultPageResultDTO getTestResults(
            com.victor.iatms.entity.query.TestResultQuery query, Integer userId);

        /**
         * 获取测试结果详情
         * @param resultId 结果ID
         * @param includeSteps 是否包含测试步骤
         * @param includeAssertions 是否包含断言详情
         * @param includeArtifacts 是否包含附件信息
         * @param includeEnvironment 是否包含环境信息
         * @param userId 用户ID
         * @return 测试结果详情
         */
        com.victor.iatms.entity.dto.TestResultDetailDTO getTestResultDetail(
            Long resultId, Boolean includeSteps, Boolean includeAssertions, 
            Boolean includeArtifacts, Boolean includeEnvironment, Integer userId);

        /**
         * 获取测试统计信息
         * @param timeRange 时间范围
         * @param startTime 自定义开始时间
         * @param endTime 自定义结束时间
         * @param projectId 项目ID
         * @param moduleId 模块ID
         * @param apiId 接口ID
         * @param environment 环境
         * @param groupBy 分组方式
         * @param includeTrend 是否包含趋势数据
         * @param includeComparison 是否包含同比环比数据
         * @param userId 用户ID
         * @return 统计信息
         */
        com.victor.iatms.entity.dto.TestStatisticsDTO getTestStatistics(
            String timeRange, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime,
            Integer projectId, Integer moduleId, Integer apiId, String environment,
            String groupBy, Boolean includeTrend, Boolean includeComparison, Integer userId);

        /**
         * 获取近七天测试执行情况
         * @param projectId 项目ID
         * @param moduleId 模块ID
         * @param environment 环境
         * @param includeDailyTrend 是否包含每日趋势数据
         * @param includeTopFailures 是否包含主要失败原因
         * @param includePerformance 是否包含性能指标
         * @param userId 用户ID
         * @return 近七天执行情况
         */
        com.victor.iatms.entity.dto.WeeklyExecutionDTO getWeeklyExecution(
            Integer projectId, Integer moduleId, String environment,
            Boolean includeDailyTrend, Boolean includeTopFailures, Boolean includePerformance, Integer userId);

        /**
         * 获取个人测试概况
         * @param timeRange 时间范围
         * @param includeRecentActivity 是否包含最近活动
         * @param includePendingTasks 是否包含待办事项
         * @param includeQuickActions 是否包含快捷操作
         * @param userId 用户ID
         * @return 个人测试概况
         */
        com.victor.iatms.entity.dto.DashboardSummaryDTO getDashboardSummary(
            String timeRange, Boolean includeRecentActivity, Boolean includePendingTasks, 
            Boolean includeQuickActions, Integer userId);
    }
