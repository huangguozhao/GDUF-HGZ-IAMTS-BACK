package com.victor.iatms.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @description: 分页结果VO
 * @author: victor
 * @date: 2025-12-07
 */
@Data
public class PaginationResultVO<T> {

    /**
     * 符合条件的数据总条数
     */
    private long total;

    /**
     * 当前页的数据列表
     */
    private List<T> items;

    /**
     * 当前页码
     */
    private int page;

    /**
     * 当前每页条数
     */
    private int pageSize;

    public PaginationResultVO(long total, List<T> items, int page, int pageSize) {
        this.total = total;
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
    }
    
    public PaginationResultVO() {
    }
}


