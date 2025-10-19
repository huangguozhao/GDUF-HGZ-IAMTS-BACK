package com.victor.iatms.mappers;

import com.victor.iatms.entity.dto.TestCaseExecutionDTO;
import com.victor.iatms.entity.po.Api;
import com.victor.iatms.entity.po.Module;
import com.victor.iatms.entity.po.Project;
import com.victor.iatms.entity.po.TestCase;
import com.victor.iatms.entity.po.TestCaseResult;
import com.victor.iatms.entity.po.TestReportSummary;
import com.victor.iatms.entity.po.TestSuite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 测试执行数据访问层
 */
@Mapper
public interface TestExecutionMapper {

    /**
     * 根据用例ID查询用例执行信息
     * @param caseId 用例ID
     * @return 用例执行信息
     */
    TestCaseExecutionDTO findTestCaseForExecution(@Param("caseId") Integer caseId);

    /**
     * 插入测试结果
     * @param testCaseResult 测试结果
     * @return 影响行数
     */
    int insertTestCaseResult(TestCaseResult testCaseResult);

    /**
     * 插入测试报告汇总
     * @param reportSummary 报告汇总
     * @return 影响行数
     */
    int insertTestReportSummary(TestReportSummary reportSummary);

    /**
     * 更新测试报告汇总
     * @param reportSummary 报告汇总
     * @return 影响行数
     */
    int updateTestReportSummary(TestReportSummary reportSummary);

    /**
     * 根据报告ID查询测试结果列表
     * @param reportId 报告ID
     * @return 测试结果列表
     */
    List<TestCaseResult> findTestCaseResultsByReportId(@Param("reportId") Long reportId);

    /**
     * 根据执行ID查询测试结果
     * @param executionId 执行ID
     * @return 测试结果
     */
    TestCaseResult findTestCaseResultByExecutionId(@Param("executionId") Long executionId);

    /**
     * 根据报告ID查询报告汇总
     * @param reportId 报告ID
     * @return 报告汇总
     */
    TestReportSummary findTestReportSummaryById(@Param("reportId") Long reportId);

    /**
     * 查询最新的报告汇总
     * @param projectId 项目ID
     * @param environment 环境
     * @return 报告汇总
     */
    TestReportSummary findLatestReportSummary(@Param("projectId") Integer projectId, 
                                            @Param("environment") String environment);

    /**
     * 更新测试结果
     * @param testCaseResult 测试结果
     * @return 影响行数
     */
    int updateTestCaseResult(TestCaseResult testCaseResult);

    // ========== 模块执行相关方法 ==========

    /**
     * 根据ID查询模块
     * @param moduleId 模块ID
     * @return 模块信息
     */
    Module findModuleById(@Param("moduleId") Integer moduleId);

    /**
     * 查询模块下的所有接口的测试用例
     * @param moduleId 模块ID
     * @param priorityList 优先级过滤列表
     * @param tagsList 标签过滤列表
     * @param enabledOnly 是否只查询启用的用例
     * @return 测试用例列表
     */
    List<TestCase> findTestCasesByModuleId(@Param("moduleId") Integer moduleId,
                                          @Param("priorityList") List<String> priorityList,
                                          @Param("tagsList") List<String> tagsList,
                                          @Param("enabledOnly") Boolean enabledOnly);

    /**
     * 统计模块下的测试用例数量
     * @param moduleId 模块ID
     * @param priorityList 优先级过滤列表
     * @param tagsList 标签过滤列表
     * @param enabledOnly 是否只统计启用的用例
     * @return 用例数量
     */
    Integer countTestCasesByModuleId(@Param("moduleId") Integer moduleId,
                                    @Param("priorityList") List<String> priorityList,
                                    @Param("tagsList") List<String> tagsList,
                                    @Param("enabledOnly") Boolean enabledOnly);

    /**
     * 查询模块下的接口列表
     * @param moduleId 模块ID
     * @return 接口ID列表
     */
    List<Integer> findApiIdsByModuleId(@Param("moduleId") Integer moduleId);

    // ========== 项目执行相关方法 ==========

    /**
     * 根据ID查询项目
     * @param projectId 项目ID
     * @return 项目信息
     */
    Project findProjectById(@Param("projectId") Integer projectId);

    /**
     * 查询项目下的所有模块
     * @param projectId 项目ID
     * @param moduleIds 指定模块ID列表（可选）
     * @param status 模块状态过滤（可选）
     * @return 模块列表
     */
    List<Module> findModulesByProjectId(@Param("projectId") Integer projectId,
                                       @Param("moduleIds") List<Integer> moduleIds,
                                       @Param("status") String status);

