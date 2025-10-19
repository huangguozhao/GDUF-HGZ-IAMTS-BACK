package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 导入测试用例DTO
 */
@Data
public class ImportTestCaseDTO {

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
     * 是否为模板用例
     */
    private Boolean isTemplate;

    /**
     * 模板用例ID
     */
    private Integer templateId;

    /**
     * 版本号
     */
    private String version;

    /**
     * 行号（用于错误报告）
     */
    private Integer rowNumber;
}
