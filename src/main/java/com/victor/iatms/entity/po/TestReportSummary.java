package com.victor.iatms.entity.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 测试报告汇总表实体类
 */
@Data
public class TestReportSummary {
    
    /**
     * 报告ID，自增主键
     */
    private Long reportId;
    
    /**
     * 报告名称
     */
    private String reportName;
    
    /**
     * 报告类型：execution, coverage, trend, comparison, custom
     */
    private String reportType;
    
    /**
     * 执行记录ID，关联TaskExecutionHistory表
     */
    private Long executionId;
    
    /**
     * 项目ID，关联Projects表
     */
    private Integer projectId;
    
    /**
     * 测试环境
     */
    private String environment;
    
    /**
     * 测试开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 测试结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 总耗时（毫秒）
     */
    private Long duration;
    
    /**
     * 总用例数
     */
    private Integer totalCases;
    
    /**
     * 已执行用例数
     */
    private Integer executedCases;
    
    /**
     * 通过用例数
     */
    private Integer passedCases;
    
    /**
     * 失败用例数
     */
    private Integer failedCases;
    
    /**
     * 中断用例数
     */
    private Integer brokenCases;
    
    /**
     * 跳过用例数
     */
    private Integer skippedCases;
    
    /**
     * 成功率
     */
    private BigDecimal successRate;
    
    /**
     * 总执行耗时（毫秒）
     */
    private Long totalDuration;
    
    /**
     * 平均用例耗时（毫秒）
     */
    private Long avgDuration;
    
    /**
     * 最大用例耗时（毫秒）
     */
    private Long maxDuration;
    
    /**
     * 最小用例耗时（毫秒）
     */
    private Long minDuration;
    
    /**
     * 报告状态：generating, completed, failed
     */
    private String reportStatus;
    
    /**
     * 报告格式：html, pdf, excel, json, xml
     */
    private String fileFormat;
    
    /**
     * 报告文件存储路径
     */
    private String filePath;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 下载地址
     */
    private String downloadUrl;
    
    /**
     * 生成人员ID，关联Users表
     */
    private Integer generatedBy;
    
    /**
     * 报告标签
     */
    private String tagsJson;
    
    /**
     * 汇总统计信息
     */
    private String summaryJson;
    
    /**
     * 趋势数据
     */
    private String trendDataJson;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否删除
     */
    private Boolean isDeleted;
    
    /**
     * 删除时间
     */
    private LocalDateTime deletedAt;
    
    /**
     * 删除人ID
     */
    private Integer deletedBy;
    
    // ==================== ISO报告相关新增字段 ====================
    
    /**
     * 报告配置ID
     */
    private Integer reportConfigId;
    
    /**
     * ISO标准指标数据（JSON格式）
     */
    private String isoMetrics;
    
    /**
     * 执行摘要
     */
    private String executiveSummary;
    
    /**
     * 结论建议
     */
    private String conclusionRecommendation;
    
    /**
     * 风险评估数据（JSON格式）
     */
    private String riskAssessment;
    
    /**
     * 缺陷分析数据（JSON格式）
     */
    private String defectAnalysis;
    
    /**
     * 环境详细信息（JSON格式）
     */
    private String environmentDetails;
    
    /**
     * 测试范围详情（JSON格式）
     */
    private String testScopeDetails;
}