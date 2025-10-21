package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 复制测试用例响应DTO
 */
@Data
public class CopyTestCaseResponseDTO {

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
     * 接口ID
     */
    private Integer apiId;

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
    private List<Map<String, Object>> preConditions;

    /**
     * 测试步骤
     */
    private List<Map<String, Object>> testSteps;

    /**
     * 请求参数覆盖
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
     * 提取器
     */
    private List<Map<String, Object>> extractors;

    /**
     * 验证器
     */
    private List<Map<String, Object>> validators;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 是否为模板
     */
    private Boolean isTemplate;

    /**
     * 模板ID
     */
    private Integer templateId;

    /**
     * 版本
     */
    private String version;

    /**
     * 创建者信息
     */
    private CreatorInfoDTO creatorInfo;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

