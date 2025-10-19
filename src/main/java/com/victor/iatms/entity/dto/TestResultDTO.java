package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 测试结果DTO
 */
@Data
public class TestResultDTO {

    /**
     * 结果ID
     */
    @JsonProperty("result_id")
    private Long resultId;

    /**
     * 报告ID
     */
    @JsonProperty("report_id")
    private Long reportId;

    /**
     * 执行记录ID
     */
    @JsonProperty("execution_id")
    private Long executionId;

    /**
     * 任务类型
     */
    @JsonProperty("task_type")
    private String taskType;

    /**
     * 关联对象ID
     */
    @JsonProperty("ref_id")
    private Integer refId;

    /**
     * 关联对象名称
     */
    @JsonProperty("ref_name")
    private String refName;

    /**
     * 完整名称（包含路径）
     */
    @JsonProperty("full_name")
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
    @JsonProperty("start_time")
    private String startTime;

    /**
     * 结束时间
     */
    @JsonProperty("end_time")
    private String endTime;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 严重程度
     */
    private String severity;

    /**
     * 执行环境
     */
    private String environment;

    /**
     * 失败信息（摘要）
     */
    @JsonProperty("failure_message")
    private String failureMessage;

    /**
     * 失败类型
     */
    @JsonProperty("failure_type")
    private String failureType;

    /**
     * 重试次数
     */
    @JsonProperty("retry_count")
    private Integer retryCount;

    /**
     * 浏览器信息
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 日志链接
     */
    @JsonProperty("logs_link")
    private String logsLink;

    /**
     * 截图链接
     */
    @JsonProperty("screenshot_link")
    private String screenshotLink;
}

