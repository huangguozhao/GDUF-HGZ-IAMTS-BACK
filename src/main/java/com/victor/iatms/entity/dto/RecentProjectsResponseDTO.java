package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页获取最近编辑的项目响应DTO
 */
@Data
public class RecentProjectsResponseDTO {

    /**
     * 符合条件的数据总条数
     */
    private Long total;

    /**
     * 当前页的项目数据列表
     */
    private List<RecentProjectItemDTO> items;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 当前每页条数
     */
    private Integer pageSize;

    /**
     * 时间范围信息
     */
    private TimeRangeDTO timeRange;
}








