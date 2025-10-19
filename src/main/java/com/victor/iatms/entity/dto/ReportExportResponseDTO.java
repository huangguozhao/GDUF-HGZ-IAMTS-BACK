package com.victor.iatms.entity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 报告导出响应DTO
 */
@Data
public class ReportExportResponseDTO {
    
    /**
     * 报告摘要信息
     */
    private ReportSummaryInfoDTO reportSummary;
    
    /**
     * 统计信息
     */
    private ReportStatisticsDTO statistics;
    
    /**
     * 测试结果列表
     */
    private List<TestCaseResultDTO> testResults;
    
    /**
     * 导出元数据
     */
    private ExportMetadataDTO exportMetadata;
    
    /**
     * 报告摘要信息内部类
     */
    @Data
    public static class ReportSummaryInfoDTO {
        private Long reportId;
        private String reportName;
        private String reportType;
        private Integer projectId;
        private String projectName;
        private String environment;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Long duration;
        private Integer totalCases;
        private Integer executedCases;
        private Integer passedCases;
        private Integer failedCases;
        private Integer brokenCases;
        private Integer skippedCases;
        private BigDecimal successRate;
    }
    
    /**
     * 统计信息内部类
     */
    @Data
    public static class ReportStatisticsDTO {
        private Map<String, Integer> byStatus;
        private Map<String, Integer> byPriority;
        private Map<String, Integer> bySeverity;
    }
    
    /**
     * 测试用例结果内部类
     */
    @Data
    public static class TestCaseResultDTO {
        private Long resultId;
        private Integer caseId;
        private String caseCode;
        private String caseName;
        private String status;
        private Long duration;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String priority;
        private String severity;
        private String failureMessage;
        private String failureTrace;
        private String failureType;
        private String environment;
        private String browser;
        private String os;
        private String device;
        private Integer retryCount;
        private Boolean flaky;
        private List<String> tags;
    }
    
    /**
     * 导出元数据内部类
     */
    @Data
    public static class ExportMetadataDTO {
        private String exportFormat;
        private LocalDateTime exportedAt;
        private Integer exportedBy;
        private Boolean includeDetails;
        private Boolean includeAttachments;
    }
}
