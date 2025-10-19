package com.victor.iatms.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.iatms.entity.dto.TestCaseExecutionDTO;
import com.victor.iatms.entity.enums.ExecutionStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试用例执行器
 */
@Slf4j
@Component
public class TestCaseExecutor {

    @Autowired
    private HttpClientUtils httpClientUtils;

    @Autowired
    private AssertionUtils assertionUtils;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 执行测试用例
     * @param executionDTO 执行信息
     * @return 执行结果
     */
    public TestCaseExecutionDTO executeTestCase(TestCaseExecutionDTO executionDTO) {
        LocalDateTime startTime = LocalDateTime.now();
        executionDTO.setExecutionStartTime(startTime);

        try {
            // 1. 验证前置条件
            if (!validatePreConditions(executionDTO)) {
                executionDTO.setExecutionStatus(ExecutionStatusEnum.SKIPPED.getCode());
                executionDTO.setFailureMessage("前置条件验证失败");
                return executionDTO;
            }

            // 2. 构建请求
            HttpRequestInfo requestInfo = buildHttpRequest(executionDTO);

            // 3. 发送HTTP请求
            HttpClientUtils.HttpResponseResult response = httpClientUtils.sendRequest(
                requestInfo.getMethod(),
                requestInfo.getUrl(),
                requestInfo.getHeaders(),
                requestInfo.getBody(),
                requestInfo.getTimeout()
            );

            // 4. 记录响应信息
            executionDTO.setHttpResponseStatus(response.getStatusCode());
            executionDTO.setHttpResponseBody(response.getBody());
            executionDTO.setHttpResponseHeaders(response.getHeaders());

            // 5. 执行断言
            List<AssertionUtils.AssertionResult> assertionResults = assertionUtils.executeAssertions(
                executionDTO.getAssertions(),
                response.getBody(),
                response.getStatusCode(),
                response.getHeaders()
            );
            
            // 转换断言结果
            List<TestCaseExecutionDTO.AssertionResultDTO> convertedResults = new ArrayList<>();
            for (AssertionUtils.AssertionResult result : assertionResults) {
                TestCaseExecutionDTO.AssertionResultDTO dto = new TestCaseExecutionDTO.AssertionResultDTO();
                dto.setAssertionType(result.getAssertionType());
                dto.setExpectedValue(result.getExpectedValue());
                dto.setActualValue(result.getActualValue());
                dto.setPassed(result.isPassed());
                dto.setErrorMessage(result.getErrorMessage());
                convertedResults.add(dto);
            }
            executionDTO.setAssertionResults(convertedResults);

            // 6. 应用提取规则
            Map<String, Object> extractedValues = applyExtractors(executionDTO, response);
            executionDTO.setExtractedValues(extractedValues);

            // 7. 判断执行状态
            ExecutionStatusEnum status = determineExecutionStatus(response, assertionResults);
            executionDTO.setExecutionStatus(status.getCode());

            // 8. 记录执行日志
            String executionLogs = generateExecutionLogs(executionDTO, requestInfo, response);
            executionDTO.setExecutionLogs(executionLogs);

        } catch (Exception e) {
            log.error("执行测试用例失败: {}", e.getMessage(), e);
            executionDTO.setExecutionStatus(ExecutionStatusEnum.FAILED.getCode());
            executionDTO.setFailureMessage(e.getMessage());
            executionDTO.setFailureTrace(getStackTrace(e));
            executionDTO.setFailureType("execution_error");
        }

        LocalDateTime endTime = LocalDateTime.now();
        executionDTO.setExecutionEndTime(endTime);
        executionDTO.setExecutionDuration(java.time.Duration.between(startTime, endTime).toMillis());

        return executionDTO;
    }

    /**
     * 验证前置条件
     */
    private boolean validatePreConditions(TestCaseExecutionDTO executionDTO) {
        String preConditions = executionDTO.getPreConditions();
        if (preConditions == null || preConditions.trim().isEmpty()) {
            return true;
        }

        try {
            // 这里可以实现前置条件的验证逻辑
            // 例如：检查依赖的接口是否可用、数据是否准备就绪等
            log.info("验证前置条件: {}", preConditions);
            return true;
        } catch (Exception e) {
            log.error("前置条件验证失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 构建HTTP请求
     */
    private HttpRequestInfo buildHttpRequest(TestCaseExecutionDTO executionDTO) {
        TestCaseExecutionDTO.ApiInfoDTO apiInfo = executionDTO.getApiInfo();
        
        // 构建URL
//        String baseUrl = executionDTO.getEnvironment() != null ?
//            getEnvironmentBaseUrl(executionDTO.getEnvironment()) : apiInfo.getBaseUrl();
        String  baseUrl = executionDTO.getApiInfo().getBaseUrl();
        String fullUrl = baseUrl + apiInfo.getPath();
        
        // 应用变量替换
        fullUrl = applyVariables(fullUrl, executionDTO.getVariables());

        // 构建请求头
        Map<String, String> headers = new HashMap<>();
        if (apiInfo.getRequestHeaders() != null) {
            try {
                Map<String, String> apiHeaders = objectMapper.readValue(
                    apiInfo.getRequestHeaders(), 
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class)
                );
                headers.putAll(apiHeaders);
            } catch (Exception e) {
                log.warn("解析请求头失败: {}", e.getMessage());
            }
        }

        // 应用请求覆盖
        if (executionDTO.getRequestOverride() != null) {
            applyRequestOverride(headers, executionDTO.getRequestOverride());
        }

        // 构建请求体
        String body = apiInfo.getRequestBody();
        if (body != null) {
            body = applyVariables(body, executionDTO.getVariables());
        }

        // 设置超时时间
        int timeout = apiInfo.getTimeoutSeconds() != null ? apiInfo.getTimeoutSeconds() : 30;

        return new HttpRequestInfo(apiInfo.getMethod(), fullUrl, headers, body, timeout);
    }

    /**
     * 获取环境基础URL
     */
    private String getEnvironmentBaseUrl(String environment) {
        // 这里可以根据环境配置返回对应的基础URL
        switch (environment.toLowerCase()) {
            case "dev":
                return "http://dev-api.example.com";
            case "test":
                return "http://test-api.example.com";
            case "prod":
                return "https://api.example.com";
            default:
                return "http://localhost:8080";
        }
    }

    /**
     * 应用变量替换
     */
    private String applyVariables(String template, Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            return template;
        }

        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            result = result.replace(placeholder, String.valueOf(entry.getValue()));
        }
        return result;
    }

