package com.victor.iatms.entity.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 创建测试用例请求DTO
 */
@Data
public class CreateTestCaseDTO {

    /**
     * 用例编码，如不提供则自动生成
     */
    @Size(max = 50, message = "用例编码长度不能超过50个字符")
    private String caseCode;

    /**
     * 用例名称
     */
    @NotBlank(message = "用例名称不能为空")
    @Size(max = 255, message = "用例名称长度不能超过255个字符")
    private String name;

    /**
     * 用例描述
     */
    @Size(max = 1000, message = "用例描述长度不能超过1000个字符")
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
     * 标签数组
     */
    private List<String> tags;

    /**
     * 前置条件配置
     */
    private String preConditions;

    /**
     * 测试步骤
     */
    private String testSteps;

    /**
     * 请求参数覆盖配置
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
     * 响应提取规则
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
}
