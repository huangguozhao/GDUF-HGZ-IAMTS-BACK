package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 模块统计数据DTO
 */
@Data
public class ModuleStatisticsDTO {
    
    /**
     * 模块ID
     */
    private Integer moduleId;
    
    /**
     * 模块名称
     */
    private String moduleName;
    
    /**
     * 模块编码
     */
    private String moduleCode;
    
    /**
     * 接口总数
     */
    private Integer apiCount;
    
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
     * 子模块总数
     */
    private Integer childModuleCount;
    
    /**
     * 测试执行记录总数
     */
    private Integer executionRecordCount;
    
    /**
     * 用例通过率（百分比，保留2位小数）
     */
    private Double passRate;
    
    /**
     * 最近一次执行时间
     */
    private String lastExecutionTime;
    
    /**
     * 模块状态
     */
    private String status;
    
    /**
     * 模块创建时间
     */
    private String createdAt;
    
    /**
     * 模块最后更新时间
     */
    private String updatedAt;
}


