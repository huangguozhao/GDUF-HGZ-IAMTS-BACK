package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 项目关联数据检查DTO
 */
@Data
public class ProjectRelationCheckDTO {
    
    /**
     * 是否存在关联模块
     */
    private Boolean hasModules;
    
    /**
     * 关联模块数量
     */
    private Integer modulesCount;
    
    /**
     * 是否存在关联接口
     */
    private Boolean hasApis;
    
    /**
     * 关联接口数量
     */
    private Integer apisCount;
    
    /**
     * 是否存在关联用例
     */
    private Boolean hasTestCases;
    
    /**
     * 关联用例数量
     */
    private Integer testCasesCount;
    
    /**
     * 是否存在关联测试报告
     */
    private Boolean hasTestReports;
    
    /**
     * 关联测试报告数量
     */
    private Integer testReportsCount;
    
    /**
     * 是否可以删除（无关联数据）
     */
    private Boolean canDelete;
    
    /**
     * 需要级联删除的数据类型
     */
    private String[] cascadeDeleteTypes;
}
