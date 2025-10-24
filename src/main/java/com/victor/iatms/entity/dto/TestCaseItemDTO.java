package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 测试用例项DTO
 */
@Data
public class TestCaseItemDTO {

    /**
     * 用例ID
     */
    private Integer caseId;

    /**
     * 用例编码
     */
    private String caseCode;

    /**
     * 接口ID
     */
    private Integer apiId;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口方法
     */
    private String apiMethod;

    /**
     * 接口路径
     */
    private String apiPath;

    /**
     * 模块ID
     */
    private Integer moduleId;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 用例名称
     */
    private String name;

    /**
     * 用例描述
     */
    private String description;

    /**
     * 测试类型：functional-功能, performance-性能, security-安全, 
     * compatibility-兼容性, smoke-冒烟, regression-回归
     */
    private String testType;

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
     * 前置条件
     */
    private Object preConditions;
    
    /**
     * 测试步骤
     */
    private Object testSteps;
    
    /**
     * 请求参数覆盖
     */
    private Object requestOverride;
    
    /**
     * 预期HTTP状态码
     */
    private Integer expectedHttpStatus;
    
    /**
     * 预期响应Schema
     */
    private Object expectedResponseSchema;
    
    /**
     * 预期响应体
     */
    private String expectedResponseBody;
    
    /**
     * 断言规则
     */
    private Object assertions;
    
    /**
     * 响应提取规则
     */
    private Object extractors;
    
    /**
     * 验证器配置
     */
    private Object validators;

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
     * 创建人信息
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

    /**
     * 是否已删除
     */
    private Boolean isDeleted;
}
