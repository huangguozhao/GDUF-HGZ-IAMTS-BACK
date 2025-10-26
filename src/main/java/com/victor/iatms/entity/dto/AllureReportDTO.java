package com.victor.iatms.entity.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

/**
 * Allure风格测试报告DTO
 * 面向技术人员（测试和开发）的详细报告
 * 
 * @author Victor
 * @since 2024-10-26
 */
@Data
public class AllureReportDTO {
    
    // ==================== 基础信息 ====================
    private String reportTitle;
    private String projectName;
    private Long reportId;
    private String executionId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long totalDuration; // 毫秒
    
    // ==================== 统计数据 ====================
    private Integer totalCases;
    private Integer executedCases;
    private Integer passedCases;
    private Integer failedCases;
    private Integer brokenCases;
    private Integer skippedCases;
    private BigDecimal successRate;
    
    // ==================== 测试套件列表 ====================
    private List<TestSuite> testSuites;
    
    // ==================== 模块统计 ====================
    private List<ModuleStatistic> moduleStatistics;
    
    // ==================== 历史趋势数据 ====================
    private List<HistoryTrend> historyTrends;
    
    /**
     * 测试套件
     */
    @Data
    public static class TestSuite {
        private String suiteName;
        private String suiteDescription;
        private String suiteIcon;
        private Integer totalCases;
        private Integer passedCases;
        private Integer failedCases;
        private Integer brokenCases;
        private Integer skippedCases;
        private Long duration;
        
        // 测试用例列表
        private List<TestCase> testCases;
    }
    
    /**
     * 测试用例
     */
    @Data
    public static class TestCase {
        private Long caseId;
        private String caseCode;
        private String caseName;
        private String status; // passed, failed, broken, skipped
        private Long duration;
        private LocalDateTime startTime;
        private String priority;
        private String severity;
        private List<String> tags;
        
        // 测试步骤
        private List<TestStep> steps;
        
        // 测试参数
        private List<TestParameter> parameters;
        
        // 断言列表
        private List<Assertion> assertions;
        
        // HTTP请求信息
        private HttpRequest httpRequest;
        private HttpResponse httpResponse;
        
        // 错误信息（如果失败）
        private FailureInfo failureInfo;
        
        // 附件
        private List<Attachment> attachments;
    }
    
    /**
     * 测试步骤
     */
    @Data
    public static class TestStep {
        private Integer stepIndex;
        private String stepTitle;
        private String stepDescription;
        private String status; // passed, failed
        private Long duration;
        private String errorMessage;
    }
    
    /**
     * 测试参数
     */
    @Data
    public static class TestParameter {
        private String paramName;
        private String paramValue;
        private String paramType;
    }
    
    /**
     * 断言
     */
    @Data
    public static class Assertion {
        private String assertionName;
        private String expected;
        private String actual;
        private String status; // passed, failed
    }
    
    /**
     * HTTP请求
     */
    @Data
    public static class HttpRequest {
        private String method; // GET, POST, PUT, DELETE
        private String url;
        private String headers;
        private String body;
    }
    
    /**
     * HTTP响应
     */
    @Data
    public static class HttpResponse {
        private Integer statusCode;
        private String headers;
        private String body;
        private Long duration;
    }
    
    /**
     * 失败信息
     */
    @Data
    public static class FailureInfo {
        private String errorType;
        private String errorMessage;
        private String stackTrace;
    }
    
    /**
     * 附件
     */
    @Data
    public static class Attachment {
        private String attachmentName;
        private String attachmentType; // image, text, json, log
        private String attachmentSize;
        private String attachmentUrl;
    }
    
    /**
     * 模块统计
     */
    @Data
    public static class ModuleStatistic {
        private String moduleName;
        private Integer totalCases;
        private Integer passedCases;
        private Integer failedCases;
        private BigDecimal passRate;
    }
    
    /**
     * 历史趋势
     */
    @Data
    public static class HistoryTrend {
        private String date;
        private Integer totalCases;
        private Integer passedCases;
        private Integer failedCases;
        private Integer skippedCases;
        private BigDecimal passRate;
        private Long avgDuration;
    }
}

