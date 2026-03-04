package com.victor.iatms.service;

import com.victor.iatms.entity.po.TestCaseResult;
import java.util.List;
import java.util.Map;

/**
 * AI诊断Service接口
 */
public interface AIDiagnosisService {
    
    /**
     * 执行AI诊断（单个测试用例）
     * @param failureMessage 失败消息
     * @param failureType 失败类型
     * @param responseStatus HTTP响应状态码
     * @param responseBody 响应体
     * @param apiPath API路径
     * @param apiMethod API方法
     * @param caseName 用例名称
     * @return 诊断结果
     */
    Map<String, Object> diagnose(String failureMessage, String failureType, Integer responseStatus, 
                                  String responseBody, String apiPath, String apiMethod, String caseName);

    /**
     * 执行AI诊断（支持批量测试用例）
     * @param failureMessage 失败消息
     * @param failureType 失败类型
     * @param responseStatus HTTP响应状态码
     * @param responseBody 响应体
     * @param apiPath API路径
     * @param apiMethod API方法
     * @param caseName 用例名称
     * @param executionId 执行记录ID（用于批量诊断）
     * @return 诊断结果
     */
    Map<String, Object> diagnose(String failureMessage, String failureType, Integer responseStatus,
                                  String responseBody, String apiPath, String apiMethod, String caseName,
                                  Long executionId);

    /**
     * 获取诊断结果
     * @param diagnosisId 诊断ID
     * @return 诊断结果
     */
    Map<String, Object> getDiagnosisResult(String diagnosisId);
}
