package com.victor.iatms.entity.dto;

import com.victor.iatms.entity.po.TestExecutionRecord;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 测试执行记录详情DTO
 */
@Data
public class TestExecutionRecordDetailDTO {
    
    /**
     * 记录ID
     */
    private Long recordId;
    
    /**
     * 执行范围类型
     */
    private String executionScope;
    
    /**
     * 关联ID
     */
    private Integer refId;
    
    /**
     * 执行范围的名称
     */
    private String scopeName;
    
    /**
     * 执行人ID
     */
    private Integer executedBy;
    
    /**
     * 执行人姓名
     */
    private String executorName;
    
    /**
     * 执行类型
     */
    private String executionType;
    
    /**
     * 测试环境
     */
    private String environment;
    
    /**
     * 执行状态
     */
    private String status;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 执行耗时（秒）
     */
    private Integer durationSeconds;
    
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
     * 跳过用例数
     */
    private Integer skippedCases;
    
    /**
     * 成功率
     */
    private BigDecimal successRate;
    
    /**
     * 浏览器类型
     */
    private String browser;
    
    /**
     * 应用版本
     */
    private String appVersion;
    
    /**
     * 执行配置信息
     */
    private String executionConfig;
    
    /**
     * 报告访问地址
     */
    private String reportUrl;
    
    /**
     * 日志文件路径
     */
    private String logFilePath;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 触发任务ID
     */
    private Long triggeredTaskId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 从实体转换为DTO
     */
    public static TestExecutionRecordDetailDTO fromEntity(TestExecutionRecord record) {
        if (record == null) {
            return null;
        }
        
        TestExecutionRecordDetailDTO dto = new TestExecutionRecordDetailDTO();
        dto.setRecordId(record.getRecordId());
        dto.setExecutionScope(record.getExecutionScope());
        dto.setRefId(record.getRefId());
        dto.setScopeName(record.getScopeName());
        dto.setExecutedBy(record.getExecutedBy());
        dto.setExecutionType(record.getExecutionType());
        dto.setEnvironment(record.getEnvironment());
        dto.setStatus(record.getStatus());
        dto.setStartTime(record.getStartTime());
        dto.setEndTime(record.getEndTime());
        dto.setDurationSeconds(record.getDurationSeconds());
        dto.setTotalCases(record.getTotalCases());
        dto.setExecutedCases(record.getExecutedCases());
        dto.setPassedCases(record.getPassedCases());
        dto.setFailedCases(record.getFailedCases());
        dto.setSkippedCases(record.getSkippedCases());
        dto.setSuccessRate(record.getSuccessRate());
        dto.setBrowser(record.getBrowser());
        dto.setAppVersion(record.getAppVersion());
        dto.setExecutionConfig(record.getExecutionConfig());
        dto.setReportUrl(record.getReportUrl());
        dto.setLogFilePath(record.getLogFilePath());
        dto.setErrorMessage(record.getErrorMessage());
        dto.setTriggeredTaskId(record.getTriggeredTaskId());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());
        
        return dto;
    }
}

