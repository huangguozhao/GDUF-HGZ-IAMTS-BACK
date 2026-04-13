package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 模块树形结构DTO
 */
@Data
public class ModuleTreeDTO {
    
    /**
     * 模块ID
     */
    private Integer moduleId;
    
    /**
     * 模块编码
     */
    private String moduleCode;
    
    /**
     * 模块名称
     */
    private String name;
    
    /**
     * 父模块ID
     */
    private Integer parentModuleId;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 模块统计信息
     */
    private ModuleStatistics statistics;
    
    /**
     * 子模块列表
     */
    private List<ModuleTreeDTO> children;
    
    /**
     * 模块统计信息内部类
     */
    @Data
    public static class ModuleStatistics {
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