    /**
     * 查询项目下的所有测试用例
     * @param projectId 项目ID
     * @param moduleIds 指定模块ID列表（可选）
     * @param priorityList 优先级过滤列表（可选）
     * @param tagsList 标签过滤列表（可选）
     * @param enabledOnly 是否只查询启用的用例（可选）
     * @return 测试用例列表
     */
    List<TestCase> findTestCasesByProjectId(@Param("projectId") Integer projectId,
                                           @Param("moduleIds") List<Integer> moduleIds,
                                           @Param("priorityList") List<String> priorityList,
                                           @Param("tagsList") List<String> tagsList,
                                           @Param("enabledOnly") Boolean enabledOnly);

    /**
     * 统计项目下的测试用例数量
     * @param projectId 项目ID
     * @param moduleIds 指定模块ID列表（可选）
     * @param priorityList 优先级过滤列表（可选）
     * @param tagsList 标签过滤列表（可选）
     * @param enabledOnly 是否只统计启用的用例（可选）
     * @return 用例数量
     */
    Integer countTestCasesByProjectId(@Param("projectId") Integer projectId,
                                     @Param("moduleIds") List<Integer> moduleIds,
                                     @Param("priorityList") List<String> priorityList,
                                     @Param("tagsList") List<String> tagsList,
                                     @Param("enabledOnly") Boolean enabledOnly);

        /**
         * 查询项目下的模块ID列表
         * @param projectId 项目ID
         * @param moduleIds 指定模块ID列表（可选）
         * @param status 模块状态过滤（可选）
         * @return 模块ID列表
         */
        List<Integer> findModuleIdsByProjectId(@Param("projectId") Integer projectId,
                                              @Param("moduleIds") List<Integer> moduleIds,
                                              @Param("status") String status);

        // ========== 接口执行相关方法 ==========

        /**
         * 根据ID查询接口
         * @param apiId 接口ID
         * @return 接口信息
         */
        Api findApiById(@Param("apiId") Integer apiId);

        /**
         * 查询接口下的所有测试用例
         * @param apiId 接口ID
         * @param priorityList 优先级过滤列表（可选）
         * @param tagsList 标签过滤列表（可选）
         * @param enabledOnly 是否只查询启用的用例（可选）
         * @param executionOrder 执行顺序（可选）
         * @return 测试用例列表
         */
        List<TestCase> findTestCasesByApiId(@Param("apiId") Integer apiId,
                                           @Param("priorityList") List<String> priorityList,
                                           @Param("tagsList") List<String> tagsList,
                                           @Param("enabledOnly") Boolean enabledOnly,
                                           @Param("executionOrder") String executionOrder);

        /**
         * 统计接口下的测试用例数量
         * @param apiId 接口ID
         * @param priorityList 优先级过滤列表（可选）
         * @param tagsList 标签过滤列表（可选）
         * @param enabledOnly 是否只统计启用的用例（可选）
         * @return 用例数量
         */
        Integer countTestCasesByApiId(@Param("apiId") Integer apiId,
                                     @Param("priorityList") List<String> priorityList,
                                     @Param("tagsList") List<String> tagsList,
                                     @Param("enabledOnly") Boolean enabledOnly);

        // ========== 测试套件执行相关方法 ==========

        /**
         * 根据ID查询测试套件
         * @param suiteId 测试套件ID
         * @return 测试套件信息
         */
        TestSuite findTestSuiteById(@Param("suiteId") Integer suiteId);

        /**
         * 查询测试套件下的所有测试用例
         * @param suiteId 测试套件ID
         * @param priorityList 优先级过滤列表（可选）
         * @param tagsList 标签过滤列表（可选）
         * @param enabledOnly 是否只查询启用的用例（可选）
         * @return 测试用例列表
         */
        List<TestCase> findTestCasesBySuiteId(@Param("suiteId") Integer suiteId,
                                             @Param("priorityList") List<String> priorityList,
                                             @Param("tagsList") List<String> tagsList,
                                             @Param("enabledOnly") Boolean enabledOnly);

        /**
         * 统计测试套件下的测试用例数量
         * @param suiteId 测试套件ID
         * @param priorityList 优先级过滤列表（可选）
         * @param tagsList 标签过滤列表（可选）
         * @param enabledOnly 是否只统计启用的用例（可选）
         * @return 用例数量
         */
        Integer countTestCasesBySuiteId(@Param("suiteId") Integer suiteId,
                                       @Param("priorityList") List<String> priorityList,
                                       @Param("tagsList") List<String> tagsList,
                                       @Param("enabledOnly") Boolean enabledOnly);

        // ========== 测试结果查询相关方法 ==========

