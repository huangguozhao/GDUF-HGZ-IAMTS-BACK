package com.victor.iatms.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.iatms.entity.dto.*;
import com.victor.iatms.entity.po.Api;
import com.victor.iatms.entity.po.AITestCaseGeneration;
import com.victor.iatms.entity.po.TestCase;
import com.victor.iatms.mappers.AITestCaseGenerationMapper;
import com.victor.iatms.mappers.TestExecutionMapper;
import com.victor.iatms.mappers.TestCaseMapper;
import com.victor.iatms.service.AITestCaseService;
import com.victor.iatms.utils.DeepSeekUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AITestCaseServiceImpl implements AITestCaseService {

    @Autowired
    private AITestCaseGenerationMapper generationMapper;

    @Autowired
    private TestExecutionMapper testExecutionMapper;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private DeepSeekUtils deepSeekUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private static final int MAX_CACHE_SIZE = 100;
    private final Map<Long, Map<String, Object>> resultCache = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> statusCache = new ConcurrentHashMap<>();

    @Override
    public AITestCaseResultDTO generateTestCases(AITestCaseGenerateDTO dto, Integer userId) {
        AITestCaseGeneration generation = createGenerationRecord(dto, userId);
        Long generationId = generation.getGenerationId();

        AITestCaseResultDTO initialResult = new AITestCaseResultDTO();
        initialResult.setGenerationId(generationId);
        initialResult.setStatus("processing");
        initialResult.setCaseCount(0);

        cacheResult(generationId, initialResult);

        generationMapper.updateStatus(generationId, "processing", null, 0, null, null);

        callDeepSeekGenerateAsync(generationId, dto, userId);

        return initialResult;
    }

    private void cacheResult(Long generationId, AITestCaseResultDTO result) {
        if (resultCache.size() >= MAX_CACHE_SIZE) {
            Long oldestKey = resultCache.keySet().iterator().next();
            resultCache.remove(oldestKey);
            statusCache.remove(oldestKey);
        }
        Map<String, Object> cacheData = new HashMap<>();
        cacheData.put("status", result.getStatus());
        cacheData.put("cases", result.getCases());
        cacheData.put("caseCount", result.getCaseCount());
        cacheData.put("errorMessage", result.getErrorMessage());
        resultCache.put(generationId, cacheData);
        statusCache.put(generationId, "completed".equals(result.getStatus()));
    }

    private void callDeepSeekGenerateAsync(Long generationId, AITestCaseGenerateDTO dto, Integer userId) {
        CompletableFuture.runAsync(() -> {
            try {
                log.info("开始异步调用DeepSeek API生成测试用例, generationId={}", generationId);

                Api api = null;
                if (dto.getApiId() != null) {
                    api = testExecutionMapper.findApiById(dto.getApiId());
                }

                String systemPrompt = buildSystemPrompt();
                String userPrompt = buildUserPrompt(api, dto);

                log.info("AI生成测试用例 - 系统提示词长度: {}, 用户提示词长度: {}",
                        systemPrompt.length(), userPrompt.length());

                String aiResponse = deepSeekUtils.chat(
                    List.of(Map.of("role", "user", "content", userPrompt)),
                    systemPrompt
                );

                if (aiResponse == null || aiResponse.isEmpty()) {
                    log.error("DeepSeek API返回为空, generationId={}", generationId);
                    updateGenerationFailed(generationId, "AI返回结果为空");
                    return;
                }

                List<GeneratedTestCaseDTO> cases = parseAIResponse(aiResponse);

                String casesJson = objectMapper.writeValueAsString(cases);
                generationMapper.updateStatus(generationId, "completed",
                        casesJson, cases.size(), LocalDateTime.now(), null);

                Map<String, Object> cacheData = resultCache.get(generationId);
                if (cacheData != null) {
                    cacheData.put("status", "completed");
                    cacheData.put("cases", cases);
                    cacheData.put("caseCount", cases.size());
                }
                statusCache.put(generationId, true);

                log.info("AI生成测试用例完成, generationId={}, caseCount={}", generationId, cases.size());

            } catch (Exception e) {
                log.error("异步AI生成测试用例异常, generationId={}", generationId, e);
                updateGenerationFailed(generationId, e.getMessage());
            }
        });
    }

    private void updateGenerationFailed(Long generationId, String errorMessage) {
        try {
            generationMapper.updateStatus(generationId, "failed",
                    null, 0, LocalDateTime.now(), errorMessage);

            Map<String, Object> cacheData = resultCache.get(generationId);
            if (cacheData != null) {
                cacheData.put("status", "failed");
                cacheData.put("errorMessage", errorMessage);
            }
            statusCache.put(generationId, true);
        } catch (Exception ex) {
            log.error("更新生成状态失败", ex);
        }
    }

    @Override
    public AITestCaseResultDTO getGenerationResult(Long generationId) {
        Map<String, Object> cacheData = resultCache.get(generationId);
        Boolean completed = statusCache.get(generationId);

        if (cacheData != null) {
            AITestCaseResultDTO result = new AITestCaseResultDTO();
            result.setGenerationId(generationId);
            result.setStatus((String) cacheData.get("status"));
            result.setCaseCount((Integer) cacheData.get("caseCount"));
            result.setErrorMessage((String) cacheData.get("errorMessage"));

            @SuppressWarnings("unchecked")
            List<GeneratedTestCaseDTO> cases = (List<GeneratedTestCaseDTO>) cacheData.get("cases");
            result.setCases(cases);

            log.info("获取生成结果(缓存): generationId={}, status={}, caseCount={}",
                    generationId, result.getStatus(), result.getCaseCount());
            return result;
        }

        AITestCaseGeneration generation = generationMapper.findById(generationId);
        if (generation == null) {
            throw new RuntimeException("生成记录不存在");
        }

        AITestCaseResultDTO result = new AITestCaseResultDTO();
        result.setGenerationId(generation.getGenerationId());
        result.setStatus(generation.getStatus());
        result.setCaseCount(generation.getCaseCount());
        result.setErrorMessage(generation.getErrorMessage());
        result.setPromptTokens(generation.getPromptTokens());
        result.setCompletionTokens(generation.getCompletionTokens());

        if (generation.getGeneratedCases() != null) {
            try {
                List<GeneratedTestCaseDTO> cases = objectMapper.readValue(
                    generation.getGeneratedCases(),
                    new TypeReference<List<GeneratedTestCaseDTO>>() {}
                );
                result.setCases(cases);
            } catch (JsonProcessingException e) {
                log.error("解析生成的用例失败", e);
            }
        }

        cacheResult(generationId, result);
        statusCache.put(generationId, "completed".equals(generation.getStatus()));

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Integer> confirmSaveTestCases(ConfirmTestCaseDTO dto, Integer userId) {
        List<Integer> savedCaseIds = new ArrayList<>();

        for (GeneratedTestCaseDTO generatedCase : dto.getCases()) {
            TestCase testCase = convertToEntity(generatedCase, dto.getApiId(), userId);

            String caseCode = generateCaseCode(dto.getApiId());
            testCase.setCaseCode(caseCode);

            testCaseMapper.insert(testCase);
            savedCaseIds.add(testCase.getCaseId());

            log.info("保存AI生成的测试用例: caseId={}, name={}", testCase.getCaseId(), testCase.getName());
        }

        return savedCaseIds;
    }

    private AITestCaseGeneration createGenerationRecord(AITestCaseGenerateDTO dto, Integer userId) {
        AITestCaseGeneration generation = new AITestCaseGeneration();
        generation.setApiId(dto.getApiId());
        generation.setUserId(userId);
        generation.setGenerationType(dto.getGenerationType() != null ? dto.getGenerationType() : "single");
        generation.setStatus("pending");
        generation.setCaseCount(0);
        generation.setCreatedAt(LocalDateTime.now());

        try {
            generation.setGenerationConfig(objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            log.warn("序列化生成配置失败", e);
        }

        generationMapper.insert(generation);
        return generation;
    }

    private String buildSystemPrompt() {
        return """
            你是一个专业的API测试工程师，擅长设计全面、高质量的测试用例。

            请根据提供的API接口信息，生成测试用例。

            ## 输出格式要求
            必须返回JSON数组格式，不要包含任何其他文字说明。每个测试用例包含以下字段：

            ```json
            [
              {
                "name": "用例名称（简洁明了，如：正常登录-成功场景）",
                "description": "用例描述（详细说明测试目的和场景）",
                "testType": "functional（测试类型：functional/performance/security）",
                "priority": "P2（优先级：P0/P1/P2/P3）",
                "severity": "medium（严重程度：critical/high/medium/low）",
                "tags": ["标签1", "标签2"],
                "preConditions": {
                  "description": "前置条件描述",
                  "requiredVariables": [
                    {
                      "name": "token",
                      "sourceType": "api",
                      "extractPath": "$.data.token",
                      "required": true,
                      "description": "认证token"
                    }
                  ],
                  "executionOrder": 1
                },
                "testSteps": [
                  {"step": 1, "action": "操作描述", "expected": "预期结果"}
                ],
                "expectedHttpStatus": 200,
                "expectedResponseBody": {"code": 0, "message": "success"},
                "assertions": [
                  {"type": "equals", "path": "$.code", "expected": 0, "description": "验证返回码"}
                ],
                "requestOverride": "{\"headers\": {\"Authorization\": \"Bearer {{token}}\"}}",
                "extractors": "{\"token\": \"$.data.token\", \"userId\": \"$.data.userId\"}"
              }
            ]
            ```

            ## 重要字段说明

            ### preConditions（前置条件）
            - description: 描述执行此前置条件
            - requiredVariables: 所需变量列表
              - name: 变量名
              - sourceType: 来源类型（api/case/env/manual）
              - sourceId: 来源ID（当sourceType为api或case时）
              - extractPath: JSONPath提取路径（如 $.data.token）
              - required: 是否必须
              - description: 变量描述
            - executionOrder: 执行顺序（数字越小越先执行）

            ### requestOverride（请求覆盖）
            - 使用 {{variableName}} 格式引用变量
            - 示例：{"headers": {"Authorization": "Bearer {{token}}"}}

            ### extractors（变量提取器）
            - 从响应中提取变量供后续用例使用
            - 格式：{"变量名": "JSONPath路径"}
            - 示例：{"token": "$.data.token", "userId": "$.data.userId"}

            ## 测试场景覆盖要求
            1. 正向测试：验证正常业务流程，使用有效参数
            2. 负面测试：异常输入、参数缺失、类型错误、权限不足
            3. 边界测试：空值、最大值、最小值、特殊字符
            4. 安全测试（可选）：SQL注入、XSS、越权访问

            ## 断言类型说明
            - equals: 精确匹配
            - contains: 包含匹配
            - notNull: 非空验证
            - regex: 正则匹配
            - jsonPath: JSON路径验证

            ## 注意事项
            1. 用例名称要能清晰表达测试场景
            2. 测试步骤要具体可执行
            3. 断言要覆盖关键业务逻辑
            4. 根据接口特点设计合适的测试场景
            5. 如果接口需要认证，在preConditions中声明token依赖
            6. 在requestOverride中使用{{token}}引用变量
            """;
    }

    private String buildUserPrompt(Api api, AITestCaseGenerateDTO dto) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("## 请生成测试用例\n\n");

        if (api != null) {
            prompt.append("### API接口信息\n");
            prompt.append("- 接口名称: ").append(api.getName() != null ? api.getName() : "未命名").append("\n");
            prompt.append("- 请求方法: ").append(api.getMethod() != null ? api.getMethod() : "GET").append("\n");
            prompt.append("- 接口路径: ").append(api.getPath() != null ? api.getPath() : "/").append("\n");

            if (api.getDescription() != null && !api.getDescription().isEmpty()) {
                prompt.append("- 接口描述: ").append(api.getDescription()).append("\n");
            }

            if (api.getRequestParameters() != null && !api.getRequestParameters().isEmpty()) {
                prompt.append("- 请求参数: ").append(api.getRequestParameters()).append("\n");
            }

            if (api.getRequestBody() != null && !api.getRequestBody().isEmpty()) {
                prompt.append("- 请求体: ").append(api.getRequestBody()).append("\n");
            }

            if (api.getRequestHeaders() != null && !api.getRequestHeaders().isEmpty()) {
                prompt.append("- 请求头: ").append(api.getRequestHeaders()).append("\n");
            }

            if (api.getExamples() != null && !api.getExamples().isEmpty()) {
                prompt.append("- 请求示例: ").append(api.getExamples()).append("\n");
            }
        }

        if (dto.getRequirement() != null && !dto.getRequirement().isEmpty()) {
            prompt.append("\n### 需求描述\n");
            prompt.append(dto.getRequirement()).append("\n");
        }

        prompt.append("\n### 生成配置\n");
        prompt.append("- 最大生成数量: ").append(dto.getMaxCases() != null ? dto.getMaxCases() : 5).append("\n");
        prompt.append("- 包含负面测试: ").append(dto.getIncludeNegative() != null ? dto.getIncludeNegative() : true).append("\n");
        prompt.append("- 包含边界测试: ").append(dto.getIncludeBoundary() != null ? dto.getIncludeBoundary() : true).append("\n");

        if (dto.getPriority() != null) {
            prompt.append("- 默认优先级: ").append(dto.getPriority()).append("\n");
        }

        prompt.append("\n请根据以上信息生成").append(dto.getMaxCases() != null ? dto.getMaxCases() : 5).append("个测试用例，直接返回JSON数组：\n");

        return prompt.toString();
    }

    private List<GeneratedTestCaseDTO> parseAIResponse(String aiResponse) {
        if (aiResponse == null || aiResponse.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            String jsonContent = extractJsonArray(aiResponse);

            List<GeneratedTestCaseDTO> cases = objectMapper.readValue(
                jsonContent,
                new TypeReference<List<GeneratedTestCaseDTO>>() {}
            );

            for (GeneratedTestCaseDTO testCase : cases) {
                if (testCase.getPriority() == null) {
                    testCase.setPriority("P2");
                }
                if (testCase.getSeverity() == null) {
                    testCase.setSeverity("medium");
                }
                if (testCase.getTestType() == null) {
                    testCase.setTestType("functional");
                }
                if (testCase.getTags() == null) {
                    testCase.setTags(new ArrayList<>());
                }
                if (testCase.getTestSteps() == null) {
                    testCase.setTestSteps(new ArrayList<>());
                }
                if (testCase.getAssertions() == null) {
                    testCase.setAssertions(new ArrayList<>());
                }
                if (testCase.getSelected() == null) {
                    testCase.setSelected(true);
                }
            }

            return cases;

        } catch (Exception e) {
            log.error("解析AI响应失败: {}", e.getMessage());
            log.error("AI响应内容: {}", aiResponse);
            throw new RuntimeException("解析AI响应失败: " + e.getMessage());
        }
    }

    private String extractJsonArray(String content) {
        int startIndex = content.indexOf('[');
        int endIndex = content.lastIndexOf(']');

        if (startIndex == -1 || endIndex == -1 || startIndex > endIndex) {
            throw new RuntimeException("AI响应中未找到有效的JSON数组");
        }

        return content.substring(startIndex, endIndex + 1);
    }

    private TestCase convertToEntity(GeneratedTestCaseDTO dto, Integer apiId, Integer userId) {
        TestCase testCase = new TestCase();

        testCase.setApiId(apiId);
        testCase.setName(dto.getName());
        testCase.setDescription(dto.getDescription());
        testCase.setTestType(dto.getTestType());
        testCase.setPriority(dto.getPriority());
        testCase.setSeverity(dto.getSeverity());

        if (dto.getTags() != null) {
            try {
                testCase.setTags(objectMapper.writeValueAsString(dto.getTags()));
            } catch (JsonProcessingException e) {
                testCase.setTags("[]");
            }
        }

        // 处理前置条件（支持对象格式）
        if (dto.getPreConditions() != null) {
            try {
                testCase.setPreConditions(objectMapper.writeValueAsString(dto.getPreConditions()));
            } catch (JsonProcessingException e) {
                testCase.setPreConditions(null);
            }
        }

        if (dto.getTestSteps() != null) {
            try {
                testCase.setTestSteps(objectMapper.writeValueAsString(dto.getTestSteps()));
            } catch (JsonProcessingException e) {
                testCase.setTestSteps("[]");
            }
        }

        testCase.setExpectedHttpStatus(dto.getExpectedHttpStatus());

        if (dto.getExpectedResponseBody() != null) {
            try {
                testCase.setExpectedResponseBody(objectMapper.writeValueAsString(dto.getExpectedResponseBody()));
            } catch (JsonProcessingException e) {
                testCase.setExpectedResponseBody("{}");
            }
        }

        if (dto.getAssertions() != null) {
            try {
                testCase.setAssertions(objectMapper.writeValueAsString(dto.getAssertions()));
            } catch (JsonProcessingException e) {
                testCase.setAssertions("[]");
            }
        }

        // 处理请求覆盖配置（支持字符串JSON格式）
        if (dto.getRequestOverride() != null && !dto.getRequestOverride().isEmpty()) {
            testCase.setRequestOverride(dto.getRequestOverride());
        }

        // 处理变量提取器
        if (dto.getExtractors() != null && !dto.getExtractors().isEmpty()) {
            testCase.setExtractors(dto.getExtractors());
        }

        testCase.setIsEnabled(true);
        testCase.setIsTemplate(false);
        testCase.setVersion("1.0");
        testCase.setCreatedBy(userId);
        testCase.setCreatedAt(LocalDateTime.now());
        testCase.setIsDeleted(false);

        return testCase;
    }

    private String generateCaseCode(Integer apiId) {
        Integer maxSeq = testCaseMapper.getMaxCaseSeqByApiId(apiId);
        int nextSeq = (maxSeq != null ? maxSeq : 0) + 1;
        return String.format("TC-API-%d-%03d", apiId, nextSeq);
    }

    private String convertToJsonString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            objectMapper.readTree(value);
            return value;
        } catch (Exception e) {
            try {
                return objectMapper.writeValueAsString(Collections.singletonMap("description", value));
            } catch (JsonProcessingException ex) {
                return null;
            }
        }
    }
}