    /**
     * 应用请求覆盖
     */
    private void applyRequestOverride(Map<String, String> headers, String requestOverride) {
        try {
            Map<String, Object> override = objectMapper.readValue(
                requestOverride,
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)
            );

            if (override.containsKey("headers")) {
                @SuppressWarnings("unchecked")
                Map<String, String> overrideHeaders = (Map<String, String>) override.get("headers");
                headers.putAll(overrideHeaders);
            }
        } catch (Exception e) {
            log.warn("应用请求覆盖失败: {}", e.getMessage());
        }
    }

    /**
     * 应用提取规则
     */
    private Map<String, Object> applyExtractors(TestCaseExecutionDTO executionDTO, HttpClientUtils.HttpResponseResult response) {
        Map<String, Object> extractedValues = new HashMap<>();
        
        String extractors = executionDTO.getExtractors();
        if (extractors == null || extractors.trim().isEmpty()) {
            return extractedValues;
        }

        try {
            Map<String, String> extractorRules = objectMapper.readValue(
                extractors,
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class)
            );

            for (Map.Entry<String, String> entry : extractorRules.entrySet()) {
                String key = entry.getKey();
                String jsonPath = entry.getValue();
                
                String value = extractJsonValue(response.getBody(), jsonPath);
                extractedValues.put(key, value);
            }
        } catch (Exception e) {
            log.warn("应用提取规则失败: {}", e.getMessage());
        }

        return extractedValues;
    }

    /**
     * 提取JSON值
     */
    private String extractJsonValue(String jsonBody, String jsonPath) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonBody);
            JsonNode targetNode = rootNode.at(jsonPath);
            
            if (targetNode.isMissingNode()) {
                return "";
            } else if (targetNode.isTextual()) {
                return targetNode.asText();
            } else if (targetNode.isNumber()) {
                return targetNode.asText();
            } else if (targetNode.isBoolean()) {
                return String.valueOf(targetNode.asBoolean());
            } else {
                return targetNode.toString();
            }
        } catch (Exception e) {
            log.warn("提取JSON值失败: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 判断执行状态
     */
    private ExecutionStatusEnum determineExecutionStatus(HttpClientUtils.HttpResponseResult response, 
                                                        List<AssertionUtils.AssertionResult> assertionResults) {
        // 检查HTTP状态码
        if (!response.isSuccess()) {
            return ExecutionStatusEnum.FAILED;
        }

        // 检查断言结果
        if (assertionResults != null && !assertionResults.isEmpty()) {
            for (AssertionUtils.AssertionResult result : assertionResults) {
                if (!result.isPassed()) {
                    return ExecutionStatusEnum.FAILED;
                }
            }
        }

        return ExecutionStatusEnum.PASSED;
    }

    /**
     * 生成执行日志
     */
    private String generateExecutionLogs(TestCaseExecutionDTO executionDTO, HttpRequestInfo requestInfo, 
                                       HttpClientUtils.HttpResponseResult response) {
        StringBuilder logs = new StringBuilder();
        logs.append("=== 测试用例执行日志 ===\n");
        logs.append("用例ID: ").append(executionDTO.getCaseId()).append("\n");
        logs.append("用例名称: ").append(executionDTO.getName()).append("\n");
        logs.append("执行时间: ").append(executionDTO.getExecutionStartTime()).append("\n");
        logs.append("请求方法: ").append(requestInfo.getMethod()).append("\n");
        logs.append("请求URL: ").append(requestInfo.getUrl()).append("\n");
        logs.append("响应状态码: ").append(response.getStatusCode()).append("\n");
        logs.append("执行状态: ").append(executionDTO.getExecutionStatus()).append("\n");
        
        if (executionDTO.getFailureMessage() != null) {
            logs.append("失败信息: ").append(executionDTO.getFailureMessage()).append("\n");
        }
        
        return logs.toString();
    }

    /**
     * 获取堆栈跟踪
     */
    private String getStackTrace(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * HTTP请求信息类
     */
    private static class HttpRequestInfo {
        private final String method;
        private final String url;
        private final Map<String, String> headers;
        private final String body;
        private final int timeout;

        public HttpRequestInfo(String method, String url, Map<String, String> headers, String body, int timeout) {
            this.method = method;
            this.url = url;
            this.headers = headers;
            this.body = body;
            this.timeout = timeout;
        }

        public String getMethod() { return method; }
        public String getUrl() { return url; }
        public Map<String, String> getHeaders() { return headers; }
        public String getBody() { return body; }
        public int getTimeout() { return timeout; }
    }
}
