package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 添加测试用例请求DTO
 */
@Data
public class AddTestCaseDTO {

    /**
     * 接口ID
     */
    private Integer apiId;

    /**
     * 用例编码，如不提供则自动生成
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
     * 优先级，默认: P2。可选: P0, P1, P2, P3
     */
    private String priority;

    /**
     * 严重程度，默认: medium。可选: critical, high, medium, low
     */
    private String severity;

    /**
     * 标签数组
     */
    private List<String> tags;

    /**
     * 前置条件配置
     */
    private List<Map<String, Object>> preConditions;

    /**
     * 测试步骤
     */
    private List<Map<String, Object>> testSteps;

    /**
     * 请求参数覆盖配置
     */
    private Map<String, Object> requestOverride;

    /**
     * 预期HTTP状态码
     */
    private Integer expectedHttpStatus;

    /**
     * 预期响应Schema
     */
    private Map<String, Object> expectedResponseSchema;

    /**
     * 预期响应体
     */
    private String expectedResponseBody;

    /**
     * 断言规则
     */
    private List<Map<String, Object>> assertions;

    /**
     * 响应提取规则
     */
    private List<Map<String, Object>> extractors;

    /**
     * 验证器配置
     */
    private List<Map<String, Object>> validators;

    /**
     * 是否启用，默认: true
     */
    private Boolean isEnabled;

    /**
     * 是否为模板用例，默认: false
     */
    private Boolean isTemplate;

    /**
     * 模板用例ID
     */
    private Integer templateId;
}
