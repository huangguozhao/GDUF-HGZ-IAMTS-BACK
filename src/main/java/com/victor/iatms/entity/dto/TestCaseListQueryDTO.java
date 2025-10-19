package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页获取测试用例列表查询DTO
 */
@Data
public class TestCaseListQueryDTO {

    /**
     * 接口ID过滤
     */
    private Integer apiId;

    /**
     * 模块ID过滤
     */
    private Integer moduleId;

    /**
     * 项目ID过滤
     */
    private Integer projectId;

    /**
     * 用例名称模糊查询
     */
    private String name;

    /**
     * 用例编码精确查询
     */
    private String caseCode;

    /**
     * 优先级过滤。可选: P0, P1, P2, P3
     */
    private String priority;

    /**
     * 严重程度过滤。可选: critical, high, medium, low
     */
    private String severity;

    /**
     * 状态过滤。可选: active, inactive
     */
    private String status;

    /**
     * 是否模板用例过滤。可选: true, false
     */
    private Boolean isTemplate;

    /**
     * 标签过滤（支持多个标签）
     */
    private List<String> tags;

    /**
     * 创建人ID过滤
     */
    private Integer createdBy;

    /**
     * 是否包含已删除的用例，默认: false
     */
    private Boolean includeDeleted;

    /**
     * 关键字搜索（用例名称、描述）
     */
    private String searchKeyword;

    /**
     * 排序字段。可选: name, case_code, priority, severity, created_at, updated_at，默认: created_at
     */
    private String sortBy;

    /**
     * 排序顺序。可选: asc, desc，默认: desc
     */
    private String sortOrder;

    /**
     * 分页查询的页码，默认为 1
     */
    private Integer page;

    /**
     * 分页查询的每页记录数，默认为 20，最大 100
     */
    private Integer pageSize;
}
