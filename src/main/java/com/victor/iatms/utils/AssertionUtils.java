package com.victor.iatms.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 断言工具类
 */
@Slf4j
@Component
public class AssertionUtils {

    private final ObjectMapper objectMapper;

    public AssertionUtils() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 执行断言
     * @param assertions 断言规则JSON字符串
     * @param responseBody 响应体
     * @param responseStatus HTTP状态码
     * @param responseHeaders 响应头
     * @return 断言结果列表
     */
    public List<AssertionResult> executeAssertions(String assertions, String responseBody, 
                                                  int responseStatus, Map<String, String> responseHeaders) {
        List<AssertionResult> results = new ArrayList<>();
        
        if (assertions == null || assertions.trim().isEmpty()) {
            return results;
        }

        try {
            JsonNode assertionNode = objectMapper.readTree(assertions);
            
            if (assertionNode.isArray()) {
                for (JsonNode assertion : assertionNode) {
                    AssertionResult result = executeSingleAssertion(assertion, responseBody, responseStatus, responseHeaders);
                    results.add(result);
                }
            } else {
                AssertionResult result = executeSingleAssertion(assertionNode, responseBody, responseStatus, responseHeaders);
                results.add(result);
            }
            
        } catch (Exception e) {
            log.error("执行断言失败: {}", e.getMessage(), e);
            AssertionResult errorResult = new AssertionResult();
            errorResult.setAssertionType("parse_error");
            errorResult.setPassed(false);
            errorResult.setErrorMessage("断言规则解析失败: " + e.getMessage());
            results.add(errorResult);
        }

        return results;
    }

    /**
     * 执行单个断言
     */
    private AssertionResult executeSingleAssertion(JsonNode assertion, String responseBody, 
                                                  int responseStatus, Map<String, String> responseHeaders) {
        AssertionResult result = new AssertionResult();
        
        try {
            String type = assertion.has("type") ? assertion.get("type").asText() : "equals";
            result.setAssertionType(type);
            
            // 处理不同类型的断言
            if ("status_code".equals(type)) {
                // HTTP状态码断言
                int expectedStatus = assertion.get("expected").asInt();
                result.setExpectedValue(String.valueOf(expectedStatus));
                result.setActualValue(String.valueOf(responseStatus));
                result.setPassed(expectedStatus == responseStatus);
                
                if (!result.isPassed()) {
                    result.setErrorMessage(String.format("状态码不匹配: 期望 %d，实际 %d", expectedStatus, responseStatus));
                }
                
            } else if ("json_path".equals(type)) {
                // JSON路径断言
                String path = assertion.get("path").asText();
                String expectedValue = assertion.get("expected").asText();
                
                String actualValue = extractJsonValueByPath(responseBody, path);
                
                result.setExpectedValue(expectedValue);
                result.setActualValue(actualValue);
                result.setPassed(expectedValue.equals(actualValue));
                
                if (!result.isPassed()) {
                    result.setErrorMessage(String.format("字段 %s 值不匹配: 期望 %s，实际 %s", path, expectedValue, actualValue));
                }
                
            } else if ("json_path_exists".equals(type)) {
                // JSON路径存在性断言
                String path = assertion.get("path").asText();
                
                String actualValue = extractJsonValueByPath(responseBody, path);
                boolean exists = actualValue != null && !actualValue.isEmpty();
                
                result.setExpectedValue("字段存在");
                result.setActualValue(exists ? "存在" : "不存在");
                result.setPassed(exists);
                
                if (!result.isPassed()) {
                    result.setErrorMessage(String.format("字段 %s 不存在", path));
                }
                
            } else {
                // 原有逻辑（兼容旧格式）
                String target = assertion.has("target") ? assertion.get("target").asText() : "status";
                String expectedValue = assertion.has("expected") ? assertion.get("expected").asText() : "";
                
                result.setExpectedValue(expectedValue);
                
                String actualValue = getActualValue(target, responseBody, responseStatus, responseHeaders);
                result.setActualValue(actualValue);
                
                boolean passed = evaluateAssertion(type, expectedValue, actualValue);
                result.setPassed(passed);
                
                if (!passed) {
                    result.setErrorMessage(String.format("断言失败: 期望 %s，实际 %s", expectedValue, actualValue));
                }
            }
            
        } catch (Exception e) {
            log.error("执行单个断言失败: {}", e.getMessage(), e);
            result.setPassed(false);
            result.setErrorMessage("断言执行异常: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 根据JSON路径提取值
     */
    private String extractJsonValueByPath(String jsonBody, String jsonPath) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonBody);
            
            // 转换简单的点路径为JsonPointer格式
            // 例如：$.code -> /code,  $.data.token -> /data/token
            String pointerPath = jsonPath.replace("$.", "/").replace(".", "/");
            if (!pointerPath.startsWith("/")) {
                pointerPath = "/" + pointerPath;
            }
            
            JsonNode targetNode = rootNode.at(pointerPath);
            
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
            log.warn("提取JSON路径值失败: path={}, error={}", jsonPath, e.getMessage());
            return "";
        }
    }

