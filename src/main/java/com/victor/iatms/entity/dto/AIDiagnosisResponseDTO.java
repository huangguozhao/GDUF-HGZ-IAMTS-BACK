package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * AI诊断响应DTO
 */
@Data
public class AIDiagnosisResponseDTO {

    /**
     * 诊断ID
     */
    @JsonProperty("diagnosis_id")
    private String diagnosisId;

    /**
     * 诊断类型
     */
    @JsonProperty("diagnosis_type")
    private String diagnosisType;

    /**
     * 诊断结果
     */
    private String result;

    /**
     * 问题原因分析
     */
    @JsonProperty("root_cause")
    private String rootCause;

    /**
     * 建议的解决方案
     */
    @JsonProperty("suggested_fix")
    private String suggestedFix;

    /**
     * 相关代码片段
     */
    @JsonProperty("related_code")
    private List<String> relatedCode;

    /**
     * 可能的根本原因列表
     */
    @JsonProperty("possible_causes")
    private List<String> possibleCauses;

    /**
     * 改进建议
     */
    @JsonProperty("improvement_suggestions")
    private List<String> improvementSuggestions;

    /**
     * 相似问题参考
     */
    @JsonProperty("similar_issues")
    private List<Map<String, String>> similarIssues;

    /**
     * 诊断置信度 (0-100)
     */
    @JsonProperty("confidence_score")
    private Integer confidenceScore;

    /**
     * 额外数据
     */
    private Map<String, Object> metadata;

    /**
     * 状态: success, failed, partial
     */
    private String status;

    /**
     * 错误信息(如果有)
     */
    @JsonProperty("error_message")
    private String errorMessage;
}

