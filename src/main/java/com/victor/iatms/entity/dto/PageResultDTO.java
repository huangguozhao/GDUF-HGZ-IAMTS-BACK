package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页结果DTO
 */
@Data
public class PageResultDTO<T> {

    /**
     * 符合条件的数据总条数
     */
    private Long total;

    /**
     * 当前页的数据列表
     */
    private List<T> items;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 当前每页条数
     */
    private Integer pageSize;

    public PageResultDTO() {
    }

    public PageResultDTO(Long total, List<T> items, Integer page, Integer pageSize) {
        this.total = total;
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
    }
}
