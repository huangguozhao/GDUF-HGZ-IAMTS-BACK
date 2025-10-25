package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 分页获取最近编辑的项目查询DTO
 */
@Data
public class RecentProjectsQueryDTO {

    /**
     * 时间范围。可选: 1d, 7d, 30d，默认: 7d
     */
    private String timeRange;

    /**
     * 是否包含项目统计信息，默认: false
     */
    private Boolean includeStats;

    /**
     * 排序字段。可选: last_accessed, updated_at, created_at，默认: last_accessed
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
     * 分页查询的每页记录数，默认为 10，最大 20
     */
    private Integer pageSize;
}








