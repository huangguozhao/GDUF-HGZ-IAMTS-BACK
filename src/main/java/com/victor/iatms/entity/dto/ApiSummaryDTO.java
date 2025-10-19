package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.Map;

/**
 * 接口统计摘要DTO
 */
@Data
public class ApiSummaryDTO {

    /**
     * 总接口数
     */
    private Integer totalApis;

    /**
     * 按请求方法统计
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
}
