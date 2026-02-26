package com.victor.iatms.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 任务实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("Tasks")
public class Task {

    @TableId(type = IdType.AUTO)
    private Long taskId;

    /**
     * 任务类型: test_case, bug_fix, api_test, performance, manual
     */
    private String taskType;

    /**
     * 任务标题
     */
    private String taskTitle;

    /**
     * 任务描述
     */
    private String taskDescription;

    /**
     * 优先级: 1-紧急, 2-高, 3-中, 4-低
     */
    private Integer priority;

    /**
     * 严重程度: critical, high, medium, low
     */
    private String severity;

    /**
     * 状态: pending, in_progress, completed, cancelled
     */
    private String status;

    /**
     * 进度百分比: 0-100
     */
    private Integer progress;

    /**
     * 截止日期
     */
    private LocalDateTime dueDate;

    /**
     * 开始日期
     */
    private LocalDateTime startDate;

    /**
     * 完成日期
     */
    private LocalDateTime completedDate;

    /**
     * 被分配人ID
     */
    private Integer assigneeId;

    /**
     * 被分配人姓名
     */
    private String assigneeName;

    /**
     * 分配人ID
     */
    private Integer assignerId;

    /**
     * 分配人姓名
     */
    private String assignerName;

    /**
     * 关联项目ID
     */
    private Integer projectId;

    /**
     * 关联项目名称
     */
    private String projectName;

    /**
     * 关联测试用例ID
     */
    private Integer testCaseId;

    /**
     * 关联执行记录ID
     */
    private Long executionId;

    /**
     * 关联报告ID
     */
    private Long reportId;

    /**
     * 标签, 多个用逗号分隔
     */
    private String tags;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 是否删除
     */
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 创建人ID
     */
    private Integer createdBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 更新人ID
     */
    private Integer updatedBy;

    /**
     * 删除时间
     */
    private LocalDateTime deletedAt;

    /**
     * 删除人ID
     */
    private Integer deletedBy;
}

