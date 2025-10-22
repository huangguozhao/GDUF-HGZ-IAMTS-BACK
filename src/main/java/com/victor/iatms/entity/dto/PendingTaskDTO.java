package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 待办事项DTO
 */
@Data
public class PendingTaskDTO {

    /**
     * 任务ID
     */
    @JsonProperty("task_id")
    private Long taskId;

    /**
     * 任务类型
     */
    private String type;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 截止日期
     */
    @JsonProperty("due_date")
    private String dueDate;

    /**
     * 分配人
     */
    private String assigner;

    /**
     * 进度百分比
     */
    private Integer progress;
}




