package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 测试步骤DTO
 */
@Data
public class TestStepDTO {

    /**
     * 步骤ID
     */
    @JsonProperty("step_id")
    private Integer stepId;

    /**
     * 步骤名称
     */
    private String name;

    /**
     * 步骤描述
     */
    private String description;

    /**
     * 步骤状态
     */
    private String status;

    /**
     * 步骤耗时
     */
    private Long duration;

    /**
     * 步骤开始时间
     */
    @JsonProperty("start_time")
    private String startTime;

    /**
     * 步骤结束时间
     */
    @JsonProperty("end_time")
    private String endTime;

    /**
     * 步骤参数
     */
    private Map<String, Object> parameters;

    /**
     * 步骤日志
     */
    private List<String> logs;
}




