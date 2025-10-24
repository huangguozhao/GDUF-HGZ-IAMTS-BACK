package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 接口列表响应DTO
 */
@Data
public class ApiListResponseDTO {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer totalPages;

    /**
     * 接口列表
     */
    private List<ApiDTO> items;

    /**
     * 统计信息（可选）
     */
    private ApiStatisticsDTO statistics;

    /**
     * 接口摘要信息（可选）
     */
    private Object summary;
}
