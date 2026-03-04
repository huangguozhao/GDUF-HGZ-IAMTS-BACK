package com.victor.iatms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.AIDiagnosisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * AI诊断Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/ai-diagnosis")
public class AIDiagnosisController {

    @Autowired
    private AIDiagnosisService aiDiagnosisService;

    /**
     * 执行AI诊断
     * @param diagnosisRequest 诊断请求参数
     * @return 诊断结果
     */
    @PostMapping("/execute")
    public ResponseVO<Map<String, Object>> executeDiagnosis(@RequestBody Map<String, Object> diagnosisRequest) {
        try {
            log.info("AI诊断请求: {}", diagnosisRequest);
            
            String failureMessage = (String) diagnosisRequest.getOrDefault("failureMessage", "");
            String failureType = (String) diagnosisRequest.getOrDefault("failureType", "");
            Integer responseStatus = diagnosisRequest.get("responseStatus") != null ? 
                Integer.parseInt(String.valueOf(diagnosisRequest.get("responseStatus"))) : null;
            
            String responseBody = "";
            Object responseBodyObj = diagnosisRequest.get("responseBody");
            if (responseBodyObj != null) {
                if (responseBodyObj instanceof String) {
                    responseBody = (String) responseBodyObj;
                } else {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        responseBody = mapper.writeValueAsString(responseBodyObj);
                    } catch (Exception e) {
                        log.warn("responseBody转换失败: {}", e.getMessage());
                        responseBody = String.valueOf(responseBodyObj);
                    }
                }
            }
            
            String apiPath = (String) diagnosisRequest.getOrDefault("apiPath", "");
            String apiMethod = (String) diagnosisRequest.getOrDefault("apiMethod", "");
            String caseName = (String) diagnosisRequest.getOrDefault("caseName", "");
            
            Long executionId = null;
            if (diagnosisRequest.get("executionId") != null) {
                try {
                    executionId = Long.parseLong(String.valueOf(diagnosisRequest.get("executionId")));
                } catch (NumberFormatException e) {
                    log.warn("executionId格式错误: {}", diagnosisRequest.get("executionId"));
                }
            }
            
            Map<String, Object> diagnosisResult = aiDiagnosisService.diagnose(
                failureMessage, 
                failureType, 
                responseStatus, 
                responseBody,
                apiPath,
                apiMethod,
                caseName,
                executionId
            );
            
            return ResponseVO.success(diagnosisResult);
        } catch (Exception e) {
            log.error("AI诊断执行失败: {}", e.getMessage(), e);
            return ResponseVO.serverError("AI诊断执行失败: " + e.getMessage());
        }
    }

    /**
     * 获取诊断结果
     * @param diagnosisId 诊断ID
     * @return 诊断结果
     */
    @GetMapping("/result/{diagnosisId}")
    public ResponseVO<Map<String, Object>> getDiagnosisResult(@PathVariable String diagnosisId) {
        try {
            Map<String, Object> result = aiDiagnosisService.getDiagnosisResult(diagnosisId);
            if (result != null) {
                return ResponseVO.success(result);
            } else {
                return ResponseVO.notFound("诊断结果不存在或已过期");
            }
        } catch (Exception e) {
            log.error("获取诊断结果失败: {}", e.getMessage(), e);
            return ResponseVO.serverError("获取诊断结果失败: " + e.getMessage());
        }
    }
}
