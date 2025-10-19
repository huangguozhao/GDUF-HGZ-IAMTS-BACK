package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 导出测试用例DTO
 */
@Data
public class ExportTestCaseDTO {

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
     * 标签（业务逻辑使用）
     */
    private List<String> tags;

    /**
     * 标签JSON字符串（数据库映射使用）
     */
    private String tagsJson;

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
     * 是否为模板用例
     */
    private Boolean isTemplate;

    /**
     * 版本号
     */
    private String version;

    /**
     * 创建人ID
     */
    private Integer createdBy;

    /**
     * 创建人姓名
     */
    private String creatorName;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
