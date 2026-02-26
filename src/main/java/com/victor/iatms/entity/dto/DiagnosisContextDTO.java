package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 完整的测试执行数据（用于AI诊断）
 */
@Data
public class DiagnosisContextDTO {

    /**
     * 执行记录ID
     */
    private Long executionId;

    /**
     * 用例ID
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
     * 用例描述
     */
    private String caseDescription;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 严重程度
     */
    private String severity;

    /**
     * 标签
     */
    private List<String> tags;

    // ==================== 接口信息 ====================

    /**
     * 接口ID
     */
    private Integer apiId;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口路径
     */
    private String apiPath;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求URL
     */
    private String url;

    /**
     * 请求头
     */
    private Map<String, String> requestHeaders;

    /**
     * 请求体
     */
    private String requestBody;

    /**
     * 期望响应体
     */
    private String expectedResponseBody;

    /**
     * 期望HTTP状态码
     */
    private Integer expectedHttpStatus;

    // ==================== 模块信息 ====================

    /**
     * 模块ID
     */
    private Integer moduleId;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 项目名称
     */
    private String projectName;

    // ==================== 执行结果信息 ====================

    /**
     * 执行状态
     */
    private String status;

    /**
     * 总用例数（接口/模块级别）
     */
    private Integer totalCases;

    /**
     * 成功率（接口/模块级别）
     */
    private Double successRate;

    /**
     * 失败信息
     */
    private String failureMessage;

    /**
     * 失败类型
     */
    private String failureType;

    /**
     * 失败堆栈
     */
    private String failureTrace;

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
     * 执行环境
     */
    private String environment;

    /**
     * HTTP响应状态码
     */
    private Integer responseStatus;

    /**
     * HTTP响应体
     */
    private String responseBody;

    /**
     * HTTP响应头
     */
    private Map<String, String> responseHeaders;

    // ==================== 断言信息 ====================

    /**
     * 断言结果列表
     */
    private List<AssertionDetailDTO> assertionDetails;

    /**
     * 通过的断言数
     */
    private Integer assertionsPassed;

    /**
     * 失败的断言数
     */
    private Integer assertionsFailed;

    // ==================== 提取的变量 ====================

    /**
     * 提取的变量
     */
    private Map<String, Object> extractedVariables;

    // ==================== 历史信息 ====================

    /**
     * 历史执行次数
     */
    private Integer totalExecutions;

    /**
     * 历史失败次数
     */
    private Integer totalFailures;

    /**
     * 最近一次执行状态
     */
    private String lastExecutionStatus;

    /**
     * 最近执行时间
     */
    private LocalDateTime lastExecutionTime;

    /**
     * 用例结果摘要（用于接口/模块级别诊断）
     */
    private String caseResultsSummary;

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
        private String jsonPath;
    }
}

