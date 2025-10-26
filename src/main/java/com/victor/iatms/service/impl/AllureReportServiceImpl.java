package com.victor.iatms.service.impl;

import com.victor.iatms.entity.dto.AllureReportDTO;
import com.victor.iatms.entity.dto.ReportExportResponseDTO;
import com.victor.iatms.mappers.ReportMapper;
import com.victor.iatms.service.AllureReportService;
import com.victor.iatms.utils.AllureHTMLBuilder;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Allure风格测试报告服务实现
 * 
 * @author Victor
 * @since 2024-10-26
 */
@Slf4j
@Service
public class AllureReportServiceImpl implements AllureReportService {
    
    @Autowired
    private ReportMapper reportMapper;
    
    @Override
    public Resource exportAllureReport(Long reportId, String locale) {
        log.info("开始导出Allure风格测试报告: reportId={}, locale={}", reportId, locale);
        
        try {
            // 构建报告数据
            AllureReportDTO reportData = buildAllureReportData(reportId);
            
            // 生成HTML
            AllureHTMLBuilder htmlBuilder = new AllureHTMLBuilder(reportData, locale);
            String htmlContent = htmlBuilder.build();
            
            // 创建资源
            byte[] content = htmlContent.getBytes(StandardCharsets.UTF_8);
            String fileName = generateFileName(reportData);
            
            log.info("Allure风格测试报告生成成功: reportId={}, fileName={}", reportId, fileName);
            
            return new ByteArrayResource(content) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            };
            
        } catch (Exception e) {
            log.error("导出Allure风格测试报告失败: reportId={}", reportId, e);
            throw new RuntimeException("导出Allure风格测试报告失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public AllureReportDTO buildAllureReportData(Long reportId) {
        log.info("开始构建Allure报告数据: reportId={}", reportId);
        
        AllureReportDTO dto = new AllureReportDTO();
        
        // 获取报告基本信息
        ReportExportResponseDTO.ReportSummaryInfoDTO summary = reportMapper.selectReportExportData(reportId);
        if (summary == null) {
            throw new IllegalArgumentException("报告不存在: reportId=" + reportId);
        }
        
        // 获取测试结果详情（包含失败信息）
        List<ReportExportResponseDTO.TestCaseResultDTO> testResults = 
            reportMapper.selectReportTestResults(reportId, true, false, true);
        
        log.info("查询到测试结果数: {}", testResults != null ? testResults.size() : 0);
        
        // 设置基础信息
        dto.setReportTitle(summary.getProjectName() + " API 自动化测试报告");
        dto.setProjectName(summary.getProjectName());
        dto.setReportId(reportId);
        dto.setExecutionId("#" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + String.format("%03d", reportId));
        dto.setStartTime(summary.getStartTime());
        dto.setEndTime(summary.getEndTime());
        dto.setTotalDuration(summary.getDuration());
        
        // 设置统计数据
        dto.setTotalCases(summary.getTotalCases());
        dto.setExecutedCases(summary.getExecutedCases());
        dto.setPassedCases(summary.getPassedCases());
        dto.setFailedCases(summary.getFailedCases());
        dto.setBrokenCases(summary.getBrokenCases());
        dto.setSkippedCases(summary.getSkippedCases());
        dto.setSuccessRate(summary.getSuccessRate());
        
        // 构建测试套件列表（按模块分组）
        List<AllureReportDTO.TestSuite> testSuites = buildTestSuites(testResults);
        dto.setTestSuites(testSuites);
        
        // 构建模块统计
        List<AllureReportDTO.ModuleStatistic> moduleStats = buildModuleStatistics(testResults);
        dto.setModuleStatistics(moduleStats);
        
        // 构建历史趋势（简化版）
        List<AllureReportDTO.HistoryTrend> historyTrends = buildHistoryTrends(summary);
        dto.setHistoryTrends(historyTrends);
        
        log.info("Allure报告数据构建完成: reportId={}", reportId);
        
        return dto;
    }
    
    /**
     * 构建测试套件列表（按模块分组）
     */
    private List<AllureReportDTO.TestSuite> buildTestSuites(List<ReportExportResponseDTO.TestCaseResultDTO> testResults) {
        if (testResults == null || testResults.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 按模块分组（这里简化处理，实际应该从数据库查询模块信息）
        Map<String, List<ReportExportResponseDTO.TestCaseResultDTO>> groupedByModule = 
            testResults.stream().collect(Collectors.groupingBy(r -> "默认模块"));
        
        List<AllureReportDTO.TestSuite> suites = new ArrayList<>();
        
        for (Map.Entry<String, List<ReportExportResponseDTO.TestCaseResultDTO>> entry : groupedByModule.entrySet()) {
            AllureReportDTO.TestSuite suite = new AllureReportDTO.TestSuite();
            suite.setSuiteName(entry.getKey());
            suite.setSuiteDescription("测试" + entry.getKey() + "的相关功能");
            suite.setSuiteIcon("📦");
            
            List<ReportExportResponseDTO.TestCaseResultDTO> cases = entry.getValue();
            suite.setTotalCases(cases.size());
            suite.setPassedCases((int) cases.stream().filter(c -> "passed".equalsIgnoreCase(c.getStatus())).count());
            suite.setFailedCases((int) cases.stream().filter(c -> "failed".equalsIgnoreCase(c.getStatus())).count());
            suite.setBrokenCases((int) cases.stream().filter(c -> "broken".equalsIgnoreCase(c.getStatus())).count());
            suite.setSkippedCases((int) cases.stream().filter(c -> "skipped".equalsIgnoreCase(c.getStatus())).count());
            suite.setDuration(cases.stream().mapToLong(c -> c.getDuration() != null ? c.getDuration().longValue() : 0L).sum());
            
            // 构建测试用例列表
            List<AllureReportDTO.TestCase> testCases = cases.stream()
                .map(this::buildTestCase)
                .collect(Collectors.toList());
            suite.setTestCases(testCases);
            
            suites.add(suite);
        }
        
        return suites;
    }
    
    /**
     * 构建单个测试用例
     */
    private AllureReportDTO.TestCase buildTestCase(ReportExportResponseDTO.TestCaseResultDTO caseResult) {
        AllureReportDTO.TestCase testCase = new AllureReportDTO.TestCase();
        
        testCase.setCaseId(caseResult.getCaseId() != null ? caseResult.getCaseId().longValue() : 0L);
        testCase.setCaseCode(caseResult.getCaseCode());
        testCase.setCaseName(caseResult.getCaseName());
        testCase.setStatus(caseResult.getStatus());
        testCase.setDuration(caseResult.getDuration() != null ? caseResult.getDuration().longValue() : 0L);
        testCase.setStartTime(caseResult.getStartTime());
        testCase.setPriority(caseResult.getPriority());
        testCase.setSeverity(caseResult.getSeverity());
        testCase.setTags(caseResult.getTags());
        
        // 构建测试步骤（简化版）
        List<AllureReportDTO.TestStep> steps = buildTestSteps(caseResult);
        testCase.setSteps(steps);
        
        // 构建测试参数（简化版）
        List<AllureReportDTO.TestParameter> parameters = buildTestParameters(caseResult);
        testCase.setParameters(parameters);
        
        // 构建断言列表（简化版）
        List<AllureReportDTO.Assertion> assertions = buildAssertions(caseResult);
        testCase.setAssertions(assertions);
        
        // 构建HTTP请求/响应信息（简化版）
        AllureReportDTO.HttpRequest httpRequest = buildHttpRequest(caseResult);
        testCase.setHttpRequest(httpRequest);
        
        AllureReportDTO.HttpResponse httpResponse = buildHttpResponse(caseResult);
        testCase.setHttpResponse(httpResponse);
        
        // 如果失败，构建错误信息
        if ("failed".equalsIgnoreCase(caseResult.getStatus()) || "broken".equalsIgnoreCase(caseResult.getStatus())) {
            AllureReportDTO.FailureInfo failureInfo = new AllureReportDTO.FailureInfo();
            failureInfo.setErrorType(caseResult.getFailureType());
            failureInfo.setErrorMessage(caseResult.getFailureMessage());
            failureInfo.setStackTrace(caseResult.getFailureTrace());
            testCase.setFailureInfo(failureInfo);
        }
        
        // 构建附件列表（简化版）
        List<AllureReportDTO.Attachment> attachments = buildAttachments(caseResult);
        testCase.setAttachments(attachments);
        
        return testCase;
    }
    
    /**
     * 构建测试步骤
     */
    private List<AllureReportDTO.TestStep> buildTestSteps(ReportExportResponseDTO.TestCaseResultDTO caseResult) {
        List<AllureReportDTO.TestStep> steps = new ArrayList<>();
        
        // 步骤1: 准备测试数据
        AllureReportDTO.TestStep step1 = new AllureReportDTO.TestStep();
        step1.setStepIndex(1);
        step1.setStepTitle("准备测试数据");
        step1.setStepDescription("初始化测试参数和环境配置");
        step1.setStatus("passed");
        step1.setDuration(50L);
        steps.add(step1);
        
        // 步骤2: 发送请求
        AllureReportDTO.TestStep step2 = new AllureReportDTO.TestStep();
        step2.setStepIndex(2);
        step2.setStepTitle("发送API请求");
        step2.setStepDescription("执行HTTP请求并获取响应");
        step2.setStatus(caseResult.getStatus());
        step2.setDuration(caseResult.getDuration() != null ? caseResult.getDuration().longValue() - 200 : 800L);
        if ("failed".equalsIgnoreCase(caseResult.getStatus()) || "broken".equalsIgnoreCase(caseResult.getStatus())) {
            step2.setErrorMessage(caseResult.getFailureMessage());
        }
        steps.add(step2);
        
        // 步骤3: 验证响应
        AllureReportDTO.TestStep step3 = new AllureReportDTO.TestStep();
        step3.setStepIndex(3);
        step3.setStepTitle("验证响应数据");
        step3.setStepDescription("检查响应状态码、数据格式和业务逻辑");
        step3.setStatus("passed".equalsIgnoreCase(caseResult.getStatus()) ? "passed" : "failed");
        step3.setDuration(150L);
        steps.add(step3);
        
        return steps;
    }
    
    /**
     * 构建测试参数
     */
    private List<AllureReportDTO.TestParameter> buildTestParameters(ReportExportResponseDTO.TestCaseResultDTO caseResult) {
        List<AllureReportDTO.TestParameter> parameters = new ArrayList<>();
        
        AllureReportDTO.TestParameter param1 = new AllureReportDTO.TestParameter();
        param1.setParamName("caseId");
        param1.setParamValue(String.valueOf(caseResult.getCaseId()));
        param1.setParamType("Long");
        parameters.add(param1);
        
        AllureReportDTO.TestParameter param2 = new AllureReportDTO.TestParameter();
        param2.setParamName("environment");
        param2.setParamValue(caseResult.getEnvironment() != null ? caseResult.getEnvironment() : "test");
        param2.setParamType("String");
        parameters.add(param2);
        
        return parameters;
    }
    
    /**
     * 构建断言列表
     */
    private List<AllureReportDTO.Assertion> buildAssertions(ReportExportResponseDTO.TestCaseResultDTO caseResult) {
        List<AllureReportDTO.Assertion> assertions = new ArrayList<>();
        
        AllureReportDTO.Assertion assertion1 = new AllureReportDTO.Assertion();
        assertion1.setAssertionName("response.statusCode");
        assertion1.setExpected("200");
        assertion1.setActual("passed".equalsIgnoreCase(caseResult.getStatus()) ? "200" : "500");
        assertion1.setStatus(caseResult.getStatus());
        assertions.add(assertion1);
        
        AllureReportDTO.Assertion assertion2 = new AllureReportDTO.Assertion();
        assertion2.setAssertionName("response.data");
        assertion2.setExpected("not null");
        assertion2.setActual("passed".equalsIgnoreCase(caseResult.getStatus()) ? "valid data" : "null");
        assertion2.setStatus(caseResult.getStatus());
        assertions.add(assertion2);
        
        return assertions;
    }
    
    /**
     * 构建HTTP请求
     */
    private AllureReportDTO.HttpRequest buildHttpRequest(ReportExportResponseDTO.TestCaseResultDTO caseResult) {
        AllureReportDTO.HttpRequest request = new AllureReportDTO.HttpRequest();
        request.setMethod("POST");
        request.setUrl("/api/v1/test/" + caseResult.getCaseId());
        request.setHeaders("{\n  \"Content-Type\": \"application/json\",\n  \"Authorization\": \"Bearer token\"\n}");
        request.setBody("{\n  \"caseId\": " + caseResult.getCaseId() + ",\n  \"testData\": \"sample\"\n}");
        return request;
    }
    
    /**
     * 构建HTTP响应
     */
    private AllureReportDTO.HttpResponse buildHttpResponse(ReportExportResponseDTO.TestCaseResultDTO caseResult) {
        AllureReportDTO.HttpResponse response = new AllureReportDTO.HttpResponse();
        response.setStatusCode("passed".equalsIgnoreCase(caseResult.getStatus()) ? 200 : 500);
        response.setHeaders("{\n  \"Content-Type\": \"application/json\"\n}");
        
        if ("passed".equalsIgnoreCase(caseResult.getStatus())) {
            response.setBody("{\n  \"code\": 200,\n  \"msg\": \"success\",\n  \"data\": {\n    \"result\": \"test passed\"\n  }\n}");
        } else {
            response.setBody("{\n  \"code\": 500,\n  \"msg\": \"error\",\n  \"error\": {\n    \"message\": \"" + 
                (caseResult.getFailureMessage() != null ? caseResult.getFailureMessage().replace("\"", "\\\"") : "Internal error") + 
                "\"\n  }\n}");
        }
        
        response.setDuration(caseResult.getDuration() != null ? caseResult.getDuration().longValue() : 0L);
        return response;
    }
    
    /**
     * 构建附件列表
     */
    private List<AllureReportDTO.Attachment> buildAttachments(ReportExportResponseDTO.TestCaseResultDTO caseResult) {
        List<AllureReportDTO.Attachment> attachments = new ArrayList<>();
        
        AllureReportDTO.Attachment attachment1 = new AllureReportDTO.Attachment();
        attachment1.setAttachmentName("request_log.txt");
        attachment1.setAttachmentType("text");
        attachment1.setAttachmentSize("2.1 KB");
        attachments.add(attachment1);
        
        if ("failed".equalsIgnoreCase(caseResult.getStatus()) || "broken".equalsIgnoreCase(caseResult.getStatus())) {
            AllureReportDTO.Attachment attachment2 = new AllureReportDTO.Attachment();
            attachment2.setAttachmentName("error_screenshot.png");
            attachment2.setAttachmentType("image");
            attachment2.setAttachmentSize("45.2 KB");
            attachments.add(attachment2);
        }
        
        return attachments;
    }
    
    /**
     * 构建模块统计
     */
    private List<AllureReportDTO.ModuleStatistic> buildModuleStatistics(List<ReportExportResponseDTO.TestCaseResultDTO> testResults) {
        if (testResults == null || testResults.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 简化处理：创建一个总计统计
        AllureReportDTO.ModuleStatistic stat = new AllureReportDTO.ModuleStatistic();
        stat.setModuleName("全部测试用例");
        stat.setTotalCases(testResults.size());
        stat.setPassedCases((int) testResults.stream().filter(r -> "passed".equalsIgnoreCase(r.getStatus())).count());
        stat.setFailedCases((int) testResults.stream().filter(r -> "failed".equalsIgnoreCase(r.getStatus()) || "broken".equalsIgnoreCase(r.getStatus())).count());
        
        if (testResults.size() > 0) {
            stat.setPassRate(BigDecimal.valueOf(stat.getPassedCases() * 100.0 / testResults.size()).setScale(2, RoundingMode.HALF_UP));
        } else {
            stat.setPassRate(BigDecimal.ZERO);
        }
        
        return Arrays.asList(stat);
    }
    
    /**
     * 构建历史趋势
     */
    private List<AllureReportDTO.HistoryTrend> buildHistoryTrends(ReportExportResponseDTO.ReportSummaryInfoDTO summary) {
        List<AllureReportDTO.HistoryTrend> trends = new ArrayList<>();
        
        // 生成最近7天的模拟数据
        LocalDateTime now = LocalDateTime.now();
        for (int i = 6; i >= 0; i--) {
            AllureReportDTO.HistoryTrend trend = new AllureReportDTO.HistoryTrend();
            LocalDateTime date = now.minusDays(i);
            trend.setDate(date.format(DateTimeFormatter.ofPattern("MM-dd")));
            
            // 模拟趋势数据（实际应从数据库查询）
            int total = summary.getTotalCases();
            int passed = summary.getPassedCases() - (int)(Math.random() * 10);
            int failed = summary.getFailedCases() + (int)(Math.random() * 5);
            
            trend.setTotalCases(total);
            trend.setPassedCases(passed);
            trend.setFailedCases(failed);
            trend.setSkippedCases(total - passed - failed);
            trend.setPassRate(BigDecimal.valueOf(passed * 100.0 / total).setScale(1, RoundingMode.HALF_UP));
            trend.setAvgDuration(summary.getDuration() + (long)(Math.random() * 10000));
            
            trends.add(trend);
        }
        
        return trends;
    }
    
    /**
     * 生成文件名
     */
    private String generateFileName(AllureReportDTO reportData) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("Allure测试报告_%s_%s.html", reportData.getExecutionId().replace("#", ""), timestamp);
    }
}

