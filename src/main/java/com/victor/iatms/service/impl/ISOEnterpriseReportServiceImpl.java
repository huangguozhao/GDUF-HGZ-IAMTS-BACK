package com.victor.iatms.service.impl;

import com.victor.iatms.entity.dto.ISOEnterpriseReportDTO;
import com.victor.iatms.entity.dto.ReportExportResponseDTO;
import com.victor.iatms.entity.po.TestReportSummary;
import com.victor.iatms.mappers.ReportMapper;
import com.victor.iatms.service.ISOEnterpriseReportService;
import com.victor.iatms.service.ReportService;
import com.victor.iatms.utils.ISOEnterpriseHTMLBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ISO/IEC/IEEE 29119标准企业级报告服务实现
 * 
 * @author Victor
 * @since 2024-10-26
 */
@Slf4j
@Service
public class ISOEnterpriseReportServiceImpl implements ISOEnterpriseReportService {
    
    @Autowired
    private ReportMapper reportMapper;
    
    @Autowired
    private ReportService reportService;
    
    @Override
    public Resource exportISOEnterpriseReport(Long reportId, String locale) {
        log.info("开始导出ISO标准企业级报告: reportId={}, locale={}", reportId, locale);
        
        try {
            // 构建报告数据
            ISOEnterpriseReportDTO reportData = buildISOEnterpriseReportData(reportId);
            
            // 生成HTML
            ISOEnterpriseHTMLBuilder htmlBuilder = new ISOEnterpriseHTMLBuilder(reportData, locale);
            String htmlContent = htmlBuilder.build();
            
            // 创建资源
            byte[] content = htmlContent.getBytes(StandardCharsets.UTF_8);
            String fileName = generateFileName(reportData);
            
            log.info("ISO标准企业级报告生成成功: reportId={}, fileName={}", reportId, fileName);
            
            return new ByteArrayResource(content) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            };
            
        } catch (Exception e) {
            log.error("导出ISO标准企业级报告失败: reportId={}", reportId, e);
            throw new RuntimeException("导出ISO标准企业级报告失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ISOEnterpriseReportDTO buildISOEnterpriseReportData(Long reportId) {
        log.info("开始构建ISO标准企业级报告数据: reportId={}", reportId);
        
        ISOEnterpriseReportDTO dto = new ISOEnterpriseReportDTO();
        
        // 获取报告基本信息
        ReportExportResponseDTO.ReportSummaryInfoDTO summary = reportMapper.selectReportExportData(reportId);
        if (summary == null) {
            throw new IllegalArgumentException("报告不存在: reportId=" + reportId);
        }
        
        // 获取测试结果详情（包含失败信息）
        List<ReportExportResponseDTO.TestCaseResultDTO> testResults = 
            reportMapper.selectReportTestResults(reportId, true, false, true);
        
        log.info("查询到测试结果数: {}", testResults != null ? testResults.size() : 0);
        
        // ==================== 模块1: 报告头信息 ====================
        buildDocumentHeader(dto, summary);
        
        // ==================== 模块2: 执行摘要 ====================
        buildExecutiveSummary(dto, summary, testResults);
        
        // ==================== 模块3: 测试范围与背景 ====================
        buildTestScope(dto, summary, testResults);
        
        // ==================== 模块4: 测试环境 ====================
        buildTestEnvironment(dto, summary, testResults);
        
        // ==================== 模块5: 测试结果与度量 ====================
        buildTestResultsAndMetrics(dto, summary, testResults);
        
        // ==================== 模块6: 详细缺陷信息 ====================
        buildDetailedDefects(dto, testResults);
        
        // ==================== 模块7: 挑战与风险 ====================
        buildChallengesAndRisks(dto, summary, testResults);
        
        // ==================== 模块8: 结论与建议 ====================
        buildConclusionAndRecommendations(dto, summary, testResults);
        
        // 设置基础统计数据
        dto.setTotalCases(summary.getTotalCases());
        dto.setExecutedCases(summary.getExecutedCases());
        dto.setPassedCases(summary.getPassedCases());
        dto.setFailedCases(summary.getFailedCases());
        dto.setBrokenCases(summary.getBrokenCases());
        dto.setSkippedCases(summary.getSkippedCases());
        dto.setSuccessRate(summary.getSuccessRate());
        dto.setDuration(summary.getDuration());
        dto.setEnvironment(summary.getEnvironment());
        dto.setReportType(summary.getReportType());
        
        log.info("ISO标准企业级报告数据构建完成: reportId={}", reportId);
        
        return dto;
    }
    
    /**
     * 构建报告头信息
     */
    private void buildDocumentHeader(ISOEnterpriseReportDTO dto, 
                                     ReportExportResponseDTO.ReportSummaryInfoDTO summary) {
        // 报告标题
        String reportTitle = String.format("【%s】%s %s测试报告", 
            summary.getProjectName(),
            generateVersion(summary.getStartTime()),
            formatReportType(summary.getReportType()));
        dto.setReportTitle(reportTitle);
        
        dto.setProjectName(summary.getProjectName());
        dto.setVersion(generateVersion(summary.getStartTime()));
        dto.setReportNumber(generateReportNumber(summary.getReportId(), summary.getStartTime()));
        dto.setTestStartDate(summary.getStartTime());
        dto.setTestEndDate(summary.getEndTime());
        dto.setReportDate(LocalDateTime.now());
        dto.setTesterName("测试工程师"); // TODO: 从用户信息获取
        dto.setReviewerName("测试经理"); // TODO: 从用户信息获取
        dto.setReportStatus("approved"); // 已批准
        
        log.debug("报告头信息构建完成: {}", reportTitle);
    }
    
    /**
     * 构建执行摘要
     */
    private void buildExecutiveSummary(ISOEnterpriseReportDTO dto,
                                       ReportExportResponseDTO.ReportSummaryInfoDTO summary,
                                       List<ReportExportResponseDTO.TestCaseResultDTO> testResults) {
        // 计算关键指标
        ISOEnterpriseReportDTO.KeyMetrics metrics = buildKeyMetrics(summary, testResults);
        dto.setKeyMetrics(metrics);
        
        // 确定核心结论
        String conclusion = determineConclusion(summary, testResults, metrics);
        dto.setConclusion(conclusion);
        
        // 生成详细结论说明
        String detailedConclusion = generateDetailedConclusion(summary, testResults, metrics);
        dto.setDetailedConclusion(detailedConclusion);
        
        log.debug("执行摘要构建完成: conclusion={}, passRate={}", conclusion, metrics.getTestPassRate());
    }
    
    /**
     * 构建关键指标
     */
    private ISOEnterpriseReportDTO.KeyMetrics buildKeyMetrics(
            ReportExportResponseDTO.ReportSummaryInfoDTO summary,
            List<ReportExportResponseDTO.TestCaseResultDTO> testResults) {
        
        ISOEnterpriseReportDTO.KeyMetrics metrics = new ISOEnterpriseReportDTO.KeyMetrics();
        
        int executed = summary.getExecutedCases() != null ? summary.getExecutedCases() : 0;
        int passed = summary.getPassedCases() != null ? summary.getPassedCases() : 0;
        int failed = summary.getFailedCases() != null ? summary.getFailedCases() : 0;
        int broken = summary.getBrokenCases() != null ? summary.getBrokenCases() : 0;
        int total = summary.getTotalCases() != null ? summary.getTotalCases() : 0;
        
        // 1. 测试通过率
        BigDecimal passRate = executed > 0 
            ? BigDecimal.valueOf(passed * 100.0 / executed).setScale(1, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        metrics.setTestPassRate(passRate);
        
        // 2. 缺陷密度 = (失败 + 异常) / 已执行 * 100
        BigDecimal defectDensity = executed > 0
            ? BigDecimal.valueOf((failed + broken) * 100.0 / executed).setScale(1, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        metrics.setDefectDensity(defectDensity);
        
        // 3. 高优先级缺陷数 (P0 + P1)
        int criticalDefects = 0;
        if (testResults != null) {
            criticalDefects = (int) testResults.stream()
                .filter(r -> ("failed".equalsIgnoreCase(r.getStatus()) || "broken".equalsIgnoreCase(r.getStatus())))
                .filter(r -> "P0".equalsIgnoreCase(r.getPriority()) || "P1".equalsIgnoreCase(r.getPriority()))
                .count();
        }
        metrics.setCriticalDefectCount(criticalDefects);
        
        // 4. 缺陷修复率 = 通过率 (简化)
        metrics.setDefectFixRate(passRate);
        
        // 5. 需求覆盖率 = 已执行 / 总用例 * 100
        BigDecimal reqCoverage = total > 0
            ? BigDecimal.valueOf(executed * 100.0 / total).setScale(1, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        metrics.setRequirementCoverage(reqCoverage);
        
        // 6. 测试效率 = 已执行 / 测试天数
        long testDays = calculateTestDays(summary.getStartTime(), summary.getEndTime());
        BigDecimal efficiency = testDays > 0
            ? BigDecimal.valueOf(executed * 1.0 / testDays).setScale(1, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        metrics.setTestEfficiency(efficiency);
        
        log.debug("关键指标计算完成: passRate={}, defectDensity={}, criticalDefects={}", 
            passRate, defectDensity, criticalDefects);
        
        return metrics;
    }
    
    /**
     * 确定测试结论
     */
    private String determineConclusion(ReportExportResponseDTO.ReportSummaryInfoDTO summary,
                                      List<ReportExportResponseDTO.TestCaseResultDTO> testResults,
                                      ISOEnterpriseReportDTO.KeyMetrics metrics) {
        
        double passRate = metrics.getTestPassRate().doubleValue();
        int criticalDefects = metrics.getCriticalDefectCount();
        
        // 统计P0和严重程度为Critical的缺陷
        int p0Count = 0;
        int criticalSeverityCount = 0;
        if (testResults != null) {
            p0Count = (int) testResults.stream()
                .filter(r -> ("failed".equalsIgnoreCase(r.getStatus()) || "broken".equalsIgnoreCase(r.getStatus())))
                .filter(r -> "P0".equalsIgnoreCase(r.getPriority()))
                .count();
            
            criticalSeverityCount = (int) testResults.stream()
                .filter(r -> ("failed".equalsIgnoreCase(r.getStatus()) || "broken".equalsIgnoreCase(r.getStatus())))
                .filter(r -> "critical".equalsIgnoreCase(r.getSeverity()))
                .count();
        }
        
        // 判断逻辑
        if (p0Count > 0 || criticalSeverityCount > 0) {
            return "not_pass"; // 不通过 - 不建议发布
        } else if (passRate >= 95.0 && criticalDefects <= 2) {
            return "pass_recommend"; // 通过 - 建议发布
        } else if (passRate >= 85.0 && criticalDefects <= 5) {
            return "pass_with_risk"; // 有风险通过 - 谨慎发布
        } else {
            return "not_pass"; // 不通过 - 不建议发布
        }
    }
    
    /**
     * 生成详细结论说明
     */
    private String generateDetailedConclusion(ReportExportResponseDTO.ReportSummaryInfoDTO summary,
                                              List<ReportExportResponseDTO.TestCaseResultDTO> testResults,
                                              ISOEnterpriseReportDTO.KeyMetrics metrics) {
        
        int total = summary.getTotalCases();
        int executed = summary.getExecutedCases();
        int passed = summary.getPassedCases();
        int failed = summary.getFailedCases();
        int broken = summary.getBrokenCases();
        int skipped = summary.getSkippedCases();
        
        StringBuilder conclusion = new StringBuilder();
        conclusion.append(String.format("本次测试共执行%d个用例", executed));
        
        if (passed > 0) {
            conclusion.append(String.format("，通过%d个(%.1f%%)", passed, metrics.getTestPassRate().doubleValue()));
        }
        if (failed > 0) {
            conclusion.append(String.format("，失败%d个", failed));
        }
        if (broken > 0) {
            conclusion.append(String.format("，异常%d个", broken));
        }
        if (skipped > 0) {
            conclusion.append(String.format("，跳过%d个", skipped));
        }
        conclusion.append("。");
        
        // 添加缺陷说明
        int criticalDefects = metrics.getCriticalDefectCount();
        if (criticalDefects > 0) {
            conclusion.append(String.format("发现%d个高优先级缺陷，建议修复后发布。", criticalDefects));
        } else if (failed + broken > 0) {
            conclusion.append("发现的缺陷优先级较低，建议评估后决定是否发布。");
        } else {
            conclusion.append("所有测试用例均通过，质量良好，建议发布。");
        }
        
        return conclusion.toString();
    }
    
    /**
     * 构建测试范围
     */
    private void buildTestScope(ISOEnterpriseReportDTO dto,
                               ReportExportResponseDTO.ReportSummaryInfoDTO summary,
                               List<ReportExportResponseDTO.TestCaseResultDTO> testResults) {
        
        ISOEnterpriseReportDTO.TestScope scope = new ISOEnterpriseReportDTO.TestScope();
        
        scope.setCoreBusinessProcesses("核心业务功能测试");
        
        // 统计测试类型
        List<String> testTypes = new ArrayList<>();
        testTypes.add("功能测试");
        testTypes.add("接口测试");
        scope.setTestTypes(testTypes);
        
        // 测试方法
        List<String> methods = Arrays.asList("黑盒测试", "自动化测试", "手工测试");
        scope.setTestMethods(methods);
        
        // 模块数量（简化：设为1）
        scope.setModuleCount(1);
        
        // 测试目标
        List<String> objectives = Arrays.asList(
            "验证核心功能正确性",
            "确保系统稳定性",
            "评估发布就绪度"
        );
        scope.setTestObjectives(objectives);
        
        dto.setTestScope(scope);
        
        log.debug("测试范围构建完成");
    }
    
    /**
     * 构建测试环境
     */
    private void buildTestEnvironment(ISOEnterpriseReportDTO dto,
                                     ReportExportResponseDTO.ReportSummaryInfoDTO summary,
                                     List<ReportExportResponseDTO.TestCaseResultDTO> testResults) {
        
        ISOEnterpriseReportDTO.TestEnvironment env = new ISOEnterpriseReportDTO.TestEnvironment();
        
        env.setEnvironmentName(summary.getEnvironment() != null ? summary.getEnvironment() : "测试环境");
        env.setEnvironmentType("dev");
        env.setServerAddress("内网服务器");
        env.setDatabaseInfo("MySQL 8.0");
        env.setBackendVersion("Spring Boot 3.5.5");
        
        env.setTestTools(Arrays.asList("Postman", "JMeter", "IATMS"));
        
        // 统计浏览器/设备覆盖
        Set<String> browsers = new HashSet<>();
        if (testResults != null) {
            testResults.stream()
                .map(ReportExportResponseDTO.TestCaseResultDTO::getBrowser)
                .filter(Objects::nonNull)
                .forEach(browsers::add);
        }
        if (browsers.isEmpty()) {
            browsers.add("Chrome");
        }
        env.setBrowserDeviceCoverage(new ArrayList<>(browsers));
        
        dto.setTestEnvironment(env);
        
        log.debug("测试环境构建完成");
    }
    
    /**
     * 构建测试结果与度量
     */
    private void buildTestResultsAndMetrics(ISOEnterpriseReportDTO dto,
                                           ReportExportResponseDTO.ReportSummaryInfoDTO summary,
                                           List<ReportExportResponseDTO.TestCaseResultDTO> testResults) {
        
        // 模块结果（简化：只显示全部测试用例）
        List<ISOEnterpriseReportDTO.ModuleResult> moduleResults = new ArrayList<>();
        ISOEnterpriseReportDTO.ModuleResult allModule = new ISOEnterpriseReportDTO.ModuleResult();
        allModule.setModuleName("全部测试用例");
        allModule.setTotalCases(summary.getTotalCases());
        allModule.setExecutedCases(summary.getExecutedCases());
        allModule.setPassedCases(summary.getPassedCases());
        allModule.setFailedCases(summary.getFailedCases());
        allModule.setBrokenCases(summary.getBrokenCases());
        allModule.setSkippedCases(summary.getSkippedCases());
        allModule.setPassRate(summary.getSuccessRate());
        moduleResults.add(allModule);
        dto.setModuleResults(moduleResults);
        
        // 覆盖率矩阵 (优先级 vs 状态)
        Map<String, Map<String, Integer>> matrix = buildCoverageMatrix(testResults);
        dto.setCoverageMatrix(matrix);
        
        // 缺陷度量
        ISOEnterpriseReportDTO.DefectMetrics defectMetrics = buildDefectMetrics(testResults);
        dto.setDefectMetrics(defectMetrics);
        
        // 缺陷趋势（简化版）
        List<ISOEnterpriseReportDTO.DefectTrend> trends = buildDefectTrends(testResults, summary);
        dto.setDefectTrends(trends);
        
        // 执行趋势（简化版）
        List<ISOEnterpriseReportDTO.ExecutionTrend> execTrends = buildExecutionTrends(testResults, summary);
        dto.setExecutionTrends(execTrends);
        
        log.debug("测试结果与度量构建完成");
    }
    
    /**
     * 构建覆盖率矩阵
     */
    private Map<String, Map<String, Integer>> buildCoverageMatrix(
            List<ReportExportResponseDTO.TestCaseResultDTO> testResults) {
        
        Map<String, Map<String, Integer>> matrix = new LinkedHashMap<>();
        
        String[] priorities = {"P0", "P1", "P2", "P3"};
        String[] statuses = {"passed", "failed", "broken", "skipped"};
        
        for (String priority : priorities) {
            Map<String, Integer> statusMap = new LinkedHashMap<>();
            for (String status : statuses) {
                int count = 0;
                if (testResults != null) {
                    count = (int) testResults.stream()
                        .filter(r -> priority.equalsIgnoreCase(r.getPriority()))
                        .filter(r -> status.equalsIgnoreCase(r.getStatus()))
                        .count();
                }
                statusMap.put(status, count);
            }
            matrix.put(priority, statusMap);
        }
        
        return matrix;
    }
    
    /**
     * 构建缺陷度量
     */
    private ISOEnterpriseReportDTO.DefectMetrics buildDefectMetrics(
            List<ReportExportResponseDTO.TestCaseResultDTO> testResults) {
        
        ISOEnterpriseReportDTO.DefectMetrics metrics = new ISOEnterpriseReportDTO.DefectMetrics();
        
        if (testResults == null || testResults.isEmpty()) {
            metrics.setTotalDefects(0);
            metrics.setP0Count(0);
            metrics.setP1Count(0);
            metrics.setP2Count(0);
            metrics.setP3Count(0);
            metrics.setDefectDiscoveryRate(BigDecimal.ZERO);
            metrics.setDefectRemainRate(BigDecimal.ZERO);
            return metrics;
        }
        
        // 筛选失败和异常的用例
        List<ReportExportResponseDTO.TestCaseResultDTO> defects = testResults.stream()
            .filter(r -> "failed".equalsIgnoreCase(r.getStatus()) || "broken".equalsIgnoreCase(r.getStatus()))
            .collect(Collectors.toList());
        
        metrics.setTotalDefects(defects.size());
        
        // 按优先级统计
        metrics.setP0Count((int) defects.stream().filter(d -> "P0".equalsIgnoreCase(d.getPriority())).count());
        metrics.setP1Count((int) defects.stream().filter(d -> "P1".equalsIgnoreCase(d.getPriority())).count());
        metrics.setP2Count((int) defects.stream().filter(d -> "P2".equalsIgnoreCase(d.getPriority())).count());
        metrics.setP3Count((int) defects.stream().filter(d -> "P3".equalsIgnoreCase(d.getPriority())).count());
        
        // 缺陷发现率 = 缺陷数 / 总用例 * 100
        BigDecimal discoveryRate = BigDecimal.valueOf(defects.size() * 100.0 / testResults.size())
            .setScale(1, RoundingMode.HALF_UP);
        metrics.setDefectDiscoveryRate(discoveryRate);
        
        // 缺陷遗留率 = 缺陷数 / 总用例 * 100 (简化，假设都是未修复的)
        metrics.setDefectRemainRate(discoveryRate);
        
        return metrics;
    }
    
    /**
     * 构建缺陷趋势
     */
    private List<ISOEnterpriseReportDTO.DefectTrend> buildDefectTrends(
            List<ReportExportResponseDTO.TestCaseResultDTO> testResults,
            ReportExportResponseDTO.ReportSummaryInfoDTO summary) {
        
        List<ISOEnterpriseReportDTO.DefectTrend> trends = new ArrayList<>();
        
        if (testResults == null || testResults.isEmpty()) {
            return trends;
        }
        
        // 按日期分组统计失败用例
        Map<String, List<ReportExportResponseDTO.TestCaseResultDTO>> defectsByDate = testResults.stream()
            .filter(r -> "failed".equalsIgnoreCase(r.getStatus()) || "broken".equalsIgnoreCase(r.getStatus()))
            .filter(r -> r.getStartTime() != null)
            .collect(Collectors.groupingBy(r -> 
                r.getStartTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        
        // 生成趋势数据
        int cumulative = 0;
        for (Map.Entry<String, List<ReportExportResponseDTO.TestCaseResultDTO>> entry : defectsByDate.entrySet()) {
            ISOEnterpriseReportDTO.DefectTrend trend = new ISOEnterpriseReportDTO.DefectTrend();
            trend.setDate(entry.getKey());
            trend.setNewDefects(entry.getValue().size());
            trend.setClosedDefects(0); // 简化
            cumulative += entry.getValue().size();
            trend.setUnresolvedDefects(cumulative);
            trends.add(trend);
        }
        
        // 按日期排序
        trends.sort(Comparator.comparing(ISOEnterpriseReportDTO.DefectTrend::getDate));
        
        return trends;
    }
    
    /**
     * 构建执行趋势
     */
    private List<ISOEnterpriseReportDTO.ExecutionTrend> buildExecutionTrends(
            List<ReportExportResponseDTO.TestCaseResultDTO> testResults,
            ReportExportResponseDTO.ReportSummaryInfoDTO summary) {
        
        List<ISOEnterpriseReportDTO.ExecutionTrend> trends = new ArrayList<>();
        
        if (testResults == null || testResults.isEmpty()) {
            return trends;
        }
        
        // 按日期分组统计
        Map<String, Long> executionByDate = testResults.stream()
            .filter(r -> r.getStartTime() != null)
            .collect(Collectors.groupingBy(
                r -> r.getStartTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                Collectors.counting()));
        
        // 生成趋势数据
        int cumulative = 0;
        for (Map.Entry<String, Long> entry : executionByDate.entrySet()) {
            ISOEnterpriseReportDTO.ExecutionTrend trend = new ISOEnterpriseReportDTO.ExecutionTrend();
            trend.setDate(entry.getKey());
            trend.setDailyExecuted(entry.getValue().intValue());
            cumulative += entry.getValue().intValue();
            trend.setCumulativeExecuted(cumulative);
            trends.add(trend);
        }
        
        // 按日期排序
        trends.sort(Comparator.comparing(ISOEnterpriseReportDTO.ExecutionTrend::getDate));
        
        return trends;
    }
    
    /**
     * 构建详细缺陷信息
     */
    private void buildDetailedDefects(ISOEnterpriseReportDTO dto,
                                     List<ReportExportResponseDTO.TestCaseResultDTO> testResults) {
        
        List<ISOEnterpriseReportDTO.DetailedDefect> defects = new ArrayList<>();
        
        if (testResults == null) {
            dto.setDetailedDefects(defects);
            return;
        }
        
        // 筛选失败和异常的用例
        List<ReportExportResponseDTO.TestCaseResultDTO> failedCases = testResults.stream()
            .filter(r -> "failed".equalsIgnoreCase(r.getStatus()) || "broken".equalsIgnoreCase(r.getStatus()))
            .collect(Collectors.toList());
        
        int index = 1;
        for (ReportExportResponseDTO.TestCaseResultDTO failedCase : failedCases) {
            ISOEnterpriseReportDTO.DetailedDefect defect = new ISOEnterpriseReportDTO.DetailedDefect();
            
            defect.setDefectIndex(index++);
            defect.setCaseId(String.valueOf(failedCase.getCaseId()));
            defect.setCaseCode(failedCase.getCaseCode());
            defect.setCaseName(failedCase.getCaseName());
            defect.setPriority(failedCase.getPriority());
            defect.setSeverity(failedCase.getSeverity());
            defect.setStatus(failedCase.getStatus());
            defect.setDiscoveryTime(failedCase.getStartTime());
            defect.setTesterName("测试人员"); // TODO: 从用户信息获取
            defect.setImpactScope(determineImpactScope(failedCase));
            
            // 错误信息
            defect.setErrorType(failedCase.getFailureType());
            defect.setErrorMessage(failedCase.getFailureMessage());
            defect.setStackTrace(failedCase.getFailureTrace());
            
            // 测试场景信息（简化）
            defect.setPreconditions("请参考用例设计文档");
            defect.setReproductionSteps("请参考用例执行步骤");
            defect.setExpectedResult("请参考用例预期结果");
            defect.setActualResult(failedCase.getFailureMessage() != null ? failedCase.getFailureMessage() : "测试失败");
            
            // 分析建议（基于优先级）
            defect.setRootCauseAnalysis(generateRootCauseAnalysis(failedCase));
            defect.setSuggestedActions(generateSuggestedActions(failedCase));
            
            // 环境信息
            defect.setEnvironment(failedCase.getEnvironment());
            defect.setBrowser(failedCase.getBrowser());
            defect.setOs(failedCase.getOs());
            defect.setDevice(failedCase.getDevice());
            defect.setTags(failedCase.getTags());
            defect.setDuration(failedCase.getDuration() != null ? failedCase.getDuration().longValue() : 0L);
            defect.setRetryCount(failedCase.getRetryCount());
            defect.setIsFlaky(failedCase.getFlaky());
            
            defects.add(defect);
        }
        
        dto.setDetailedDefects(defects);
        
        log.debug("详细缺陷信息构建完成: 共{}个缺陷", defects.size());
    }
    
    /**
     * 确定影响范围
     */
    private String determineImpactScope(ReportExportResponseDTO.TestCaseResultDTO testCase) {
        if ("P0".equalsIgnoreCase(testCase.getPriority())) {
            return "核心功能模块，影响系统正常使用";
        } else if ("P1".equalsIgnoreCase(testCase.getPriority())) {
            return "重要功能模块，影响用户体验";
        } else if ("P2".equalsIgnoreCase(testCase.getPriority())) {
            return "一般功能模块，影响较小";
        } else {
            return "次要功能模块，影响可忽略";
        }
    }
    
    /**
     * 生成根因分析
     */
    private String generateRootCauseAnalysis(ReportExportResponseDTO.TestCaseResultDTO testCase) {
        if (testCase.getFailureType() != null) {
            if (testCase.getFailureType().contains("Timeout")) {
                return "请求超时，可能是网络延迟或服务器响应慢导致";
            } else if (testCase.getFailureType().contains("Assertion")) {
                return "断言失败，实际结果与预期不符，需检查业务逻辑";
            } else if (testCase.getFailureType().contains("Connection")) {
                return "连接失败，可能是网络问题或服务不可用";
            }
        }
        return "需进一步分析日志和代码以确定根本原因";
    }
    
    /**
     * 生成建议措施
     */
    private String generateSuggestedActions(ReportExportResponseDTO.TestCaseResultDTO testCase) {
        StringBuilder actions = new StringBuilder();
        
        if ("P0".equalsIgnoreCase(testCase.getPriority())) {
            actions.append("1. 立即修复该缺陷\n");
            actions.append("2. 回归测试验证修复效果\n");
            actions.append("3. 修复后方可发布");
        } else if ("P1".equalsIgnoreCase(testCase.getPriority())) {
            actions.append("1. 优先修复该缺陷\n");
            actions.append("2. 评估是否影响发布\n");
            actions.append("3. 建议修复后发布");
        } else {
            actions.append("1. 记录缺陷，排期修复\n");
            actions.append("2. 可在后续版本修复\n");
            actions.append("3. 不影响当前发布");
        }
        
        return actions.toString();
    }
    
    /**
     * 构建挑战与风险
     */
    private void buildChallengesAndRisks(ISOEnterpriseReportDTO dto,
                                        ReportExportResponseDTO.ReportSummaryInfoDTO summary,
                                        List<ReportExportResponseDTO.TestCaseResultDTO> testResults) {
        
        // 挑战
        List<ISOEnterpriseReportDTO.Challenge> challenges = new ArrayList<>();
        if (summary.getFailedCases() + summary.getBrokenCases() > 0) {
            ISOEnterpriseReportDTO.Challenge ch1 = new ISOEnterpriseReportDTO.Challenge();
            ch1.setTitle("测试环境不稳定");
            ch1.setDescription("部分测试用例执行失败，可能与测试环境有关");
            ch1.setMitigation("优化测试环境配置，增加监控和重试机制");
            challenges.add(ch1);
        }
        dto.setChallenges(challenges);
        
        // 风险矩阵
        List<ISOEnterpriseReportDTO.RiskItem> risks = buildRiskMatrix(summary, testResults);
        dto.setRiskMatrix(risks);
        
        // 测试覆盖不足区域
        List<ISOEnterpriseReportDTO.UncoveredArea> uncovered = new ArrayList<>();
        ISOEnterpriseReportDTO.UncoveredArea area1 = new ISOEnterpriseReportDTO.UncoveredArea();
        area1.setAreaName("性能测试");
        area1.setCoveragePercent(30);
        area1.setRecommendation("建议增加专项性能测试");
        uncovered.add(area1);
        dto.setUncoveredAreas(uncovered);
        
        log.debug("挑战与风险构建完成");
    }
    
    /**
     * 构建风险矩阵
     */
    private List<ISOEnterpriseReportDTO.RiskItem> buildRiskMatrix(
            ReportExportResponseDTO.ReportSummaryInfoDTO summary,
            List<ReportExportResponseDTO.TestCaseResultDTO> testResults) {
        
        List<ISOEnterpriseReportDTO.RiskItem> risks = new ArrayList<>();
        
        // 风险1: 基于失败用例数量评估
        if (summary.getFailedCases() > 0) {
            ISOEnterpriseReportDTO.RiskItem risk1 = new ISOEnterpriseReportDTO.RiskItem();
            risk1.setRiskName("功能缺陷风险");
            risk1.setProbability("medium");
            risk1.setProbabilityPercent(40);
            risk1.setImpact("high");
            risk1.setRiskLevel("medium_high");
            risk1.setRiskIcon("🟠");
            risk1.setMitigation("修复所有P0和P1缺陷，回归测试验证");
            risks.add(risk1);
        }
        
        // 风险2: 测试覆盖率风险
        double coverage = summary.getTotalCases() > 0 
            ? summary.getExecutedCases() * 100.0 / summary.getTotalCases() 
            : 0;
        if (coverage < 100) {
            ISOEnterpriseReportDTO.RiskItem risk2 = new ISOEnterpriseReportDTO.RiskItem();
            risk2.setRiskName("测试覆盖不足");
            risk2.setProbability("low");
            risk2.setProbabilityPercent(20);
            risk2.setImpact("medium");
            risk2.setRiskLevel("medium");
            risk2.setRiskIcon("🟡");
            risk2.setMitigation("后续补充未执行的测试用例");
            risks.add(risk2);
        }
        
        return risks;
    }
    
    /**
     * 构建结论与建议
     */
    private void buildConclusionAndRecommendations(ISOEnterpriseReportDTO dto,
                                                  ReportExportResponseDTO.ReportSummaryInfoDTO summary,
                                                  List<ReportExportResponseDTO.TestCaseResultDTO> testResults) {
        
        // 总体结论
        ISOEnterpriseReportDTO.OverallConclusion conclusion = buildOverallConclusion(dto, summary, testResults);
        dto.setOverallConclusion(conclusion);
        
        // 发布检查清单
        ISOEnterpriseReportDTO.ReleaseChecklist checklist = buildReleaseChecklist(testResults);
        dto.setReleaseChecklist(checklist);
        
        // 改进计划
        ISOEnterpriseReportDTO.ImprovementPlan plan = buildImprovementPlan(summary);
        dto.setImprovementPlan(plan);
        
        log.debug("结论与建议构建完成");
    }
    
    /**
     * 构建总体结论
     */
    private ISOEnterpriseReportDTO.OverallConclusion buildOverallConclusion(
            ISOEnterpriseReportDTO dto,
            ReportExportResponseDTO.ReportSummaryInfoDTO summary,
            List<ReportExportResponseDTO.TestCaseResultDTO> testResults) {
        
        ISOEnterpriseReportDTO.OverallConclusion conclusion = new ISOEnterpriseReportDTO.OverallConclusion();
        
        String testConclusion = dto.getConclusion();
        
        // 设置结论文本
        if ("pass_recommend".equals(testConclusion)) {
            conclusion.setTestConclusion("✅ 测试通过 - 建议发布");
            conclusion.setQualityAssessment("🟢 良好");
            conclusion.setReleaseRecommendation("✅ 可以发布");
            conclusion.setRiskLevel("🟡 低风险");
        } else if ("pass_with_risk".equals(testConclusion)) {
            conclusion.setTestConclusion("⚠️ 有风险通过 - 谨慎发布");
            conclusion.setQualityAssessment("🟡 一般");
            conclusion.setReleaseRecommendation("⚠️ 谨慎发布");
            conclusion.setRiskLevel("🟠 中风险");
        } else {
            conclusion.setTestConclusion("❌ 测试不通过 - 不建议发布");
            conclusion.setQualityAssessment("🔴 需改进");
            conclusion.setReleaseRecommendation("❌ 不建议发布");
            conclusion.setRiskLevel("🔴 高风险");
        }
        
        // 综合评价
        conclusion.setComprehensiveEvaluation(dto.getDetailedConclusion());
        
        return conclusion;
    }
    
    /**
     * 构建发布检查清单
     */
    private ISOEnterpriseReportDTO.ReleaseChecklist buildReleaseChecklist(
            List<ReportExportResponseDTO.TestCaseResultDTO> testResults) {
        
        ISOEnterpriseReportDTO.ReleaseChecklist checklist = new ISOEnterpriseReportDTO.ReleaseChecklist();
        
        List<ISOEnterpriseReportDTO.DefectItem> mustFix = new ArrayList<>();
        List<ISOEnterpriseReportDTO.DefectItem> shouldFix = new ArrayList<>();
        List<ISOEnterpriseReportDTO.DefectItem> canDefer = new ArrayList<>();
        
        if (testResults != null) {
            List<ReportExportResponseDTO.TestCaseResultDTO> defects = testResults.stream()
                .filter(r -> "failed".equalsIgnoreCase(r.getStatus()) || "broken".equalsIgnoreCase(r.getStatus()))
                .collect(Collectors.toList());
            
            for (ReportExportResponseDTO.TestCaseResultDTO defect : defects) {
                ISOEnterpriseReportDTO.DefectItem item = new ISOEnterpriseReportDTO.DefectItem();
                item.setPriority(defect.getPriority());
                item.setDescription(defect.getCaseName());
                item.setImpact(determineImpactScope(defect));
                
                if ("P0".equalsIgnoreCase(defect.getPriority()) || "P1".equalsIgnoreCase(defect.getPriority())) {
                    mustFix.add(item);
                } else if ("P2".equalsIgnoreCase(defect.getPriority())) {
                    shouldFix.add(item);
                } else {
                    canDefer.add(item);
                }
            }
        }
        
        checklist.setMustFix(mustFix);
        checklist.setShouldFix(shouldFix);
        checklist.setCanDefer(canDefer);
        
        // 建议发布时间
        if (mustFix.isEmpty()) {
            checklist.setSuggestedReleaseDate("可立即发布");
        } else {
            LocalDateTime suggested = LocalDateTime.now().plusDays(3);
            checklist.setSuggestedReleaseDate(suggested.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " (修复P0/P1后)");
        }
        
        return checklist;
    }
    
    /**
     * 构建改进计划
     */
    private ISOEnterpriseReportDTO.ImprovementPlan buildImprovementPlan(
            ReportExportResponseDTO.ReportSummaryInfoDTO summary) {
        
        ISOEnterpriseReportDTO.ImprovementPlan plan = new ISOEnterpriseReportDTO.ImprovementPlan();
        
        List<String> shortTerm = Arrays.asList(
            "修复所有P0和P1缺陷",
            "补充未执行的测试用例",
            "优化测试环境配置"
        );
        plan.setShortTerm(shortTerm);
        
        List<String> mediumTerm = Arrays.asList(
            "建立专项性能测试体系",
            "扩展自动化测试覆盖率",
            "引入安全测试工具"
        );
        plan.setMediumTerm(mediumTerm);
        
        List<String> longTerm = Arrays.asList(
            "建立完整的测试度量体系",
            "实施持续集成/持续测试",
            "培训团队测试技能"
        );
        plan.setLongTerm(longTerm);
        
        return plan;
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 生成版本号
     */
    private String generateVersion(LocalDateTime startTime) {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        return "V" + startTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
    
    /**
     * 生成报告编号
     */
    private String generateReportNumber(Long reportId, LocalDateTime startTime) {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        String dateStr = startTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("TR-%s-%04d", dateStr, reportId);
    }
    
    /**
     * 格式化报告类型
     */
    private String formatReportType(String reportType) {
        if (reportType == null) return "系统";
        switch (reportType.toLowerCase()) {
            case "api": return "接口";
            case "function": return "功能";
            case "performance": return "性能";
            case "security": return "安全";
            default: return "系统";
        }
    }
    
    /**
     * 计算测试天数
     */
    private long calculateTestDays(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 1;
        }
        long days = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate()) + 1;
        return Math.max(days, 1);
    }
    
    /**
     * 生成文件名
     */
    private String generateFileName(ISOEnterpriseReportDTO reportData) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("ISO企业级测试报告_%s_%s.html", reportData.getReportNumber(), timestamp);
    }
}

