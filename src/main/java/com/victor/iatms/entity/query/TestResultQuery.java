package com.victor.iatms.entity.query;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试结果查询参数
 */
@Data
public class TestResultQuery {

    /**
     * 任务类型过滤
     */
    private String taskType;

    /**
     * 根据task_type关联的ID
     */
    private Integer refId;

    /**
     * 执行状态过滤
     */
    private String status;

    /**
     * 执行环境过滤
     */
    private String environment;

    /**
     * 优先级过滤（支持多个，逗号分隔）
     */
    private String priority;

    /**
     * 严重程度过滤
     */
    private String severity;

    /**
     * 开始时间范围查询（起始）
     */
    private LocalDateTime startTimeBegin;

    /**
     * 开始时间范围查询（结束）
     */
    private LocalDateTime startTimeEnd;

    /**
     * 最小执行时长（毫秒）
     */
    private Long durationMin;

    /**
     * 最大执行时长（毫秒）
     */
    private Long durationMax;

    /**
     * 关键字搜索（用例名称、失败信息等）
     */
    private String searchKeyword;

    /**
     * 排序字段
     */
    private String sortBy;

    /**
     * 排序顺序
     */
    private String sortOrder;

    /**
     * 页码，默认为1
     */
    private Integer page = 1;

    /**
     * 每页条数，默认为20，最大100
     */
    private Integer pageSize = 20;
    
    /**
     * 分页偏移量
     */
    private Integer offset;
}

