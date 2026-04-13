package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.iatms.entity.po.TestCaseResult;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.mappers.TestExecutionMapper;
import com.victor.iatms.service.AIDiagnosisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI诊断Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/ai-diagnosis")
@GlobalInterceptor(checkLogin = true)
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
            
            // 获取批量测试用例结果（用于接口/模块/项目测试的AI诊断）
            List<Map<String, Object>> caseResults = null;
            Object caseResultsObj = diagnosisRequest.get("caseResults");
            if (caseResultsObj instanceof List) {
                try {
                    caseResults = (List<Map<String, Object>>) caseResultsObj;
                    log.info("收到前端传递的批量测试用例结果: {} 条", caseResults.size());
                } catch (Exception e) {
                    log.warn("caseResults转换失败: {}", e.getMessage());
                }
            }
            
            // 如果没有收到 caseResults 或为空，则从数据库查询
            // 优先使用 executionId（执行记录ID），如果没有则尝试用 reportId
            if ((caseResults == null || caseResults.isEmpty())) {
                // 尝试用 executionId 作为执行记录ID查询
                if (executionId != null) {
                    log.info("尝试用executionId={}查询测试用例结果", executionId);
                    try {
                        List<TestCaseResult> testCaseResults = testExecutionMapper.findTestCaseResultsByExecutionId(executionId);
                        if (testCaseResults != null && !testCaseResults.isEmpty()) {
                            caseResults = convertTestCaseResults(testCaseResults);
                            log.info("用executionId查询到测试用例结果: {} 条", caseResults.size());
                        }
                    } catch (Exception e) {
                        log.error("用executionId查询失败: {}", e.getMessage());
                    }
                }
                
                // 如果还是没查到，尝试解析 reportId
                if ((caseResults == null || caseResults.isEmpty()) && diagnosisRequest.get("reportId") != null) {
                    try {
                        Long reportId = Long.parseLong(String.valueOf(diagnosisRequest.get("reportId")));
                        log.info("尝试用reportId={}查询测试用例结果", reportId);
                        List<TestCaseResult> testCaseResults = testExecutionMapper.findTestCaseResultsByReportId(reportId);
                        if (testCaseResults != null && !testCaseResults.isEmpty()) {
                            caseResults = convertTestCaseResults(testCaseResults);
                            log.info("用reportId查询到测试用例结果: {} 条", caseResults.size());
                        }
                    } catch (Exception e) {
                        log.error("用reportId查询失败: {}", e.getMessage());
                    }
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
                executionId,
                caseResults
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
    
    /**
     * 转换 TestCaseResult 列表为 Map 列表
     */
    private List<Map<String, Object>> convertTestCaseResults(List<TestCaseResult> testCaseResults) {
        if (testCaseResults == null) return null;
        return testCaseResults.stream().map(tcr -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("caseId", tcr.getCaseId());
            map.put("caseCode", tcr.getCaseCode());
            map.put("caseName", tcr.getCaseName());
            map.put("status", tcr.getStatus());
            map.put("duration", tcr.getDuration());
            map.put("failureMessage", tcr.getFailureMessage());
            map.put("failureType", tcr.getFailureType());
            return map;
        }).collect(Collectors.toList());
    }
}
