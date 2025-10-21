package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 执行结果响应DTO
 */
@Data
public class ExecutionResultDTO {

    /**
     * 执行ID
     */
    private Long executionId;

    /**
     * 用例ID
     */
    private Integer caseId;

    /**
     * 用例名称
     */
    private String caseName;

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
     * HTTP响应状态码
     */
    private Integer responseStatus;

    /**
     * 通过的断言数
     */
    private Integer assertionsPassed;

    /**
     * 失败的断言数
     */
    private Integer assertionsFailed;

    /**
     * 失败信息
     */
    private String failureMessage;

    /**
     * 日志链接
     */
    private String logsLink;

    /**
     * 报告ID
     */
    private Long reportId;

    /**
     * 任务ID（异步执行时使用）
     */
    private String taskId;

    /**
     * 预估等待时间（秒）
     */
    private Integer estimatedWaitTime;

    /**
     * 队列位置
     */
    private Integer queuePosition;

    /**
     * 监控URL
     */
    private String monitorUrl;
    
    /**
     * 详细的断言结果列表
     */
    private List<AssertionDetailDTO> assertionDetails;
    
    /**
     * HTTP响应体
     */
    private String responseBody;
    
    /**
     * HTTP响应头
     */
    private Map<String, String> responseHeaders;
    
    /**
     * 提取的变量
     */
    private Map<String, Object> extractedVariables;
    
    /**
     * 失败类型
     */
    private String failureType;
    
    /**
     * 失败堆栈跟踪
     */
    private String failureTrace;
    
    /**
     * 断言详情DTO
     */
    @Data
    public static class AssertionDetailDTO {
        private Integer assertionId;
        private String assertionType;
        private String description;
        private String expectedValue;
        private String actualValue;
        private Boolean passed;
        private String errorMessage;
        private String jsonPath;  // JSON路径（如果适用）
    }
}
