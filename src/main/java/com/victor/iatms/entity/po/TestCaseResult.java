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
}
