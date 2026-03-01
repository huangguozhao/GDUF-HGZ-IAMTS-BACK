package com.victor.iatms.service;

import java.util.Map;

/**
 * AI诊断Service接口
 */
public interface AIDiagnosisService {
    
    /**
     * 执行AI诊断
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
}