    /**
     * 获取实际值
     */
    private String getActualValue(String target, String responseBody, int responseStatus, Map<String, String> responseHeaders) {
        switch (target.toLowerCase()) {
            case "status":
                return String.valueOf(responseStatus);
            case "body":
                return responseBody;
            case "json":
                return extractJsonValue(responseBody, "$");
            default:
                if (target.startsWith("json:")) {
                    String jsonPath = target.substring(5);
                    return extractJsonValue(responseBody, jsonPath);
                } else if (target.startsWith("header:")) {
                    String headerName = target.substring(7);
                    return responseHeaders.getOrDefault(headerName, "");
                } else {
                    return responseBody;
                }
        }
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
     * 评估断言
     */
    private boolean evaluateAssertion(String type, String expectedValue, String actualValue) {
        switch (type.toLowerCase()) {
            case "equals":
            case "equal":
                return expectedValue.equals(actualValue);
            case "contains":
                return actualValue.contains(expectedValue);
            case "not_contains":
                return !actualValue.contains(expectedValue);
            case "regex":
                return actualValue.matches(expectedValue);
            case "greater_than":
                return compareNumbers(actualValue, expectedValue) > 0;
            case "less_than":
                return compareNumbers(actualValue, expectedValue) < 0;
            case "greater_equal":
                return compareNumbers(actualValue, expectedValue) >= 0;
            case "less_equal":
                return compareNumbers(actualValue, expectedValue) <= 0;
            case "not_equal":
                return !expectedValue.equals(actualValue);
            case "is_empty":
                return actualValue == null || actualValue.trim().isEmpty();
            case "is_not_empty":
                return actualValue != null && !actualValue.trim().isEmpty();
            default:
                return expectedValue.equals(actualValue);
        }
    }

    /**
     * 比较数字
     */
    private int compareNumbers(String actual, String expected) {
        try {
            double actualNum = Double.parseDouble(actual);
            double expectedNum = Double.parseDouble(expected);
            return Double.compare(actualNum, expectedNum);
        } catch (NumberFormatException e) {
            return actual.compareTo(expected);
        }
    }

    /**
     * 断言结果类
     */
    public static class AssertionResult {
        private String assertionType;
        private String expectedValue;
        private String actualValue;
        private boolean passed;
        private String errorMessage;

        // Getters and Setters
        public String getAssertionType() {
            return assertionType;
        }

        public void setAssertionType(String assertionType) {
            this.assertionType = assertionType;
        }

        public String getExpectedValue() {
            return expectedValue;
        }

        public void setExpectedValue(String expectedValue) {
            this.expectedValue = expectedValue;
        }

        public String getActualValue() {
            return actualValue;
        }

        public void setActualValue(String actualValue) {
            this.actualValue = actualValue;
        }

        public boolean isPassed() {
            return passed;
        }

        public void setPassed(boolean passed) {
            this.passed = passed;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
