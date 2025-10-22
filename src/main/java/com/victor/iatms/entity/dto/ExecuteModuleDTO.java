package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 执行模块测试请求DTO
 */
@Data
public class ExecuteModuleDTO {
    
    /**
     * 执行环境标识（如: dev, test, prod）
     */
    private String environment;
    
    /**
     * 执行类型：manual（手动）, scheduled（定时）, triggered（触发）
     */
    private String executionType = "manual";
    
    /**
     * 覆盖所有接口的基础URL
     */
    private String baseUrl;
    
    /**
     * 全局超时时间（秒）
     */
    private Integer timeout;
    
    /**
     * 全局认证信息覆盖配置
     */
    private Map<String, Object> authOverride;
    
    /**
     * 全局执行变量
     */
    private Map<String, Object> variables;
    
    /**
     * 是否异步执行，默认: true
     */
    private Boolean async = true;
    
    /**
     * 异步执行完成后的回调URL
     */
    private String callbackUrl;
    
    /**
     * 并发执行数，默认: 5
     */
    private Integer concurrency = 5;
    
    /**
     * 用例过滤条件
     */
    private CaseFilter caseFilter;
    
    /**
     * 用例过滤条件内部类
     */
    @Data
    public static class CaseFilter {
        /**
         * 优先级过滤，如: ["P0", "P1"]
         */
        private List<String> priority;
        
        /**
         * 标签过滤，如: ["冒烟测试"]
         */
        private List<String> tags;
        
        /**
         * 是否只执行启用的用例，默认: true
         */
        private Boolean enabledOnly = true;
    }
}
