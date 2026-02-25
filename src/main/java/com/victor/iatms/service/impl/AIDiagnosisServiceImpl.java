package com.victor.iatms.service.impl;

import com.victor.iatms.entity.dto.AIDiagnosisRequestDTO;
import com.victor.iatms.entity.dto.AIDiagnosisResponseDTO;
import com.victor.iatms.service.AIDiagnosisService;
import com.victor.iatms.utils.DeepSeekUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AI诊断服务实现类
 */
@Slf4j
@Service
public class AIDiagnosisServiceImpl implements AIDiagnosisService {

    @Autowired
    private DeepSeekUtils deepSeekUtils;

    @Value("${deepseek.diagnosis.enabled:true}")
    private boolean diagnosisEnabled;

    /**
     * 测试失败诊断的系统提示词
     */
    private static final String TEST_FAILURE_PROMPT = """ 
        你是一个专业的测试工程师和API测试专家。你的任务是根据提供的测试失败信息，分析并诊断问题原因。
        
        请根据以下测试失败信息，进行分析：
        1. 错误信息分析
        2. 可能的原因分析
        3. 建议的解决方案
        
        请按以下JSON格式返回诊断结果：
        {
            "result": "总体诊断结果描述",
            "rootCause": "根本原因分析",
            "suggestedFix": "建议的修复方案",
            "possibleCauses": ["可能原因1", "可能原因2", "可能原因3"],
            "improvementSuggestions": ["改进建议1", "改进建议2"],
            "confidenceScore": 85
        }
        
        请确保返回有效的JSON格式，不要包含其他内容。
        """;

    /**
     * 性能问题诊断的系统提示词
     */
    private static final String PERFORMANCE_PROMPT = """
        你是一个专业的性能测试工程师。你的任务是根据提供的性能数据，分析并诊断性能问题。
        
        请根据以下性能数据，进行分析：
        1. 性能瓶颈分析
        2. 可能的原因分析
        3. 优化建议
        
        请按以下JSON格式返回诊断结果：
        {
            "result": "总体性能诊断结果",
            "rootCause": "性能瓶颈根本原因",
            "suggestedFix": "优化建议",
            "possibleCauses": ["原因1", "原因2"],
            "improvementSuggestions": ["优化建议1", "优化建议2"],
            "confidenceScore": 80
        }
        
        请确保返回有效的JSON格式，不要包含其他内容。
        """;

    /**
     * 错误日志诊断的系统提示词
     */
    private static final String ERROR_LOG_PROMPT = """
        你是一个专业的系统运维工程师和调试专家。你的任务是根据提供的错误日志，分析并诊断系统问题。
        
        请根据以下错误日志，进行分析：
        1. 错误类型识别
        2. 问题定位
        3. 解决方案
        
        请按以下JSON格式返回诊断结果：
        {
            "result": "总体诊断结果",
            "rootCause": "问题根本原因",
            "suggestedFix": "解决方案",
            "possibleCauses": ["原因1", "原因2"],
            "improvementSuggestions": ["建议1", "建议2"],
            "confidenceScore": 90
        }
        
        请确保返回有效的JSON格式，不要包含其他内容。
        """;

    @Override
    public AIDiagnosisResponseDTO diagnose(AIDiagnosisRequestDTO request) {
        if (!diagnosisEnabled) {
            return buildErrorResponse("AI诊断功能未启用");
        }

        String diagnosisType = request.getDiagnosisType();
        if (diagnosisType == null) {
            diagnosisType = "test_failure";
        }

        return switch (diagnosisType) {
            case "test_failure" -> diagnoseTestFailure(
                    request.getErrorMessage(),
                    request.getErrorLog(),
                    request.getDescription()
            );
            case "performance" -> diagnosePerformance(
                    request.getContext() != null ? request.getContext().toString() : null,
                    request.getDescription()
            );
            case "error_log" -> diagnoseErrorLog(
                    request.getErrorLog(),
                    request.getDescription()
            );
            default -> diagnoseTestFailure(
                    request.getErrorMessage(),
                    request.getErrorLog(),
                    request.getDescription()
            );
        };
    }

