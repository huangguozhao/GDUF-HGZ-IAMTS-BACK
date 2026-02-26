package com.victor.iatms.service;

import com.victor.iatms.entity.dto.AIDiagnosisRequestDTO;
import com.victor.iatms.entity.dto.AIDiagnosisResponseDTO;

/**
 * AI诊断服务接口
 */
public interface AIDiagnosisService {

    /**
     * 执行AI诊断
     * @param request 诊断请求
     * @return 诊断结果
     */
    AIDiagnosisResponseDTO diagnose(AIDiagnosisRequestDTO request);

    /**
     * 根据执行记录ID执行AI诊断（会自动从数据库查询完整数据）
     * @param executionId 执行记录ID
     * @param userId 用户ID
     * @return 诊断结果
     */
    AIDiagnosisResponseDTO diagnoseByExecutionId(Long executionId, Integer userId);

    /**
     * 测试失败诊断
     * @param errorMessage 错误信息
     * @param errorLog 错误日志
     * @param description 问题描述
     * @return 诊断结果
     */
    AIDiagnosisResponseDTO diagnoseTestFailure(String errorMessage, String errorLog, String description);

    /**
     * 性能问题诊断
     * @param performanceData 性能数据
     * @param description 问题描述
     * @return 诊断结果
     */
    AIDiagnosisResponseDTO diagnosePerformance(String performanceData, String description);

    /**
     * 错误日志诊断
     * @param errorLog 错误日志
     * @param description 问题描述
     * @return 诊断结果
     */
    AIDiagnosisResponseDTO diagnoseErrorLog(String errorLog, String description);

    /**
     * 检查AI诊断功能是否可用
     * @return 是否可用
     */
    boolean isAvailable();
}

