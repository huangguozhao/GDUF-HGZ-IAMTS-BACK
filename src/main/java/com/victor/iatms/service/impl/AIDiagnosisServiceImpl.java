package com.victor.iatms.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.iatms.entity.po.TestCaseResult;
import com.victor.iatms.mappers.TestExecutionMapper;
import com.victor.iatms.service.AIDiagnosisService;
import com.victor.iatms.utils.DeepSeekUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI诊断Service实现类
 * 基于DeepSeek大模型的智能诊断系统
 */
@Slf4j
@Service
public class AIDiagnosisServiceImpl implements AIDiagnosisService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final int MAX_CACHE_SIZE = 100;
    private final Map<String, Map<String, Object>> diagnosisResultCache = new ConcurrentHashMap<>();
    private final Map<String, Boolean> diagnosisStatusCache = new ConcurrentHashMap<>();

    @Autowired
    private DeepSeekUtils deepSeekUtils;
    
    @Autowired
    private TestExecutionMapper testExecutionMapper;

    @Override
    public Map<String, Object> diagnose(String failureMessage, String failureType, Integer responseStatus,
                                         String responseBody, String apiPath, String apiMethod, String caseName) {
        return diagnose(failureMessage, failureType, responseStatus, responseBody, apiPath, apiMethod, caseName, null);
    }

    @Override
    public Map<String, Object> diagnose(String failureMessage, String failureType, Integer responseStatus,
                                         String responseBody, String apiPath, String apiMethod, String caseName,
                                         Long executionId) {
        log.info("开始AI诊断: failureMessage={}, failureType={}, responseStatus={}, executionId={}",
                 failureMessage, failureType, responseStatus, executionId);

        String diagnosisId = generateDiagnosisId(failureMessage, apiPath, caseName);
        
        Map<String, Object> ruleResult = diagnoseWithRules(failureMessage, failureType, responseStatus, 
                responseBody, apiPath, apiMethod, caseName, null);
        ruleResult.put("diagnosisId", diagnosisId);
        ruleResult.put("aiStatus", "processing");
        
        cacheResult(diagnosisId, ruleResult);
        
        callDeepSeekDiagnosisAsync(diagnosisId, failureMessage, failureType,
                responseStatus, responseBody, apiPath, apiMethod, caseName, executionId);
        
        return ruleResult;
    }
    
    private String generateDiagnosisId(String failureMessage, String apiPath, String caseName) {
        return UUID.nameUUIDFromBytes((failureMessage + apiPath + caseName + System.currentTimeMillis()).getBytes()).toString();
    }
    
    private void cacheResult(String diagnosisId, Map<String, Object> result) {
        if (diagnosisResultCache.size() >= MAX_CACHE_SIZE) {
            String oldestKey = diagnosisResultCache.keySet().iterator().next();
            diagnosisResultCache.remove(oldestKey);
            diagnosisStatusCache.remove(oldestKey);
        }
        diagnosisResultCache.put(diagnosisId, result);
        diagnosisStatusCache.put(diagnosisId, false);
    }
    
    @Override
    public Map<String, Object> getDiagnosisResult(String diagnosisId) {
        Map<String, Object> result = diagnosisResultCache.get(diagnosisId);
        if (result != null) {
            Map<String, Object> copy = new HashMap<>(result);
            Boolean aiCompleted = diagnosisStatusCache.get(diagnosisId);
            copy.put("aiCompleted", aiCompleted != null && aiCompleted);
            log.info("获取诊断结果: diagnosisId={}, aiCompleted={}, severity={}, rootCause={}", 
                    diagnosisId, aiCompleted, copy.get("severity"), copy.get("rootCause"));
            return copy;
        }
        log.warn("诊断结果不存在: diagnosisId={}", diagnosisId);
        return null;
    }
    
    private void callDeepSeekDiagnosisAsync(String diagnosisId, String failureMessage, String failureType,
                                            Integer responseStatus, String responseBody, String apiPath, 
                                            String apiMethod, String caseName, Long executionId) {
        CompletableFuture.runAsync(() -> {
            try {
                log.info("开始异步调用DeepSeek API进行AI诊断, diagnosisId={}", diagnosisId);
                
                List<TestCaseResult> allCaseResults = null;
                if (executionId != null) {
                    allCaseResults = testExecutionMapper.findTestCaseResultsByExecutionId(executionId);
                    log.info("获取到{}条测试用例结果", allCaseResults != null ? allCaseResults.size() : 0);
                }
                
                Map<String, Object> aiResult = callDeepSeekDiagnosis(failureMessage, failureType,
                        responseStatus, responseBody, apiPath, apiMethod, caseName, allCaseResults);
                
                if (aiResult != null && !aiResult.isEmpty()) {
                    aiResult.put("diagnosisId", diagnosisId);
                    aiResult.put("aiStatus", "completed");
                    diagnosisResultCache.put(diagnosisId, aiResult);
                    diagnosisStatusCache.put(diagnosisId, true);
                    log.info("AI诊断完成, diagnosisId={}, severity={}, rootCause={}, issuesCount={}, suggestionsCount={}", 
                            diagnosisId, aiResult.get("severity"), aiResult.get("rootCause"),
                            aiResult.get("issues") != null ? ((List<?>)aiResult.get("issues")).size() : 0,
                            aiResult.get("suggestions") != null ? ((List<?>)aiResult.get("suggestions")).size() : 0);
                } else {
                    Map<String, Object> currentResult = diagnosisResultCache.get(diagnosisId);
                    if (currentResult != null) {
                        currentResult.put("aiStatus", "failed");
                        diagnosisStatusCache.put(diagnosisId, true);
                    }
                    log.warn("AI诊断返回为空, diagnosisId={}", diagnosisId);
                }
            } catch (Exception e) {
                log.error("异步AI诊断异常, diagnosisId={}", diagnosisId, e);
                Map<String, Object> currentResult = diagnosisResultCache.get(diagnosisId);
                if (currentResult != null) {
                    currentResult.put("aiStatus", "failed");
                    currentResult.put("aiError", e.getMessage());
                    diagnosisStatusCache.put(diagnosisId, true);
                }
            }
        });
    }

    /**
     * 调用DeepSeek API进行AI诊断
     */
    private Map<String, Object> callDeepSeekDiagnosis(String failureMessage, String failureType, Integer responseStatus,
                                                       String responseBody, String apiPath, String apiMethod, String caseName,
                                                       List<TestCaseResult> allCaseResults) {
        try {
            // 构建系统提示词
            String systemPrompt = buildSystemPrompt();

            // 构建用户消息
            String userMessage = buildUserMessage(failureMessage, failureType, responseStatus,
                    responseBody, apiPath, apiMethod, caseName, allCaseResults);

            log.info("准备调用DeepSeek API进行AI诊断...");
            String aiResponse = deepSeekUtils.chat(systemPrompt, userMessage);

            if (aiResponse == null || aiResponse.isEmpty()) {
                log.error("DeepSeek API返回为空");
                return null;
            }

            log.info("DeepSeek API返回: {}", aiResponse);

            // 解析AI返回的JSON结果
            return parseAIResponse(aiResponse);

        } catch (Exception e) {
            log.error("调用DeepSeek API进行AI诊断时发生异常: ", e);
            return null;
        }
    }
    
    /**
     * 构建系统提示词
     */
    private String buildSystemPrompt() {
        return "你是一个专业的API测试诊断专家。你的任务是根据测试执行失败的信息，分析失败原因，并提供详细的诊断结果和修复建议。\n\n" +
                "请以JSON格式返回诊断结果，格式如下：\n" +
                "{\n" +
                "  \"severity\": \"high/medium/low\",  // 问题严重程度\n" +
                "  \"rootCause\": \"根本原因描述\",      // 根本原因分析\n" +
                "  \"issues\": [                       // 发现的问题列表\n" +
                "    {\n" +
                "      \"title\": \"问题标题\",\n" +
                "      \"severity\": \"high/medium/low\",\n" +
                "      \"description\": \"问题详细描述\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"suggestions\": [                  // 修复建议列表\n" +
                "    {\n" +
                "      \"title\": \"建议标题\",\n" +
                "      \"content\": \"建议详细说明\",\n" +
                "      \"priority\": \"high/medium/low\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"analysis\": [                     // 分析过程记录\n" +
                "    \"分析步骤1\",\n" +
                "    \"分析步骤2\"\n" +
                "  ],\n" +
                "  \"batchInfo\": {                     // 批量测试信息（如果有）\n" +
                "    \"total\": 0,\n" +
                "    \"passed\": 0,\n" +
                "    \"failed\": 0,\n" +
                "    \"broken\": 0,\n" +
                "    \"skipped\": 0,\n" +
                "    \"failedCases\": []               // 失败用例详情\n" +
                "  }\n" +
                "}\n\n" +
                "请确保返回的是有效的JSON格式，不要包含其他文字说明。";
    }

    /**
     * 构建用户消息
     */
    private String buildUserMessage(String failureMessage, String failureType, Integer responseStatus,
                                     String responseBody, String apiPath, String apiMethod, String caseName,
                                     List<TestCaseResult> allCaseResults) {
        StringBuilder sb = new StringBuilder();
        sb.append("请分析以下API测试失败信息：\n\n");

        // 批量测试信息
        if (allCaseResults != null && !allCaseResults.isEmpty()) {
            sb.append("【批量测试结果统计】\n");
            int passedCount = 0, failedCount = 0, brokenCount = 0, skippedCount = 0;
            for (TestCaseResult cr : allCaseResults) {
                String status = cr.getStatus();
                if ("passed".equals(status)) passedCount++;
                else if ("failed".equals(status)) failedCount++;
                else if ("broken".equals(status)) brokenCount++;
                else if ("skipped".equals(status)) skippedCount++;
            }
            sb.append("总用例数: ").append(allCaseResults.size()).append("\n");
            sb.append("通过: ").append(passedCount).append(", 失败: ").append(failedCount);
            sb.append(", 异常: ").append(brokenCount).append(", 跳过: ").append(skippedCount).append("\n\n");

            // 失败用例详情
            if (failedCount > 0 || brokenCount > 0) {
                sb.append("【失败用例详情】\n");
                for (TestCaseResult cr : allCaseResults) {
                    if ("failed".equals(cr.getStatus()) || "broken".equals(cr.getStatus())) {
                        sb.append("- 用例: ").append(cr.getCaseName()).append(" [").append(cr.getCaseCode()).append("]\n");
                        sb.append("  状态: ").append(cr.getStatus()).append("\n");
                        sb.append("  耗时: ").append(cr.getDuration()).append("ms\n");
                        if (cr.getFailureMessage() != null) {
                            sb.append("  失败原因: ").append(cr.getFailureMessage()).append("\n");
                        }
                        if (cr.getFailureType() != null) {
                            sb.append("  失败类型: ").append(cr.getFailureType()).append("\n");
                        }
                        sb.append("\n");
                    }
                }
            }
        }

        // 当前测试用例信息
        sb.append("【当前测试用例信息】\n");
        sb.append("用例名称: ").append(caseName != null ? caseName : "未知").append("\n");
        sb.append("API路径: ").append(apiPath != null ? apiPath : "未知").append("\n");
        sb.append("请求方法: ").append(apiMethod != null ? apiMethod : "未知").append("\n\n");

        // 失败信息
        sb.append("【失败信息】\n");
        sb.append("失败消息: ").append(failureMessage != null ? failureMessage : "无").append("\n");
        sb.append("失败类型: ").append(failureType != null ? failureType : "未知").append("\n");
        sb.append("响应状态码: ").append(responseStatus != null ? responseStatus : "无").append("\n");

        // 响应体
        if (responseBody != null && !responseBody.isEmpty()) {
            sb.append("响应体: ").append(responseBody).append("\n");
        }

        sb.append("\n请根据以上信息进行诊断分析。");
        return sb.toString();
    }

    /**
     * 解析AI返回的JSON响应
     */
    private Map<String, Object> parseAIResponse(String aiResponse) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 预处理：去除markdown代码块标记（如 ```json 和 ```）
            String jsonStr = aiResponse.trim();
            if (jsonStr.startsWith("```")) {
                // 找到第一个换行位置
                int firstNewline = jsonStr.indexOf('\n');
                if (firstNewline > 0) {
                    // 去除开头的 ```json 或 ``` 等语言标记
                    jsonStr = jsonStr.substring(firstNewline + 1);
                }
            }
            // 去除结尾的 ```
            if (jsonStr.endsWith("```")) {
                jsonStr = jsonStr.substring(0, jsonStr.length() - 3).trim();
            }

            log.debug("预处理后的JSON字符串: {}", jsonStr);

            // 尝试解析JSON
            JsonNode jsonNode = objectMapper.readTree(jsonStr);

            // 提取severity
            if (jsonNode.has("severity")) {
                result.put("severity", jsonNode.get("severity").asText());
            } else {
                result.put("severity", "medium");
            }

            // 提取rootCause
            if (jsonNode.has("rootCause")) {
                result.put("rootCause", jsonNode.get("rootCause").asText());
            } else {
                result.put("rootCause", "AI分析中");
            }

            // 提取issues
            List<Map<String, String>> issues = new ArrayList<>();
            if (jsonNode.has("issues")) {
                JsonNode issuesNode = jsonNode.get("issues");
                if (issuesNode.isArray()) {
                    for (JsonNode issueNode : issuesNode) {
                        Map<String, String> issue = new HashMap<>();
                        if (issueNode.has("title")) issue.put("title", issueNode.get("title").asText());
                        if (issueNode.has("severity")) issue.put("severity", issueNode.get("severity").asText());
                        if (issueNode.has("description")) issue.put("description", issueNode.get("description").asText());
                        issues.add(issue);
                    }
                }
            }
            result.put("issues", issues);

            // 提取suggestions
            List<Map<String, String>> suggestions = new ArrayList<>();
            if (jsonNode.has("suggestions")) {
                JsonNode suggestionsNode = jsonNode.get("suggestions");
                if (suggestionsNode.isArray()) {
                    for (JsonNode suggestionNode : suggestionsNode) {
                        Map<String, String> suggestion = new HashMap<>();
                        if (suggestionNode.has("title")) suggestion.put("title", suggestionNode.get("title").asText());
                        if (suggestionNode.has("content")) suggestion.put("content", suggestionNode.get("content").asText());
                        if (suggestionNode.has("priority")) suggestion.put("priority", suggestionNode.get("priority").asText());
                        suggestions.add(suggestion);
                    }
                }
            }
            result.put("suggestions", suggestions);

            // 提取analysis
            List<String> analysis = new ArrayList<>();
            if (jsonNode.has("analysis")) {
                JsonNode analysisNode = jsonNode.get("analysis");
                if (analysisNode.isArray()) {
                    for (JsonNode aNode : analysisNode) {
                        analysis.add(aNode.asText());
                    }
                }
            }
            result.put("analysis", analysis);

            // 提取batchInfo
            if (jsonNode.has("batchInfo")) {
                result.put("batchInfo", objectMapper.convertValue(jsonNode.get("batchInfo"), Map.class));
            }

            return result;

        } catch (Exception e) {
            log.error("解析AI响应JSON失败: ", e);
            // 如果解析失败，尝试将整个响应作为文本处理
            result.put("severity", "medium");
            result.put("rootCause", "AI分析完成");
            result.put("issues", new ArrayList<>());
            result.put("suggestions", new ArrayList<>());
            List<String> analysis = new ArrayList<>();
            analysis.add("AI诊断结果: " + aiResponse);
            result.put("analysis", analysis);
            return result;
        }
    }

    /**
     * 规则引擎诊断（兜底方案）
     */
    private Map<String, Object> diagnoseWithRules(String failureMessage, String failureType, Integer responseStatus,
                                                    String responseBody, String apiPath, String apiMethod, String caseName,
                                                    List<TestCaseResult> allCaseResults) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> issues = new ArrayList<>();
        List<Map<String, String>> suggestions = new ArrayList<>();
        List<String> analysis = new ArrayList<>();
        String severity = "medium";
        String rootCause = "";

        // 0. 如果有批量测试用例结果，先分析批量结果
        if (allCaseResults != null && !allCaseResults.isEmpty()) {
            analysis.add("========== 批量测试用例诊断 ==========");
            analysis.add("本次共执行 " + allCaseResults.size() + " 个测试用例");
            
            int passedCount = 0;
            int failedCount = 0;
            int brokenCount = 0;
            int skippedCount = 0;
            
            for (TestCaseResult caseResult : allCaseResults) {
                String status = caseResult.getStatus();
                if ("passed".equals(status)) {
                    passedCount++;
                } else if ("failed".equals(status)) {
                    failedCount++;
                } else if ("broken".equals(status)) {
                    brokenCount++;
                } else if ("skipped".equals(status)) {
                    skippedCount++;
                }
            }
            
            analysis.add("执行结果: " + passedCount + " 通过, " + failedCount + " 失败, " + brokenCount + " 异常, " + skippedCount + " 跳过");
            
            // 如果有失败的用例，分析失败用例
            if (failedCount > 0 || brokenCount > 0) {
                analysis.add("");
                analysis.add("========== 失败用例详情 ==========");
                
                for (TestCaseResult caseResult : allCaseResults) {
                    if ("failed".equals(caseResult.getStatus()) || "broken".equals(caseResult.getStatus())) {
                        analysis.add("用例: " + caseResult.getCaseName() + " [" + caseResult.getCaseCode() + "]");
                        analysis.add("  状态: " + caseResult.getStatus());
                        analysis.add("  耗时: " + caseResult.getDuration() + "ms");
                        if (caseResult.getFailureMessage() != null && !caseResult.getFailureMessage().isEmpty()) {
                            analysis.add("  失败原因: " + caseResult.getFailureMessage());
                        }
                        if (caseResult.getFailureType() != null) {
                            analysis.add("  失败类型: " + caseResult.getFailureType());
                        }
                        analysis.add("");
                    }
                }
            }
            
            // 更新严重程度
            if (failedCount > 0 || brokenCount > 0) {
                if (failedCount + brokenCount >= allCaseResults.size() * 0.5) {
                    severity = "high";
                } else if (failedCount + brokenCount >= allCaseResults.size() * 0.2) {
                    severity = "medium";
                }
                rootCause = "批量测试中 " + (failedCount + brokenCount) + " 个用例执行失败";
            }
        }

        // 1. 分析失败类型
        if (failureMessage != null && !failureMessage.isEmpty()) {
            analysis.add("========== 单个用例诊断 ==========");
            analysis.add("检测到失败消息: " + failureMessage);
            
            // URL格式错误
            if (failureMessage.contains("no protocol") || failureMessage.contains("URL格式错误") || failureMessage.contains("MalformedURL")) {
                issues.add(createIssue("URL格式错误", "high", "Base URL缺少协议前缀"));
                suggestions.add(createSuggestion("检查Base URL配置", "在环境配置或执行参数中填写完整的URL（如 http://localhost:8080）", "high"));
                suggestions.add(createSuggestion("检查API路径", "确保接口路径以 / 开头", "medium"));
                rootCause = rootCause.isEmpty() ? "Base URL配置不完整，缺少协议前缀（http:// 或 https://）" : rootCause;
                severity = "high";
            }
            
            // 连接被拒绝
            if (failureMessage.contains("connection") || failureMessage.contains("Connection refused") || failureMessage.contains("连接被拒绝")) {
                issues.add(createIssue("连接失败", "high", "无法连接到目标服务器"));
                suggestions.add(createSuggestion("检查目标服务", "确认被测系统是否已启动", "high"));
                suggestions.add(createSuggestion("检查端口号", "确认端口号是否正确", "high"));
                suggestions.add(createSuggestion("检查防火墙", "确认防火墙未阻止连接", "medium"));
                rootCause = rootCause.isEmpty() ? "目标服务器不可达，可能未启动或端口错误" : rootCause;
                severity = "high";
            }
            
            // 请求超时
            if (failureMessage.contains("timeout") || failureMessage.contains("超时") || failureMessage.contains("SocketTimeout")) {
                issues.add(createIssue("请求超时", "high", "等待响应时间过长"));
                suggestions.add(createSuggestion("增加超时时间", "在执行配置中增加超时时间", "medium"));
                suggestions.add(createSuggestion("检查服务性能", "确认被测系统响应正常", "high"));
                suggestions.add(createSuggestion("检查网络延迟", "确认网络连接稳定", "medium"));
                rootCause = rootCause.isEmpty() ? "被测系统响应缓慢或网络延迟过高" : rootCause;
                severity = "medium";
            }
            
            // DNS解析失败
            if (failureMessage.contains("Unknown host") || failureMessage.contains("未知主机") || failureMessage.contains("UnknownHost")) {
                issues.add(createIssue("DNS解析失败", "high", "无法解析主机名"));
                suggestions.add(createSuggestion("检查域名配置", "确认域名拼写正确", "high"));
                suggestions.add(createSuggestion("检查DNS配置", "确认DNS服务器可访问", "medium"));
                rootCause = rootCause.isEmpty() ? "主机名无法解析，域名配置错误或DNS问题" : rootCause;
                severity = "high";
            }
            
            // SSL证书错误
            if (failureMessage.contains("SSL") || failureMessage.contains("证书") || failureMessage.contains("ssl")) {
                issues.add(createIssue("SSL证书错误", "high", "HTTPS证书验证失败"));
                suggestions.add(createSuggestion("检查证书有效期", "确认SSL证书未过期", "high"));
                suggestions.add(createSuggestion("使用HTTP", "测试环境可使用HTTP协议", "medium"));
                rootCause = rootCause.isEmpty() ? "SSL证书无效或不受信任" : rootCause;
                severity = "high";
            }
        }
        
        // 2. 分析HTTP响应状态码
        if (responseStatus != null) {
            analysis.add("HTTP响应状态码: " + responseStatus);
            switch (responseStatus) {
                case 400:
                    issues.add(createIssue("400 Bad Request", "high", "请求参数错误"));
                    suggestions.add(createSuggestion("检查请求参数", "确认请求参数格式和内容正确", "high"));
                    suggestions.add(createSuggestion("检查请求头", "确认Content-Type等请求头正确", "medium"));
                    rootCause = rootCause.isEmpty() ? "请求参数格式错误或缺少必要参数" : rootCause;
                    severity = "high";
                    break;
                case 401:
                    issues.add(createIssue("401 Unauthorized", "high", "认证失败"));
                    suggestions.add(createSuggestion("检查Token", "确认Token是否过期或有效", "high"));
                    suggestions.add(createSuggestion("检查认证信息", "确认用户名密码正确", "high"));
                    suggestions.add(createSuggestion("检查请求头", "确认Authorization头正确", "high"));
                    rootCause = rootCause.isEmpty() ? "认证信息无效或已过期" : rootCause;
                    severity = "high";
                    break;
                case 403:
                    issues.add(createIssue("403 Forbidden", "medium", "权限不足"));
                    suggestions.add(createSuggestion("检查权限", "确认用户有权限访问该资源", "high"));
                    suggestions.add(createSuggestion("检查API权限", "确认API是否需要特殊权限", "medium"));
                    rootCause = rootCause.isEmpty() ? "当前用户权限不足" : rootCause;
                    severity = "medium";
                    break;
                case 404:
                    issues.add(createIssue("404 Not Found", "high", "资源不存在"));
                    suggestions.add(createSuggestion("检查API路径", "确认API路径正确", "high"));
                    suggestions.add(createSuggestion("检查接口是否存在", "确认接口已部署", "high"));
                    rootCause = rootCause.isEmpty() ? "API路径错误或接口不存在" : rootCause;
                    severity = "high";
                    break;
                case 500:
                    issues.add(createIssue("500 Internal Server Error", "high", "服务器内部错误"));
                    suggestions.add(createSuggestion("检查服务端日志", "查看服务端错误日志", "high"));
                    suggestions.add(createSuggestion("检查请求参数", "确认请求参数符合服务端要求", "medium"));
                    rootCause = rootCause.isEmpty() ? "服务端发生内部错误" : rootCause;
                    severity = "high";
                    break;
                case 502:
                    issues.add(createIssue("502 Bad Gateway", "high", "网关错误"));
                    suggestions.add(createSuggestion("检查网关服务", "确认网关服务正常运行", "high"));
                    suggestions.add(createSuggestion("检查上游服务", "确认上游服务可用", "high"));
                    rootCause = rootCause.isEmpty() ? "网关配置错误或上游服务不可用" : rootCause;
                    severity = "high";
                    break;
                case 503:
                    issues.add(createIssue("503 Service Unavailable", "high", "服务不可用"));
                    suggestions.add(createSuggestion("检查服务状态", "确认服务正在运行", "high"));
                    suggestions.add(createSuggestion("检查维护状态", "确认服务不在维护中", "medium"));
                    rootCause = rootCause.isEmpty() ? "服务暂时不可用，可能在维护或过载" : rootCause;
                    severity = "high";
                    break;
            }
        }
        
        // 3. 分析响应体中的业务错误码
        if (responseBody != null && !responseBody.isEmpty()) {
            try {
                JsonNode rootNode = objectMapper.readTree(responseBody);
                if (rootNode.has("code")) {
                    int code = rootNode.get("code").asInt();
                    String msg = rootNode.has("msg") ? rootNode.get("msg").asText() : "";
                    analysis.add("业务响应码: " + code + ", 消息: " + msg);
                    if (code != 1 && code != 200 && code != 0) {
                        issues.add(createIssue("业务错误", "high", "code=" + code + ", msg=" + msg));
                        suggestions.add(createSuggestion("分析业务错误消息", "根据业务错误消息进行排查", "high"));
                        suggestions.add(createSuggestion("检查请求参数", "确认请求参数符合业务要求", "medium"));
                        rootCause = rootCause.isEmpty() ? "业务处理失败: " + msg : rootCause;
                        severity = "high";
                    }
                }
            } catch (Exception e) {
                log.debug("解析响应体失败: {}", e.getMessage());
            }
        }
        
        // 4. 如果没有识别出任何问题，提供通用建议
        if (issues.isEmpty()) {
            issues.add(createIssue("未知错误", "medium", "无法确定具体原因"));
            suggestions.add(createSuggestion("查看详细日志", "查看完整执行日志了解详情", "medium"));
            suggestions.add(createSuggestion("检查测试用例配置", "确认测试用例配置正确", "medium"));
            rootCause = rootCause.isEmpty() ? "无法确定失败原因" : rootCause;
            severity = "low";
        }
        
        // 5. 构建诊断结果
        result.put("success", true);
        result.put("caseName", caseName);
        result.put("apiPath", apiPath);
        result.put("apiMethod", apiMethod);
        result.put("severity", severity);
        result.put("rootCause", rootCause);
        result.put("analysis", analysis);
        result.put("issues", issues);
        result.put("suggestions", suggestions);
        result.put("timestamp", System.currentTimeMillis());
        
        // 添加批量测试统计信息
        if (allCaseResults != null && !allCaseResults.isEmpty()) {
            Map<String, Object> batchInfo = new HashMap<>();
            int total = allCaseResults.size();
            int passed = 0, failed = 0;
            for (TestCaseResult r : allCaseResults) {
                if ("passed".equals(r.getStatus())) passed++;
                else if ("failed".equals(r.getStatus())) failed++;
            }
            batchInfo.put("totalCases", total);
            batchInfo.put("passedCases", passed);
            batchInfo.put("failedCases", failed);
            result.put("batchInfo", batchInfo);
        }
        
        log.info("AI诊断完成: severity={}, issuesCount={}, suggestionsCount={}",
                 severity, issues.size(), suggestions.size());
        return result;
    }

    private Map<String, String> createIssue(String title, String severity, String description) {
        Map<String, String> issue = new HashMap<>();
        issue.put("title", title);
        issue.put("severity", severity);
        issue.put("description", description);
        return issue;
    }

    private Map<String, String> createSuggestion(String title, String content, String priority) {
        Map<String, String> suggestion = new HashMap<>();
        suggestion.put("title", title);
        suggestion.put("content", content);
        suggestion.put("priority", priority);
        return suggestion;
    }
}
