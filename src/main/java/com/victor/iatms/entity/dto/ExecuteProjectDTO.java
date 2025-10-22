package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 执行项目测试请求DTO
 */
@Data
public class ExecuteProjectDTO {
    private String environment;
    private String executionType = "manual"; // 执行类型：manual, scheduled, triggered
    private String baseUrl;
    private Integer timeout;
    private JsonNode authOverride; // 全局认证信息覆盖配置
    private Map<String, String> variables; // 全局执行变量
    private Boolean async = true; // 是否异步执行，默认true
    private String callbackUrl;
    private Integer concurrency; // 并发执行数
    private String executionStrategy; // 执行策略：sequential, by_module, by_priority
    private ModuleFilter moduleFilter; // 模块过滤条件
    private CaseFilter caseFilter; // 用例过滤条件
    private ReportConfig reportConfig; // 报告配置

    @Data
    public static class ModuleFilter {
        private List<Integer> moduleIds; // 指定要执行的模块ID列表
        private String status; // 模块状态过滤
    }

    @Data
    public static class CaseFilter {
        private List<String> priority; // 优先级过滤
        private List<String> tags; // 标签过滤
        private Boolean enabledOnly = true; // 是否只执行启用的用例，默认true
    }

    @Data
    public static class ReportConfig {
        private Boolean detailed = true; // 是否生成详细报告，默认true
        private Boolean includeLogs = false; // 是否包含执行日志，默认false
    }
}
