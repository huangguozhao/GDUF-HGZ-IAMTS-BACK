package com.victor.iatms.entity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 报告列表查询DTO
 */
@Data
public class ReportListQueryDTO {
    
    /**
     * 项目ID过滤
     */
    private Integer projectId;
    
    /**
     * 报告类型过滤：execution, coverage, trend, comparison, custom
     */
    private String reportType;
    
    /**
     * 环境过滤
     */
    private String environment;
    
    /**
     * 报告状态过滤：generating, completed, failed
     */
    private String reportStatus;
    
    /**
     * 文件格式过滤：html, pdf, excel, json, xml
     */
    private String fileFormat;
    
    /**
     * 开始时间范围查询（开始）
     */
    private LocalDateTime startTimeBegin;
    
    /**
     * 开始时间范围查询（结束）
     */
    private LocalDateTime startTimeEnd;
    
    /**
     * 最小成功率过滤（0-100）
     */
    private BigDecimal successRateMin;
    
    /**
     * 最大成功率过滤（0-100）
     */
    private BigDecimal successRateMax;
    
    /**
     * 标签过滤
     */
    private String tags;
    
    /**
     * 关键字搜索（报告名称、描述）
     */
    private String searchKeyword;
    
    /**
     * 排序字段：created_at, start_time, success_rate, duration
     */
    private String sortBy = "created_at";
    
    /**
     * 排序顺序：asc, desc
     */
    private String sortOrder = "desc";
    
    /**
     * 是否包含已删除的报告
     */
    private Boolean includeDeleted = false;
    
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页条数
     */
    private Integer pageSize = 20;
    
    /**
     * 偏移量（用于分页）
     */
    private Integer offset;
}
