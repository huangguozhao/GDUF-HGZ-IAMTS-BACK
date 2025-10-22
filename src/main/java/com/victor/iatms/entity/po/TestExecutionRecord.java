package com.victor.iatms.entity.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 测试执行记录实体类
 */
@Data
public class TestExecutionRecord {

    /**
     * 记录ID，自增主键
     */
    private Long recordId;

    /**
     * 执行范围类型：api, module, project, test_suite, test_case
     */
    private String executionScope;

    /**
     * 根据execution_scope关联对应表的ID
     */
    private Integer refId;

    /**
     * 执行范围的名称
     */
    private String scopeName;

    /**
     * 执行人ID，关联Users表
     */
    private Integer executedBy;

    /**
     * 执行类型：manual（手动）, scheduled（定时）, triggered（触发）
     */
    private String executionType;

    /**
     * 测试环境
     */
    private String environment;

    /**
     * 执行状态：running（运行中）, completed（完成）, failed（失败）, cancelled（取消）
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
     * 执行配置信息（JSON格式）
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
     * 触发任务ID，关联ScheduledTasks表
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
}

