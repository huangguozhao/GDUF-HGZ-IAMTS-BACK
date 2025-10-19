package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 项目删除结果DTO
 */
@Data
public class ProjectDeleteResultDTO {
    
    /**
     * 是否删除成功
     */
    private Boolean success;
    
    /**
     * 删除消息
     */
    private String message;
    
    /**
     * 级联删除的模块数量
     */
    private Integer deletedModulesCount;
    
    /**
     * 级联删除的接口数量
     */
    private Integer deletedApisCount;
    
    /**
     * 级联删除的用例数量
     */
    private Integer deletedTestCasesCount;
    
    /**
     * 错误代码
     */
    private String errorCode;
}
