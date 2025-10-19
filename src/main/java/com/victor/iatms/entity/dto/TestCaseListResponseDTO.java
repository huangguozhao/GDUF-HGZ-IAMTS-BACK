package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页获取测试用例列表响应DTO
 */
@Data
public class TestCaseListResponseDTO {

    /**
     * 符合条件的数据总条数
     */
    private Long total;

    /**
     * 当前页的用例数据列表
     */
    private List<TestCaseItemDTO> items;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 当前每页条数
     */
    private Integer pageSize;

    /**
     * 用例统计摘要
     */
    private TestCaseSummaryDTO summary;
}
