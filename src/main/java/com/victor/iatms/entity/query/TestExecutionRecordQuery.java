package com.victor.iatms.entity.query;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试执行记录查询参数
 */
@Data
public class TestExecutionRecordQuery {
    
    /**
     * 执行范围类型（api, module, project, test_suite, test_case）
     */
    private String executionScope;
    
    /**
     * 关联ID
     */
    private Integer refId;
    
    /**
     * 执行人ID
     */
    private Integer executedBy;
    
    /**
     * 执行类型（manual, scheduled, triggered）
     */
    private String executionType;
    
    /**
     * 测试环境
     */
    private String environment;
    
    /**
     * 执行状态（running, completed, failed, cancelled）
     */
    private String status;
    
    /**
     * 开始时间-起始
     */
    private LocalDateTime startTimeBegin;
    
    /**
     * 开始时间-结束
     */
    private LocalDateTime startTimeEnd;
    
    /**
     * 结束时间-起始
     */
    private LocalDateTime endTimeBegin;
    
    /**
     * 结束时间-结束
     */
    private LocalDateTime endTimeEnd;
    
    /**
     * 执行耗时最小值（秒）
     */
    private Integer durationMin;
    
    /**
     * 执行耗时最大值（秒）
     */
    private Integer durationMax;
    
    /**
     * 成功率最小值
     */
    private Double successRateMin;
    
    /**
     * 成功率最大值
     */
    private Double successRateMax;
    
    /**
     * 关键字搜索（范围名称）
     */
    private String searchKeyword;
    
    /**
     * 浏览器类型
     */
    private String browser;
    
    /**
     * 应用版本
     */
    private String appVersion;
    
    /**
     * 触发任务ID
     */
    private Long triggeredTaskId;
    
    /**
     * 排序字段
     */
    private String sortBy;
    
    /**
     * 排序顺序（asc/desc）
     */
    private String sortOrder;
    
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页条数
     */
    private Integer pageSize = 10;
    
    /**
     * 是否包含已删除
     */
    private Boolean includeDeleted = false;
}