        /**
         * 分页查询测试结果列表
         * @param query 查询参数
         * @return 测试结果列表
         */
        List<TestCaseResult> findTestResults(@Param("query") com.victor.iatms.entity.query.TestResultQuery query);

        /**
         * 统计测试结果总数
         * @param query 查询参数
         * @return 总数
         */
        Long countTestResults(@Param("query") com.victor.iatms.entity.query.TestResultQuery query);

        /**
         * 统计测试结果摘要
         * @param query 查询参数
         * @return 统计摘要
         */
        com.victor.iatms.entity.dto.TestResultSummaryDTO getTestResultSummary(@Param("query") com.victor.iatms.entity.query.TestResultQuery query);

        /**
         * 根据结果ID查询测试结果详情
         * @param resultId 结果ID
         * @return 测试结果详情
         */
        TestCaseResult findTestResultById(@Param("resultId") Long resultId);

        // ========== 测试统计相关方法 ==========

        /**
         * 获取统计摘要
         * @param startTime 开始时间
         * @param endTime 结束时间
         * @param projectId 项目ID（可选）
         * @param moduleId 模块ID（可选）
         * @param apiId 接口ID（可选）
         * @param environment 环境（可选）
         * @return 统计摘要
         */
        com.victor.iatms.entity.dto.StatisticsSummaryDTO getStatisticsSummary(
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime,
            @Param("projectId") Integer projectId,
            @Param("moduleId") Integer moduleId,
            @Param("apiId") Integer apiId,
            @Param("environment") String environment);

        /**
         * 按时间分组获取趋势数据
         * @param startTime 开始时间
         * @param endTime 结束时间
         * @param groupBy 分组方式（hour/day/week/month）
         * @param projectId 项目ID（可选）
         * @param moduleId 模块ID（可选）
         * @param apiId 接口ID（可选）
         * @param environment 环境（可选）
         * @return 趋势数据列表
         */
        List<com.victor.iatms.entity.dto.TrendDataDTO> getTrendData(
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime,
            @Param("groupBy") String groupBy,
            @Param("projectId") Integer projectId,
            @Param("moduleId") Integer moduleId,
            @Param("apiId") Integer apiId,
            @Param("environment") String environment);

        /**
         * 按指定维度分组获取统计数据
         * @param startTime 开始时间
         * @param endTime 结束时间
         * @param groupBy 分组维度（priority/severity/project/module/api）
         * @param projectId 项目ID（可选）
         * @param moduleId 模块ID（可选）
         * @param apiId 接口ID（可选）
         * @param environment 环境（可选）
         * @return 分组数据列表
         */
        List<com.victor.iatms.entity.dto.GroupDataDTO> getGroupData(
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime,
            @Param("groupBy") String groupBy,
            @Param("projectId") Integer projectId,
            @Param("moduleId") Integer moduleId,
            @Param("apiId") Integer apiId,
            @Param("environment") String environment);

        /**
         * 获取主要问题统计（Top失败类型）
         * @param startTime 开始时间
         * @param endTime 结束时间
         * @param projectId 项目ID（可选）
         * @param moduleId 模块ID（可选）
         * @param apiId 接口ID（可选）
         * @param environment 环境（可选）
         * @param limit 返回数量限制
         * @return 问题统计列表
         */
        List<com.victor.iatms.entity.dto.TopIssueDTO> getTopIssues(
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime,
            @Param("projectId") Integer projectId,
            @Param("moduleId") Integer moduleId,
            @Param("apiId") Integer apiId,
            @Param("environment") String environment,
            @Param("limit") Integer limit);

        // ========== 近七天执行情况相关方法 ==========

        /**
         * 获取近七天总体统计摘要
         * @param startTime 开始时间（7天前）
         * @param endTime 结束时间（当天）
         * @param projectId 项目ID（可选）
         * @param moduleId 模块ID（可选）
         * @param environment 环境（可选）
         * @return 总体统计摘要
         */
        com.victor.iatms.entity.dto.SummaryDTO getWeeklySummary(
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime,
            @Param("projectId") Integer projectId,
            @Param("moduleId") Integer moduleId,
            @Param("environment") String environment);

        /**
         * 获取近七天每日趋势数据
         * @param startTime 开始时间
         * @param endTime 结束时间
         * @param projectId 项目ID（可选）
         * @param moduleId 模块ID（可选）
         * @param environment 环境（可选）
         * @return 每日趋势数据
         */
        List<com.victor.iatms.entity.dto.DailyTrendDTO> getWeeklyDailyTrend(
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime,
            @Param("projectId") Integer projectId,
            @Param("moduleId") Integer moduleId,
            @Param("environment") String environment);

