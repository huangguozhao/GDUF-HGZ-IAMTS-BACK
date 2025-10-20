package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 测试结果基本信息DTO
 */
@Data
public class TestResultInfoDTO {

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
     * 完整名称
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
     * 重试次数
     */
    @JsonProperty("retry_count")
    private Integer retryCount;

    /**
     * 是否是不稳定用例
     */
    private Boolean flaky;
}


