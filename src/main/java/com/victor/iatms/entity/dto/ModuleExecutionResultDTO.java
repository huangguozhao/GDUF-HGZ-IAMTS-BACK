package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.Map;

/**
 * 模块执行结果DTO
 */
@Data
public class ModuleExecutionResultDTO {
    
    /**
     * 任务ID（异步执行时）
     */
    private String taskId;
    
    /**
     * 执行记录ID（同步执行时）
     */
    private Long executionId;
    
    /**
     * 模块ID
     */
    private Integer moduleId;
    
    /**
     * 模块名称
     */
    private String moduleName;
    
    /**
     * 总用例数
     */
    private Integer totalCases;
    
    /**
     * 过滤后的用例数
     */
    private Integer filteredCases;
    
    /**
     * 执行状态
     */
    private String status;
    
    /**
     * 并发执行数
     */
    private Integer concurrency;
    
    /**
     * 预估执行时间（秒）
     */
    private Integer estimatedDuration;
    
    /**
     * 队列位置（异步执行时）
     */
    private Integer queuePosition;
    
    /**
     * 监控URL
     */
    private String monitorUrl;
    
    /**
     * 报告URL
     */
    private String reportUrl;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
    
    /**
     * 总耗时（毫秒）
     */
    private Long totalDuration;
    
    /**
     * 通过用例数
     */
    private Integer passed;
    
    /**
     * 失败用例数
     */
    private Integer failed;
    
    /**
     * 跳过用例数
     */
    private Integer skipped;
    
    /**
     * 中断用例数
     */
    private Integer broken;
    
    /**
     * 成功率
     */
    private Double successRate;
    
    /**
     * 详细统计信息
     */
    private ExecutionDetails details;
    
    /**
     * 报告ID
     */
    private Long reportId;
    
    /**
     * 汇总URL
     */
    private String summaryUrl;
    
    /**
     * 执行详情内部类
     */
    @Data
    public static class ExecutionDetails {
        /**
         * 按优先级统计
         */
        private Map<String, PriorityStats> byPriority;
        
        /**
         * 按接口统计
         */
        private Map<String, ApiStats> byApi;
        
        /**
         * 优先级统计内部类
         */
        @Data
        public static class PriorityStats {
            private Integer total;
            private Integer passed;
            private Integer failed;
            private Integer skipped;
            private Integer broken;
        }
        
        /**
         * 接口统计内部类
         */
        @Data
        public static class ApiStats {
            private Integer total;
            private Integer passed;
            private Integer failed;
            private Integer skipped;
            private Integer broken;
        }
    }
}
