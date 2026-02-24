package com.victor.iatms.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 定时任务查询参数DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTaskQueryDTO {

    // 分页参数
    private Integer page;
    private Integer pageSize;

    // 查询条件
    private String taskName;
    private String taskType; // single_case, module, project, test_suite, api
    private Integer targetId;
    private String triggerType;
    private Boolean isEnabled;
    private String executionEnvironment;
    private Integer createdBy;

    // 排序
    private String sortBy;
    private String sortOrder;
}

