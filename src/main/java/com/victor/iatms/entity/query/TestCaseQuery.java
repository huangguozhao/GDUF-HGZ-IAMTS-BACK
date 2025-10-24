package com.victor.iatms.entity.query;

import lombok.Data;

/**
 * 测试用例查询参数
 */
@Data
public class TestCaseQuery {

    /**
     * 接口ID
     */
    private Integer apiId;

    /**
     * 用例名称（模糊查询）
     */
    private String name;

    /**
     * 优先级过滤
     */
    private String priority;

    /**
     * 严重程度过滤
     */
    private String severity;

    /**
     * 测试类型过滤：functional, performance, security, compatibility, smoke, regression
     */
    private String testType;

    /**
     * 是否启用过滤
     */
    private Boolean isEnabled;

    /**
     * 是否模板用例过滤
     */
    private Boolean isTemplate;

    /**
     * 标签过滤（支持多个标签）
     */
    private String[] tags;

    /**
     * 创建人ID过滤
     */
    private Integer createdBy;

    /**
     * 页码，默认为1
     */
    private Integer page = 1;

    /**
     * 每页条数，默认为10，最大100
     */
    private Integer pageSize = 10;
}
