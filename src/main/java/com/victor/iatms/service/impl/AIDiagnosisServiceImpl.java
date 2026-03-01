package com.victor.iatms.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.iatms.service.AIDiagnosisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AI诊断Service实现类
 * 基于规则的智能诊断系统
 */
@Slf4j
@Service
public class AIDiagnosisServiceImpl implements AIDiagnosisService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> diagnose(String failureMessage, String failureType, Integer responseStatus,
                                         String responseBody, String apiPath, String apiMethod, String caseName) {
        log.info("开始AI诊断: failureMessage={}, failureType={}, responseStatus={}", 
                 failureMessage, failureType, responseStatus);

        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> issues = new ArrayList<>();
        List<Map<String, String>> suggestions = new ArrayList<>();
        List<String> analysis = new ArrayList<>();
        String severity = "medium";
        String rootCause = "";

        // 1. 分析失败类型
        if (failureMessage != null && !failureMessage.isEmpty()) {
            analysis.add("检测到失败消息: " + failureMessage);
            
            // URL格式错误
            if (failureMessage.contains("no protocol") || failureMessage.contains("URL格式错误") || failureMessage.contains("MalformedURL")) {
                issues.add(createIssue("URL格式错误", "high", "Base URL缺少协议前缀"));
                suggestions.add(createSuggestion("检查Base URL配置", "在环境配置或执行参数中填写完整的URL（如 http://localhost:8080）", "high"));
                suggestions.add(createSuggestion("检查API路径", "确保接口路径以 / 开头", "medium"));
                rootCause = "Base URL配置不完整，缺少协议前缀（http:// 或 https://）";
                severity = "high";
            }
            
            // 连接被拒绝
            if (failureMessage.contains("connection") || failureMessage.contains("Connection refused") || failureMessage.contains("连接被拒绝")) {
                issues.add(createIssue("连接失败", "high", "无法连接到目标服务器"));
                suggestions.add(createSuggestion("检查目标服务", "确认被测系统是否已启动", "high"));
                suggestions.add(createSuggestion("检查端口号", "确认端口号是否正确", "high"));
                suggestions.add(createSuggestion("检查防火墙", "确认防火墙未阻止连接", "medium"));
                rootCause = "目标服务器不可达，可能未启动或端口错误";
                severity = "high";
            }
            
            // 请求超时
            if (failureMessage.contains("timeout") || failureMessage.contains("超时") || failureMessage.contains("SocketTimeout")) {
                issues.add(createIssue("请求超时", "high", "等待响应时间过长"));
                suggestions.add(createSuggestion("增加超时时间", "在执行配置中增加超时时间", "medium"));
                suggestions.add(createSuggestion("检查服务性能", "确认被测系统响应正常", "high"));
                suggestions.add(createSuggestion("检查网络延迟", "确认网络连接稳定", "medium"));
                rootCause = "被测系统响应缓慢或网络延迟过高";
                severity = "medium";
            }
            
            // 未知主机
            if (failureMessage.contains("Unknown host") || failureMessage.contains("未知主机") || failureMessage.contains("UnknownHost")) {
                issues.add(createIssue("DNS解析失败", "high", "无法解析主机名"));
                suggestions.add(createSuggestion("检查域名配置", "确认域名拼写正确", "high"));
                suggestions.add(createSuggestion("检查DNS配置", "确认DNS服务器可访问", "medium"));
                rootCause = "主机名无法解析，域名配置错误或DNS问题";
                severity = "high";
            }
            
            // SSL错误
            if (failureMessage.contains("SSL") || failureMessage.contains("证书") || failureMessage.contains("ssl")) {
                issues.add(createIssue("SSL证书错误", "high", "HTTPS证书验证失败"));
                suggestions.add(createSuggestion("检查证书有效期", "确认SSL证书未过期", "high"));
                suggestions.add(createSuggestion("使用HTTP", "测试环境可使用HTTP协议", "medium"));
                rootCause = "SSL证书无效或不受信任";
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
                    rootCause = "请求参数格式错误或缺少必要参数";
                    severity = "high";
                    break;
                case 401:
                    issues.add(createIssue("401 Unauthorized", "high", "认证失败"));
                    suggestions.add(createSuggestion("检查Token", "确认Token是否过期或有效", "high"));
                    suggestions.add(createSuggestion("检查认证信息", "确认用户名密码正确", "high"));
                    suggestions.add(createSuggestion("检查请求头", "确认Authorization头正确", "high"));
                    rootCause = "认证信息无效或已过期";
                    severity = "high";
                    break;
                case 403:
                    issues.add(createIssue("403 Forbidden", "medium", "权限不足"));
                    suggestions.add(createSuggestion("检查权限", "确认用户有权限访问该资源", "high"));
                    suggestions.add(createSuggestion("检查API权限", "确认API是否需要特殊权限", "medium"));
                    rootCause = "当前用户权限不足";
                    severity = "medium";
                    break;
                case 404:
                    issues.add(createIssue("404 Not Found", "high", "资源不存在"));
                    suggestions.add(createSuggestion("检查API路径", "确认API路径正确", "high"));
                    suggestions.add(createSuggestion("检查接口是否存在", "确认接口已部署", "high"));
                    rootCause = "API路径错误或接口不存在";
                    severity = "high";
                    break;
                case 500:
                    issues.add(createIssue("500 Internal Server Error", "high", "服务器内部错误"));
                    suggestions.add(createSuggestion("检查服务端日志", "查看服务端错误日志", "high"));
                    suggestions.add(createSuggestion("检查请求参数", "确认请求参数符合服务端要求", "medium"));
                    rootCause = "服务端发生内部错误";
                    severity = "high";
                    break;
                case 502:
                    issues.add(createIssue("502 Bad Gateway", "high", "网关错误"));
                    suggestions.add(createSuggestion("检查网关服务", "确认网关服务正常运行", "high"));
                    suggestions.add(createSuggestion("检查上游服务", "确认上游服务可用", "high"));
                    rootCause = "网关配置错误或上游服务不可用";
                    severity = "high";
                    break;
                case 503:
                    issues.add(createIssue("503 Service Unavailable", "high", "服务不可用"));
                    suggestions.add(createSuggestion("检查服务状态", "确认服务正在运行", "high"));
                    suggestions.add(createSuggestion("检查维护状态", "确认服务不在维护中", "medium"));
                    rootCause = "服务暂时不可用，可能在维护或过载";
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
                        rootCause = "业务处理失败: " + msg;
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
            rootCause = "无法确定失败原因";
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