    @Override
    public AIDiagnosisResponseDTO diagnoseTestFailure(String errorMessage, String errorLog, String description) {
        if (!diagnosisEnabled) {
            return buildErrorResponse("AI诊断功能未启用");
        }

        try {
            StringBuilder userMessage = new StringBuilder();
            userMessage.append("【错误信息】\n").append(errorMessage != null ? errorMessage : "无");
            userMessage.append("\n\n【错误日志】\n").append(errorLog != null ? errorLog : "无");
            if (description != null && !description.isEmpty()) {
                userMessage.append("\n\n【问题描述】\n").append(description);
            }

            String aiResponse = deepSeekUtils.chat(TEST_FAILURE_PROMPT, userMessage.toString());

            if (aiResponse == null || aiResponse.isEmpty()) {
                return buildErrorResponse("AI诊断服务调用失败");
            }

            return parseAIResponse(aiResponse, "test_failure");

        } catch (Exception e) {
            log.error("测试失败诊断异常: ", e);
            return buildErrorResponse("诊断过程发生异常: " + e.getMessage());
        }
    }

    @Override
    public AIDiagnosisResponseDTO diagnosePerformance(String performanceData, String description) {
        if (!diagnosisEnabled) {
            return buildErrorResponse("AI诊断功能未启用");
        }

        try {
            StringBuilder userMessage = new StringBuilder();
            userMessage.append("【性能数据】\n").append(performanceData != null ? performanceData : "无");
            if (description != null && !description.isEmpty()) {
                userMessage.append("\n\n【问题描述】\n").append(description);
            }

            String aiResponse = deepSeekUtils.chat(PERFORMANCE_PROMPT, userMessage.toString());

            if (aiResponse == null || aiResponse.isEmpty()) {
                return buildErrorResponse("AI诊断服务调用失败");
            }

            return parseAIResponse(aiResponse, "performance");

        } catch (Exception e) {
            log.error("性能问题诊断异常: ", e);
            return buildErrorResponse("诊断过程发生异常: " + e.getMessage());
        }
    }

    @Override
    public AIDiagnosisResponseDTO diagnoseErrorLog(String errorLog, String description) {
        if (!diagnosisEnabled) {
            return buildErrorResponse("AI诊断功能未启用");
        }

        try {
            StringBuilder userMessage = new StringBuilder();
            userMessage.append("【错误日志】\n").append(errorLog != null ? errorLog : "无");
            if (description != null && !description.isEmpty()) {
                userMessage.append("\n\n【问题描述】\n").append(description);
            }

            String aiResponse = deepSeekUtils.chat(ERROR_LOG_PROMPT, userMessage.toString());

            if (aiResponse == null || aiResponse.isEmpty()) {
                return buildErrorResponse("AI诊断服务调用失败");
            }

            return parseAIResponse(aiResponse, "error_log");

        } catch (Exception e) {
            log.error("错误日志诊断异常: ", e);
            return buildErrorResponse("诊断过程发生异常: " + e.getMessage());
        }
    }

    @Override
    public boolean isAvailable() {
        return diagnosisEnabled && deepSeekUtils != null;
    }

    private AIDiagnosisResponseDTO parseAIResponse(String aiResponse, String diagnosisType) {
        AIDiagnosisResponseDTO response = new AIDiagnosisResponseDTO();
        response.setDiagnosisId(UUID.randomUUID().toString());
        response.setDiagnosisType(diagnosisType);
        response.setStatus("success");

        try {
            String jsonStr = extractJson(aiResponse);
            if (jsonStr != null) {
                com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSON.parseObject(jsonStr);
                response.setResult(json.getString("result"));
                response.setRootCause(json.getString("rootCause"));
                response.setSuggestedFix(json.getString("suggestedFix"));
                
                if (json.containsKey("possibleCauses")) {
                    response.setPossibleCauses(json.getJSONArray("possibleCauses").toJavaList(String.class));
                }
                if (json.containsKey("improvementSuggestions")) {
                    response.setImprovementSuggestions(json.getJSONArray("improvementSuggestions").toJavaList(String.class));
                }
                
                response.setConfidenceScore(json.getInteger("confidenceScore"));
            } else {
                response.setResult(aiResponse);
                response.setConfidenceScore(50);
            }
        } catch (Exception e) {
            log.warn("解析AI响应JSON失败，使用原始文本: ", e);
            response.setResult(aiResponse);
            response.setConfidenceScore(50);
        }

        return response;
    }

    private String extractJson(String response) {
        if (response == null) return null;
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return response.substring(start, end + 1);
        }
        return null;
    }

    private AIDiagnosisResponseDTO buildErrorResponse(String errorMessage) {
        AIDiagnosisResponseDTO response = new AIDiagnosisResponseDTO();
        response.setDiagnosisId(UUID.randomUUID().toString());
        response.setStatus("failed");
        response.setErrorMessage(errorMessage);
        response.setConfidenceScore(0);
        return response;
    }
}

