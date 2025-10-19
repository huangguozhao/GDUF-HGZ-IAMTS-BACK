package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 接口列表响应DTO
 */
@Data
public class ApiListResponseDTO {

    /**
     * 符合条件的数据总条数
     */
    private Integer total;

    /**
     * 当前页的接口数据列表
     */
    private List<ApiDTO> items;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 当前每页条数
     */
    private Integer pageSize;

    /**
     * 接口统计摘要
     */
    private ApiSummaryDTO summary;
}
