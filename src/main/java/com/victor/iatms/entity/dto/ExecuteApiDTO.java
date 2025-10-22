package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 执行接口测试请求DTO
 */
@Data
public class ExecuteApiDTO {
    private String environment;
    private String executionType = "manual"; // 执行类型：manual, scheduled, triggered
    private String baseUrl;
    private Integer timeout;
    private JsonNode authOverride; // 认证信息覆盖配置
    private Map<String, String> variables; // 执行变量，用于参数化测试
    private Boolean async = false; // 是否异步执行，默认false
    private String callbackUrl;
    private Integer concurrency; // 并发执行数
    private CaseFilter caseFilter; // 用例过滤条件
    private String executionOrder; // 执行顺序

    @Data
    public static class CaseFilter {
        private List<String> priority; // 优先级过滤
        private List<String> tags; // 标签过滤
        private Boolean enabledOnly = true; // 是否只执行启用的用例，默认true
    }
}
