package com.victor.iatms.controller;

import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.AIDiagnosisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
            
            // 提取诊断所需参数
            String failureMessage = (String) diagnosisRequest.getOrDefault("failureMessage", "");
            String failureType = (String) diagnosisRequest.getOrDefault("failureType", "");
            Integer responseStatus = diagnosisRequest.get("responseStatus") != null ? 
                Integer.parseInt(String.valueOf(diagnosisRequest.get("responseStatus"))) : null;
            String responseBody = (String) diagnosisRequest.getOrDefault("responseBody", "");
            String apiPath = (String) diagnosisRequest.getOrDefault("apiPath", "");
            String apiMethod = (String) diagnosisRequest.getOrDefault("apiMethod", "");
            String caseName = (String) diagnosisRequest.getOrDefault("caseName", "");
            
            // 执行AI诊断
            Map<String, Object> diagnosisResult = aiDiagnosisService.diagnose(
                failureMessage, 
                failureType, 
                responseStatus, 
                responseBody,
                apiPath,
                apiMethod,
                caseName
            );
            
            return ResponseVO.success(diagnosisResult);
        } catch (Exception e) {
            log.error("AI诊断执行失败: {}", e.getMessage(), e);
            return ResponseVO.serverError("AI诊断执行失败: " + e.getMessage());
        }
    }
}
