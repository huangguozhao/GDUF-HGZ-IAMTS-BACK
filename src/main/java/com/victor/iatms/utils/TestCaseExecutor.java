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
            List<AssertionUtils.AssertionResult> assertionResults;
            
            // 5.1 如果没有明确的断言规则，自动生成基础断言
            String assertions = executionDTO.getAssertions();
            if (assertions == null || assertions.trim().isEmpty() || "null".equals(assertions)) {
                log.info("未定义断言规则，自动生成基础断言");
                assertions = generateDefaultAssertions(executionDTO);
                log.info("自动生成的断言: {}", assertions);
            }
            
            // 5.2 执行断言
            assertionResults = assertionUtils.executeAssertions(
                assertions,
                response.getBody(),
                response.getStatusCode(),
                response.getHeaders()
            );
            
            // 5.3 转换断言结果并生成详细日志
            List<TestCaseExecutionDTO.AssertionResultDTO> convertedResults = new ArrayList<>();
            StringBuilder assertionLog = new StringBuilder();
            assertionLog.append("\n========== 断言执行详情 ==========\n");
            
            int passedCount = 0;
            int failedCount = 0;
            
            for (int i = 0; i < assertionResults.size(); i++) {
                AssertionUtils.AssertionResult result = assertionResults.get(i);
                TestCaseExecutionDTO.AssertionResultDTO dto = new TestCaseExecutionDTO.AssertionResultDTO();
                dto.setAssertionType(result.getAssertionType());
                dto.setExpectedValue(result.getExpectedValue());
                dto.setActualValue(result.getActualValue());
                dto.setPassed(result.isPassed());
                dto.setErrorMessage(result.getErrorMessage());
                convertedResults.add(dto);
                
                // 生成详细的断言日志
                assertionLog.append(String.format("断言 #%d [%s]\n", i + 1, result.getAssertionType()));
                assertionLog.append(String.format("  期望值: %s\n", result.getExpectedValue()));
                assertionLog.append(String.format("  实际值: %s\n", result.getActualValue()));
                assertionLog.append(String.format("  结果: %s\n", result.isPassed() ? "✓ 通过" : "✗ 失败"));
                
                if (!result.isPassed()) {
                    assertionLog.append(String.format("  错误: %s\n", result.getErrorMessage()));
                    failedCount++;
                } else {
                    passedCount++;
                }
                assertionLog.append("\n");
            }
            
            assertionLog.append(String.format("总计: %d 个断言，通过 %d，失败 %d\n", 
                assertionResults.size(), passedCount, failedCount));
            assertionLog.append("=====================================\n");
            
            // 打印详细日志
            log.info(assertionLog.toString());
            
            executionDTO.setAssertionResults(convertedResults);
            
            // 如果有失败的断言，设置失败信息
            if (failedCount > 0) {
                StringBuilder failureMsg = new StringBuilder();
                failureMsg.append(String.format("断言失败：%d/%d 个断言未通过\n", failedCount, assertionResults.size()));
                
                for (TestCaseExecutionDTO.AssertionResultDTO dto : convertedResults) {
                    if (!dto.getPassed()) {
                        failureMsg.append(String.format("  - [%s] %s\n", dto.getAssertionType(), dto.getErrorMessage()));
                    }
                }
                
                // 将失败信息保存到执行结果中
                if (executionDTO.getFailureMessage() == null || executionDTO.getFailureMessage().isEmpty()) {
                    executionDTO.setFailureMessage(failureMsg.toString());
                    executionDTO.setFailureType("ASSERTION_FAILED");
                }
            }

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
     * 自动生成默认断言规则
     */
    private String generateDefaultAssertions(TestCaseExecutionDTO executionDTO) {
        List<Map<String, Object>> assertions = new ArrayList<>();
        
        try {
            // 1. HTTP状态码断言
            if (executionDTO.getExpectedHttpStatus() != null) {
                Map<String, Object> statusAssertion = new HashMap<>();
                statusAssertion.put("type", "status_code");
                statusAssertion.put("expected", executionDTO.getExpectedHttpStatus());
                assertions.add(statusAssertion);
                log.info("添加状态码断言: {}", executionDTO.getExpectedHttpStatus());
            }
            
            // 2. 响应体断言（基于expected_response_body）
            String expectedBody = executionDTO.getExpectedResponseBody();
            if (expectedBody != null && !expectedBody.trim().isEmpty() && !"null".equals(expectedBody)) {
                try {
                    JsonNode expectedNode = objectMapper.readTree(expectedBody);
                    
                    // 遍历期待响应的字段，为每个字段生成断言
                    generateAssertionsFromExpectedBody(expectedNode, "$", assertions);
                    
                } catch (Exception e) {
                    log.warn("解析expected_response_body失败: {}", e.getMessage());
                }
            }
            
            // 3. 如果没有任何断言，至少验证响应成功
            if (assertions.isEmpty()) {
                Map<String, Object> defaultAssertion = new HashMap<>();
                defaultAssertion.put("type", "status_code");
                defaultAssertion.put("expected", 200);
                assertions.add(defaultAssertion);
            }
            
            return objectMapper.writeValueAsString(assertions);
            
        } catch (Exception e) {
            log.error("生成默认断言失败: {}", e.getMessage(), e);
            return "[]";
        }
    }
    
    /**
     * 从期待响应体生成断言
     */
    private void generateAssertionsFromExpectedBody(JsonNode node, String path, List<Map<String, Object>> assertions) {
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                String fieldName = entry.getKey();
                JsonNode fieldValue = entry.getValue();
                String fieldPath = path.equals("$") ? "$." + fieldName : path + "." + fieldName;
                
                if (fieldValue.isTextual()) {
                    String value = fieldValue.asText();
                    if (!"*".equals(value)) {  // * 表示只验证存在，不验证值
                        Map<String, Object> assertion = new HashMap<>();
                        assertion.put("type", "json_path");
                        assertion.put("path", fieldPath);
                        assertion.put("expected", value);
                        assertions.add(assertion);
                        log.info("添加字段断言: {} = {}", fieldPath, value);
                    } else {
                        // * 表示只验证字段存在
                        Map<String, Object> assertion = new HashMap<>();
                        assertion.put("type", "json_path_exists");
                        assertion.put("path", fieldPath);
                        assertions.add(assertion);
                        log.info("添加字段存在断言: {}", fieldPath);
                    }
                } else if (fieldValue.isNumber()) {
                    Map<String, Object> assertion = new HashMap<>();
                    assertion.put("type", "json_path");
                    assertion.put("path", fieldPath);
                    assertion.put("expected", fieldValue.asText());
                    assertions.add(assertion);
                } else if (fieldValue.isBoolean()) {
                    Map<String, Object> assertion = new HashMap<>();
                    assertion.put("type", "json_path");
                    assertion.put("path", fieldPath);
                    assertion.put("expected", String.valueOf(fieldValue.asBoolean()));
                    assertions.add(assertion);
                } else if (fieldValue.isObject()) {
                    // 递归处理嵌套对象
                    if (!fieldValue.toString().equals("{\"*\"}")) {
                        generateAssertionsFromExpectedBody(fieldValue, fieldPath, assertions);
                    }
                }
            });
        }
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
        
        // 1. 构建URL
        String baseUrl = executionDTO.getApiInfo().getBaseUrl();
        String fullUrl = baseUrl + apiInfo.getPath();
        
        // 应用变量替换
        fullUrl = applyVariables(fullUrl, executionDTO.getVariables());

        // 2. 构建请求头（接口默认 + 用例覆盖）
        Map<String, String> headers = new HashMap<>();
        
        // 2.1 加载接口默认请求头
        if (apiInfo.getRequestHeaders() != null && !apiInfo.getRequestHeaders().trim().isEmpty()) {
            try {
                JsonNode headersNode = objectMapper.readTree(apiInfo.getRequestHeaders());
                if (headersNode.isObject()) {
                    headersNode.fields().forEachRemaining(entry -> {
                        headers.put(entry.getKey(), entry.getValue().asText());
                    });
                }
            } catch (Exception e) {
                log.warn("解析接口默认请求头失败: {}", e.getMessage());
            }
        }

        // 2.2 应用用例的 request_override 中的 headers
        if (executionDTO.getRequestOverride() != null && !executionDTO.getRequestOverride().trim().isEmpty()) {
            try {
                JsonNode overrideNode = objectMapper.readTree(executionDTO.getRequestOverride());
                if (overrideNode.has("headers")) {
                    JsonNode headersOverride = overrideNode.get("headers");
                    headersOverride.fields().forEachRemaining(entry -> {
                        headers.put(entry.getKey(), entry.getValue().asText());
                    });
                }
            } catch (Exception e) {
                log.warn("应用请求头覆盖失败: {}", e.getMessage());
            }
        }

        // 3. 构建请求体
        String body = null;
        
        // 3.1 优先使用 preConditions 作为请求体数据
        String preConditions = executionDTO.getPreConditions();
        log.info("preConditions原始值: {}", preConditions);
        
        if (preConditions != null && !preConditions.trim().isEmpty() && !"null".equals(preConditions)) {
            // preConditions 直接作为请求体
            body = preConditions;
            log.info("✓ 使用preConditions作为请求体: {}", body);
        }
        
        // 3.2 如果有 request_override，用它覆盖请求体
        String requestOverride = executionDTO.getRequestOverride();
        if (requestOverride != null && !requestOverride.trim().isEmpty() && !"null".equals(requestOverride)) {
            try {
                JsonNode overrideNode = objectMapper.readTree(requestOverride);
                
                if (overrideNode.has("body")) {
                    // request_override.body 会完全替换 preConditions
                    JsonNode bodyNode = overrideNode.get("body");
                    body = objectMapper.writeValueAsString(bodyNode);
                    log.info("✓ request_override.body 覆盖了preConditions: {}", body);
                }
            } catch (Exception e) {
                log.warn("解析request_override失败: {}", e.getMessage());
            }
        }
        
        // 3.3 如果都没有，使用接口默认请求体
        if (body == null) {
            String apiBody = apiInfo.getRequestBody();
            if (apiBody != null && !apiBody.trim().isEmpty() && !"null".equals(apiBody)) {
                body = apiBody;
                log.info("✓ 使用接口默认请求体: {}", body);
            }
        }
        
        // 3.4 应用变量替换
        if (body != null) {
            body = applyVariables(body, executionDTO.getVariables());
        } else {
            log.warn("⚠️ 最终请求体为null（对于GET请求这是正常的）");
        }

        // 4. 设置超时时间
        int timeout = apiInfo.getTimeoutSeconds() != null ? apiInfo.getTimeoutSeconds() : 30;

        log.info("最终请求URL: {}", fullUrl);
        log.info("最终请求头: {}", headers);
        log.info("最终请求体: {}", body);

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
