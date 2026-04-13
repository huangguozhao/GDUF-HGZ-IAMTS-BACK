package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 项目结构响应DTO
 */
@Data
public class ProjectStructureDTO {
    
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
     * 项目状态
     */
    private String status;
    
    /**
     * 项目统计信息
     */
    private ProjectStatistics statistics;
    
    /**
     * 模块列表（树形结构）
     */
    private List<ModuleTreeDTO> modules;
    
    /**
     * 项目统计信息内部类
     */
    @Data
    public static class ProjectStatistics {
        /**
         * 模块数量
         */
        private Integer moduleCount;
        
        /**
         * 接口数量
         */
        private Integer apiCount;
        
        /**
         * 用例总数
         */
        private Integer testCaseCount;
        
        /**
         * 通过数量
         */
        private Integer passedCount;
        
        /**
         * 失败数量
         */
        private Integer failedCount;
        
        /**
         * 未执行数量
         */
        private Integer notExecutedCount;
    }
}
