package com.victor.iatms.entity.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试用例实体类
 */
@Data
public class TestCase {

    /**
     * 用例ID，自增主键
     */
    private Integer caseId;

    /**
     * 用例编码，接口内唯一
     */
    private String caseCode;

    /**
     * 接口ID，关联Apis表的主键
     */
    private Integer apiId;

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
     * 标签，JSON数组格式
     */
    private String tags;

    /**
     * 前置条件，JSON格式
     */
    private String preConditions;

    /**
     * 测试步骤，JSON格式
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
     * 预期响应Schema，JSON格式
     */
    private String expectedResponseSchema;

    /**
     * 预期响应体
     */
    private String expectedResponseBody;

    /**
     * 断言规则，JSON格式
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

    /**
     * 版本号
     */
    private String version;

    /**
     * 创建人ID，关联用户表
     */
    private Integer createdBy;

    /**
     * 更新人ID
     */
    private Integer updatedBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 是否删除
     */
    private Boolean isDeleted;

    /**
     * 删除时间
     */
    private LocalDateTime deletedAt;

    /**
     * 删除人ID
     */
    private Integer deletedBy;
}
