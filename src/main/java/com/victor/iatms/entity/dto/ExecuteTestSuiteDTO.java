package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 执行测试套件请求DTO
 */
@Data
public class ExecuteTestSuiteDTO {
    private String environment;
    private String executionType = "manual"; // 执行类型：manual, scheduled, triggered
    private String baseUrl;
    private Integer timeout;
    private JsonNode authOverride; // 全局认证信息覆盖配置
    private Map<String, String> variables; // 全局执行变量
    private Boolean async = true; // 是否异步执行，默认true
    private String callbackUrl;
    private Integer concurrency; // 并发执行数
    private String executionStrategy; // 执行策略
    private Boolean stopOnFailure; // 失败时是否停止执行
    private RetryConfig retryConfig; // 重试配置
    private CaseFilter caseFilter; // 用例过滤条件
    private ReportConfig reportConfig; // 报告配置

    @Data
    public static class RetryConfig {
        private Boolean enabled = false; // 是否启用重试，默认false
        private Integer maxAttempts = 3; // 最大重试次数，默认3
        private Integer delayMs = 1000; // 重试延迟时间（毫秒），默认1000
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
        private Boolean includeArtifacts = false; // 是否包含附件和日志，默认false
    }
}
