package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 分组数据DTO
 */
@Data
public class GroupDataDTO {

    /**
     * 分组键
     */
    @JsonProperty("group_key")
    private String groupKey;

    /**
     * 分组名称
     */
    @JsonProperty("group_name")
    private String groupName;

    /**
     * 总执行数
     */
    private Long total;

    /**
     * 通过数
     */
    private Long passed;

    /**
     * 失败数
     */
    private Long failed;

    /**
     * 异常数
     */
    private Long broken;

    /**
     * 跳过数
     */
    private Long skipped;

    /**
     * 成功率
     */
    @JsonProperty("success_rate")
    private BigDecimal successRate;

    /**
     * 平均时长
     */
    @JsonProperty("avg_duration")
    private Long avgDuration;
}

