package com.victor.iatms.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 测试执行记录统计DTO
 */
@Data
public class TestExecutionRecordStatisticsDTO {
    
    /**
     * 总执行次数
     */
    private Long totalExecutions;
    
    /**
     * 运行中的执行数
     */
    private Long runningExecutions;
    
    /**
     * 完成的执行数
     */
    private Long completedExecutions;
    
    /**
     * 失败的执行数
     */
    private Long failedExecutions;
    
    /**
     * 取消的执行数
     */
    private Long cancelledExecutions;
    
    /**
     * 平均执行时长（秒）
     */
    private Double avgDurationSeconds;
    
    /**
     * 最大执行时长（秒）
     */
    private Integer maxDurationSeconds;
    
    /**
     * 最小执行时长（秒）
     */
    private Integer minDurationSeconds;
    
    /**
     * 总用例数
     */
    private Long totalCases;
    
    /**
     * 总通过数
     */
    private Long totalPassedCases;
    
    /**
     * 总失败数
     */
    private Long totalFailedCases;
    
    /**
     * 总跳过数
     */
    private Long totalSkippedCases;
    
    /**
     * 平均成功率
     */
    private BigDecimal avgSuccessRate;
}

