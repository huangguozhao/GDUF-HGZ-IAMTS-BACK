package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 测试用例响应DTO
 */
@Data
public class TestCaseDTO {

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
     * 标签数组（业务逻辑使用）
     */
    private List<String> tags;

    /**
     * 标签JSON字符串（数据库映射使用）
     */
    private String tagsJson;

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
