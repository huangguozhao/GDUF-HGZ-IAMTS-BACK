package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * 最近活动记录DTO
 */
@Data
public class RecentActivityDTO {

    /**
     * 活动ID
     */
    @JsonProperty("activity_id")
    private Long activityId;

    /**
     * 活动类型
     */
    private String type;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 目标ID
     */
    @JsonProperty("target_id")
    private Integer targetId;

    /**
     * 目标名称
     */
    @JsonProperty("target_name")
    private String targetName;

    /**
     * 活动时间
     */
    private String timestamp;

    /**
     * 活动详情
     */
    private Map<String, Object> details;
}




