package com.victor.iatms.entity.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试结果实体类
 */
@Data
public class TestCaseResult {

    /**
     * 结果ID
     */
    private Long resultId;

    /**
     * 测试执行记录ID，关联TestExecutionRecords表
     */
    private Long executionRecordId;

    /**
     * 报告ID
     */
    private Long reportId;

    /**
     * 执行记录ID
     */
    private Long executionId;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 引用ID
     */
    private Integer refId;

    /**
     * 完整名称
     */
    private String fullName;

    /**
     * 执行状态
     */
    private String status;

    /**
     * 执行耗时（毫秒）
     */
    private Long duration;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 失败信息
     */
    private String failureMessage;

    /**
     * 失败堆栈跟踪
     */
    private String failureTrace;

    /**
     * 失败类型
     */
    private String failureType;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 测试步骤执行详情
     */
    private String stepsJson;

    /**
     * 测试参数信息
     */
    private String parametersJson;

    /**
     * 附件信息
     */
    private String attachmentsJson;

    /**
     * 日志链接
     */
    private String logsLink;

    /**
     * 截图链接
     */
    private String screenshotLink;

    /**
     * 视频录制链接
     */
    private String videoLink;

    /**
     * 执行环境
     */
    private String environment;

    /**
     * 浏览器信息
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 设备信息
     */
    private String device;

    /**
     * 标签信息
     */
    private String tagsJson;

    /**
     * 严重程度
     */
    private String severity;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 是否不稳定用例
     */
    private Boolean flaky;

    /**
     * 记录创建时间
     */
    private LocalDateTime createdAt;

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

    // ==================== 新增字段 ====================

    /**
     * 用例ID（关联TestCases表）
     */
    private Integer caseId;

    /**
     * 用例编码
     */
    private String caseCode;

    /**
     * 用例名称
     */
    private String caseName;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 测试套件名称
     */
    private String suiteName;

    /**
     * 包名/命名空间
     */
    private String packageName;

    /**
     * Epic名称
     */
    private String epicName;

    /**
     * Feature名称
     */
    private String featureName;

    /**
     * Story名称
     */
    private String storyName;

    /**
     * 测试层级（UNIT, INTEGRATION, API, E2E, PERFORMANCE, SECURITY）
     */
    private String testLayer;

    /**
     * 测试类型（POSITIVE, NEGATIVE, BOUNDARY, SECURITY, PERFORMANCE, USABILITY）
     */
    private String testType;

    /**
     * 不稳定次数
     */
    private Integer flakyCount;

    /**
     * 最后一次不稳定时间
     */
    private LocalDateTime lastFlakyTime;

    /**
     * 历史趋势数据（JSON格式）
     */
    private String historyTrend;

    /**
     * 自定义标签（JSON格式）
     */
    private String customLabels;

    /**
     * 根因分析
     */
    private String rootCauseAnalysis;

    /**
     * 影响评估（HIGH, MEDIUM, LOW）
     */
    private String impactAssessment;

    /**
     * 复测结果（PASSED, FAILED, NOT_RETESTED）
     */
    private String retestResult;

    /**
     * 复测备注
     */
    private String retestNotes;
}
