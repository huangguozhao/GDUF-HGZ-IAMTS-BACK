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
