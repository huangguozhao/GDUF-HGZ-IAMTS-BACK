package com.victor.iatms.controller;

import com.victor.iatms.entity.po.TestCaseResult;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.mappers.TestExecutionMapper;
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

    @Autowired
    private TestExecutionMapper testExecutionMapper;

    /**
     * 执行AI诊断
     * @param diagnosisRequest 诊断请求参数
     * @return 诊断结果
     */
    @PostMapping("/execute")
    public ResponseVO<Map<String, Object>> executeDiagnosis(@RequestBody Map<String, Object> diagnosisRequest) {
        try {
            log.info("AI诊断请求: {}", diagnosisRequest);
            
            // 检查是否有executionId（批量测试诊断）
            Long executionId = null;
            if (diagnosisRequest.get("executionId") != null) {
                try {
                    executionId = Long.parseLong(String.valueOf(diagnosisRequest.get("executionId")));
                } catch (NumberFormatException e) {
                    log.warn("executionId格式错误: {}", diagnosisRequest.get("executionId"));
                }
            }
            
            // 如果有executionId，获取所有测试用例结果
            List<TestCaseResult> allCaseResults = new ArrayList<>();
            if (executionId != null) {
                allCaseResults = testExecutionMapper.findTestCaseResultsByExecutionId(executionId);
                log.info("获取到{}条测试用例结果", allCaseResults.size());
            }
            
            // 提取单个测试用例的诊断参数（用于兼容旧的调用方式）
            String failureMessage = (String) diagnosisRequest.getOrDefault("failureMessage", "");
            String failureType = (String) diagnosisRequest.getOrDefault("failureType", "");
            Integer responseStatus = diagnosisRequest.get("responseStatus") != null ? 
                Integer.parseInt(String.valueOf(diagnosisRequest.get("responseStatus"))) : null;
            String responseBody = (String) diagnosisRequest.getOrDefault("responseBody", "");
            String apiPath = (String) diagnosisRequest.getOrDefault("apiPath", "");
            String apiMethod = (String) diagnosisRequest.getOrDefault("apiMethod", "");
            String caseName = (String) diagnosisRequest.getOrDefault("caseName", "");
            
            // 执行AI诊断（传入所有测试用例结果）
            Map<String, Object> diagnosisResult = aiDiagnosisService.diagnose(
                failureMessage, 
                failureType, 
                responseStatus, 
                responseBody,
                apiPath,
                apiMethod,
                caseName,
                allCaseResults  // 新增参数：所有测试用例结果
            );
            
            return ResponseVO.success(diagnosisResult);
        } catch (Exception e) {
            log.error("AI诊断执行失败: {}", e.getMessage(), e);
            return ResponseVO.serverError("AI诊断执行失败: " + e.getMessage());
        }
    }
}
