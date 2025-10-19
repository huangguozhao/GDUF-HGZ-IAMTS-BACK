package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 测试结果分页结果DTO（带统计摘要）
 */
@Data
public class TestResultPageResultDTO {

    /**
     * 符合条件的数据总条数
     */
    private Long total;

    /**
     * 当前页的测试结果数据列表
     */
    private List<TestResultDTO> items;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 当前每页条数
     */
    @JsonProperty("page_size")
    private Integer pageSize;

    /**
     * 结果统计摘要
     */
    private TestResultSummaryDTO summary;

    public TestResultPageResultDTO() {
    }

    public TestResultPageResultDTO(Long total, List<TestResultDTO> items, Integer page, Integer pageSize, TestResultSummaryDTO summary) {
        this.total = total;
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.summary = summary;
    }
}