        /**
         * 获取项目统计排行（前5）
         * @param startTime 开始时间
         * @param endTime 结束时间
         * @param projectId 项目ID（可选）
         * @param environment 环境（可选）
         * @return 项目统计排行
         */
        List<com.victor.iatms.entity.dto.ProjectStatsDTO> getWeeklyProjectStats(
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime,
            @Param("projectId") Integer projectId,
            @Param("environment") String environment);

        /**
         * 获取模块统计排行（前5）
         * @param startTime 开始时间
         * @param endTime 结束时间
         * @param projectId 项目ID（可选）
         * @param moduleId 模块ID（可选）
         * @param environment 环境（可选）
         * @return 模块统计排行
         */
        List<com.victor.iatms.entity.dto.ModuleStatsDTO> getWeeklyModuleStats(
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime,
            @Param("projectId") Integer projectId,
            @Param("moduleId") Integer moduleId,
            @Param("environment") String environment);

        /**
         * 获取近七天主要失败原因
         * @param startTime 开始时间
         * @param endTime 结束时间
         * @param projectId 项目ID（可选）
         * @param moduleId 模块ID（可选）
         * @param environment 环境（可选）
         * @param limit 返回数量限制
         * @return 主要失败原因
         */
        List<com.victor.iatms.entity.dto.TopFailureDTO> getWeeklyTopFailures(
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime,
            @Param("projectId") Integer projectId,
            @Param("moduleId") Integer moduleId,
            @Param("environment") String environment,
            @Param("limit") Integer limit);

        /**
         * 获取近七天性能指标
         * @param startTime 开始时间
         * @param endTime 结束时间
         * @param projectId 项目ID（可选）
         * @param moduleId 模块ID（可选）
         * @param environment 环境（可选）
         * @return 性能指标
         */
        com.victor.iatms.entity.dto.PerformanceMetricsDTO getWeeklyPerformanceMetrics(
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime,
            @Param("projectId") Integer projectId,
            @Param("moduleId") Integer moduleId,
            @Param("environment") String environment);

        /**
         * 获取上周同期数据（用于对比）
         * @param lastWeekStart 上周开始时间
         * @param lastWeekEnd 上周结束时间
         * @param projectId 项目ID（可选）
         * @param moduleId 模块ID（可选）
         * @param environment 环境（可选）
         * @return 上周统计摘要
         */
        com.victor.iatms.entity.dto.SummaryDTO getLastWeekSummary(
            @Param("lastWeekStart") java.time.LocalDateTime lastWeekStart,
            @Param("lastWeekEnd") java.time.LocalDateTime lastWeekEnd,
            @Param("projectId") Integer projectId,
            @Param("moduleId") Integer moduleId,
            @Param("environment") String environment);

        // ========== 个人测试概况相关方法 ==========

        /**
         * 获取用户基本信息
         * @param userId 用户ID
         * @return 用户基本信息
         */
        com.victor.iatms.entity.dto.UserInfoDTO getUserInfo(@Param("userId") Integer userId);

        /**
         * 获取用户执行统计信息
         * @param userId 用户ID
         * @param startTime 开始时间
         * @param endTime 结束时间
         * @return 执行统计信息
         */
        com.victor.iatms.entity.dto.ExecutionStatsDTO getUserExecutionStats(
            @Param("userId") Integer userId,
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime);

        /**
         * 获取用户项目统计概览
         * @param userId 用户ID
         * @param startTime 开始时间
         * @param endTime 结束时间
         * @return 项目统计概览
         */
        List<com.victor.iatms.entity.dto.ProjectStatsDTO> getUserProjectStats(
            @Param("userId") Integer userId,
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime);

        /**
         * 获取用户最近活动记录
         * @param userId 用户ID
         * @param limit 返回数量限制
         * @return 最近活动记录
         */
        List<com.victor.iatms.entity.dto.RecentActivityDTO> getUserRecentActivity(
            @Param("userId") Integer userId,
            @Param("limit") Integer limit);

        /**
         * 获取用户待办事项
         * @param userId 用户ID
         * @return 待办事项列表
         */
        List<com.victor.iatms.entity.dto.PendingTaskDTO> getUserPendingTasks(@Param("userId") Integer userId);

        /**
         * 获取系统状态信息
         * @return 系统状态信息
         */
        com.victor.iatms.entity.dto.SystemStatusDTO getSystemStatus();

        /**
         * 获取用户质量健康评分
         * @param userId 用户ID
         * @param startTime 开始时间
         * @param endTime 结束时间
         * @return 质量健康评分
         */
        com.victor.iatms.entity.dto.HealthScoreDTO getUserHealthScore(
            @Param("userId") Integer userId,
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime);
    }
