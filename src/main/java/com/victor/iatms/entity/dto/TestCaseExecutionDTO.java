package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 测试用例执行信息DTO
 */
@Data
public class TestCaseExecutionDTO {

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
    private String name;

    /**
     * 用例描述
     */
    private String description;

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

    /**
     * 前置条件
     */
    private String preConditions;

    /**
     * 测试步骤
     */
    private String testSteps;

    /**
     * 请求参数覆盖
     */
    private String requestOverride;

    /**
     * 预期HTTP状态码
     */
    private Integer expectedHttpStatus;

    /**
     * 预期响应Schema
     */
    private String expectedResponseSchema;

    /**
     * 预期响应体
     */
    private String expectedResponseBody;

    /**
     * 断言规则
     */
    private String assertions;

    /**
     * 提取规则
     */
    private String extractors;

    /**
     * 验证器配置
     */
    private String validators;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 版本号
     */
    private String version;

    /**
     * 关联的接口信息
     */
    private ApiInfoDTO apiInfo;

    /**
     * 执行环境
     */
    private String environment;

    /**
     * 执行变量
     */
    private Map<String, Object> variables;

    /**
     * 执行开始时间
     */
    private LocalDateTime executionStartTime;

    /**
     * 执行结束时间
     */
    private LocalDateTime executionEndTime;

    /**
     * 执行状态
     */
    private String executionStatus;

    /**
     * 执行耗时
     */
    private Long executionDuration;

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
     * HTTP响应状态码
     */
    private Integer httpResponseStatus;

    /**
     * HTTP响应体
     */
    private String httpResponseBody;

    /**
     * HTTP响应头
     */
    private Map<String, String> httpResponseHeaders;

    /**
     * 断言结果
     */
    private List<AssertionResultDTO> assertionResults;

    /**
     * 提取结果
     */
    private Map<String, Object> extractedValues;

    /**
     * 执行日志
     */
    private String executionLogs;

    /**
     * 截图链接
     */
    private String screenshotLink;

    /**
     * 视频链接
     */
    private String videoLink;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 是否不稳定用例
     */
    private Boolean flaky;

    /**
     * 数据类：接口信息
     */
    @Data
    public static class ApiInfoDTO {
        private Integer apiId;
        private String apiCode;
        private String name;
        private String method;
        private String path;
        private String baseUrl;
        private String fullUrl;
        private String requestParameters;
        private String pathParameters;
        private String requestHeaders;
        private String requestBody;
        private String requestBodyType;
        private String responseBodyType;
        private String description;
        private String status;
        private String version;
        private Integer timeoutSeconds;
        private String authType;
        private String authConfig;
        private String tags;
        private String examples;
    }

    /**
     * 数据类：断言结果
     */
    @Data
    public static class AssertionResultDTO {
        private String assertionType;
        private String expectedValue;
        private String actualValue;
        private Boolean passed;
        private String message;
        private String errorMessage;
    }
}
