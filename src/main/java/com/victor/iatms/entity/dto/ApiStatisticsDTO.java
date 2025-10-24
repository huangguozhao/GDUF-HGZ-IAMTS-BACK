package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.Map;

/**
 * 接口统计信息DTO
 */
@Data
public class ApiStatisticsDTO {

    /**
     * 总接口数
     */
    private Integer totalApis;

    /**
     * 按HTTP方法统计
     */
    private Map<String, Integer> byMethod;

    /**
     * 按状态统计
     */
    private Map<String, Integer> byStatus;

    /**
     * 按认证类型统计
     */
    private Map<String, Integer> byAuthType;

    /**
     * 有测试用例的接口数
     */
    private Integer apisWithTestCases;

    /**
     * 无测试用例的接口数
     */
    private Integer apisWithoutTestCases;
}

