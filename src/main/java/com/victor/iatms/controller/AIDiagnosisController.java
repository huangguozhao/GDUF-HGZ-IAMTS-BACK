package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.dto.AIDiagnosisRequestDTO;
import com.victor.iatms.entity.dto.AIDiagnosisResponseDTO;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.AIDiagnosisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * AI诊断控制器
 */
@Slf4j
@RestController
@RequestMapping("/ai-diagnosis")
@Validated
public class AIDiagnosisController {

    @Autowired
    private AIDiagnosisService aiDiagnosisService;

    /**
     * 执行AI诊断
     */
    @PostMapping("/diagnose")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:view", "testcase:execute"}
    )
    public ResponseVO<AIDiagnosisResponseDTO> diagnose(
            @RequestBody @Validated AIDiagnosisRequestDTO request) {
        try {
            log.info("收到AI诊断请求，类型: {}, executionId: {}, description: {}", 
                request.getDiagnosisType(), request.getExecutionId(), request.getDescription());
            AIDiagnosisResponseDTO result = aiDiagnosisService.diagnose(request);
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("AI诊断异常: ", e);
            return ResponseVO.serverError("AI诊断服务异常: " + e.getMessage());
        }
    }

    /**
     * 测试失败诊断
     */
    @PostMapping("/test-failure")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:view", "testcase:execute"}
    )
    public ResponseVO<AIDiagnosisResponseDTO> diagnoseTestFailure(
            @RequestParam String errorMessage,
            @RequestParam(required = false) String errorLog,
            @RequestParam(required = false) String description) {
        try {
            AIDiagnosisResponseDTO result = aiDiagnosisService.diagnoseTestFailure(
                    errorMessage, errorLog, description);
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("测试失败诊断异常: ", e);
            return ResponseVO.serverError("诊断服务异常: " + e.getMessage());
        }
    }

    /**
     * 性能问题诊断
     */
    @PostMapping("/performance")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:view"}
    )
    public ResponseVO<AIDiagnosisResponseDTO> diagnosePerformance(
            @RequestParam String performanceData,
            @RequestParam(required = false) String description) {
        try {
            AIDiagnosisResponseDTO result = aiDiagnosisService.diagnosePerformance(
                    performanceData, description);
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("性能问题诊断异常: ", e);
            return ResponseVO.serverError("诊断服务异常: " + e.getMessage());
        }
    }

    /**
     * 错误日志诊断
     */
    @PostMapping("/error-log")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:view", "testcase:execute"}
    )
    public ResponseVO<AIDiagnosisResponseDTO> diagnoseErrorLog(
            @RequestParam String errorLog,
            @RequestParam(required = false) String description) {
        try {
            AIDiagnosisResponseDTO result = aiDiagnosisService.diagnoseErrorLog(
                    errorLog, description);
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("错误日志诊断异常: ", e);
            return ResponseVO.serverError("诊断服务异常: " + e.getMessage());
        }
    }

    /**
     * 检查AI诊断功能是否可用
     */
    @GetMapping("/status")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Boolean> getStatus() {
        boolean available = aiDiagnosisService.isAvailable();
        return ResponseVO.success(available);
    }
}

