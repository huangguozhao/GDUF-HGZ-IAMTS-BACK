package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * AI诊断请求DTO
 */
@Data
public class AIDiagnosisRequestDTO {

    /**
     * 诊断类型: test_failure(测试失败诊断), performance(性能诊断), error_log(错误日志诊断)
     */
    private String diagnosisType;

    /**
     * 相关测试用例ID
     */
    @JsonProperty("test_case_id")
    private Integer testCaseId;

    /**
     * 测试执行记录ID
     */
    @JsonProperty("execution_id")
    private Long executionId;

    /**
     * 错误信息
     */
    @JsonProperty("error_message")
    private String errorMessage;

    /**
     * 错误日志
     */
    @JsonProperty("error_log")
    private String errorLog;

    /**
     * 请求上下文/附加信息
     */
    private Map<String, Object> context;

    /**
     * 是否包含代码上下文
     */
    @JsonProperty("include_code_context")
    private Boolean includeCodeContext;

    /**
     * 额外的问题描述
     */
    private String description;
}

