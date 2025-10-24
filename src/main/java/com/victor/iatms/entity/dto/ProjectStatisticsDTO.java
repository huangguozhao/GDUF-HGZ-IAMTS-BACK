package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 项目统计数据DTO
 */
@Data
public class ProjectStatisticsDTO {
    
    /**
     * 项目ID
     */
    private Integer projectId;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 项目编码
     */
    private String projectCode;
    
    /**
     * 模块总数
     */
    private Integer moduleCount;
    
    /**
     * 用例总数
     */
    private Integer testCaseCount;
    
    /**
     * 通过的用例数
     */
    private Integer passedCount;
    
    /**
     * 失败的用例数
     */
    private Integer failedCount;
    
    /**
     * 未执行的用例数
     */
    private Integer notExecutedCount;
    
    /**
     * 接口总数
     */
    private Integer apiCount;
    
    /**
     * 测试执行记录总数
     */
    private Integer executionRecordCount;
    
    /**
     * 测试报告总数
     */
    private Integer testReportCount;
    
    /**
     * 项目成员总数
     */
    private Integer memberCount;
    
    /**
     * 用例通过率（百分比，保留2位小数）
     */
    private Double passRate;
    
    /**
     * 最近一次执行时间
     */
    private String lastExecutionTime;
    
    /**
     * 项目创建时间
     */
    private String createdAt;
    
    /**
     * 项目最后更新时间
     */
    private String updatedAt;
}

