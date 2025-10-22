package com.victor.iatms.entity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 更新测试执行记录DTO
 */
@Data
public class UpdateTestExecutionRecordDTO {
    
    /**
     * 执行状态（running, completed, failed, cancelled）
     */
    private String status;
    
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
}

