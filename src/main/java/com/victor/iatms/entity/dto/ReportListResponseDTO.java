package com.victor.iatms.entity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 报告列表响应DTO
 */
@Data
public class ReportListResponseDTO {
    
    /**
     * 报告ID
     */
    private Long reportId;
    
    /**
     * 报告名称
     */
    private String reportName;
    
    /**
     * 报告类型
     */
    private String reportType;
    
    /**
     * 项目ID
     */
    private Integer projectId;
    
    /**
     * 项目名称
     */
    private String projectName;
    
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
     * 报告状态
     */
    private String reportStatus;
    
    /**
     * 文件格式
     */
    private String fileFormat;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 下载地址
     */
    private String downloadUrl;
    
    /**
     * 生成人员ID
     */
    private Integer generatedBy;
    
    /**
     * 生成人员姓名
     */
    private String generatorName;
    
    /**
     * 报告标签
     */
    private List<String> tags;
    
    /**
     * 报告配置ID
     */
    private Integer reportConfigId;
    
    /**
     * 执行摘要
     */
    private String executiveSummary;
    
    /**
     * 结论建议
     */
    private String conclusionRecommendation;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 是否已删除
     */
    private Boolean isDeleted;
}
