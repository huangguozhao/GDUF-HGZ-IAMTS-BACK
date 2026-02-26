package com.victor.iatms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.iatms.entity.dto.AIDiagnosisRequestDTO;
import com.victor.iatms.entity.dto.AIDiagnosisResponseDTO;
import com.victor.iatms.entity.dto.DiagnosisContextDTO;
import com.victor.iatms.entity.po.TestCase;
import com.victor.iatms.entity.po.TestCaseResult;
import com.victor.iatms.entity.po.Api;
import com.victor.iatms.entity.po.Module;
import com.victor.iatms.entity.po.Project;
import com.victor.iatms.mappers.TestCaseMapper;
import com.victor.iatms.mappers.TestExecutionMapper;
import com.victor.iatms.mappers.ApiMapper;
import com.victor.iatms.mappers.ModuleMapper;
import com.victor.iatms.mappers.ProjectMapper;
import com.victor.iatms.service.AIDiagnosisService;
import com.victor.iatms.utils.DeepSeekUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * AI诊断服务实现类
 */
@Slf4j
@Service
public class AIDiagnosisServiceImpl implements AIDiagnosisService {

    @Autowired
    private DeepSeekUtils deepSeekUtils;

    @Autowired
    private TestExecutionMapper testExecutionMapper;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private ApiMapper apiMapper;

    @Autowired
    private ModuleMapper moduleMapper;

    @Autowired
    private ProjectMapper projectMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${deepseek.diagnosis.enabled:true}")
    private boolean diagnosisEnabled;

    /**
     * 测试失败诊断的系统提示词
     */
    private static final String TEST_FAILURE_PROMPT = """ 
        你是一个专业的测试工程师和API测试专家。你的任务是根据提供的测试失败信息，分析并诊断问题原因。
        
        请根据以下测试失败信息，进行分析：
        1. 错误信息分析
        2. 可能的原因分析
        3. 建议的解决方案
        
        请按以下JSON格式返回诊断结果：
        {
            "result": "总体诊断结果描述",
            "rootCause": "根本原因分析",
            "suggestedFix": "建议的修复方案",
            "possibleCauses": ["可能原因1", "可能原因2", "可能原因3"],
            "improvementSuggestions": ["改进建议1", "改进建议2"],
            "confidenceScore": 85
        }
        
        请确保返回有效的JSON格式，不要包含其他内容。
        """;

    /**
     * 性能问题诊断的系统提示词
     */
    private static final String PERFORMANCE_PROMPT = """
        你是一个专业的性能测试工程师。你的任务是根据提供的性能数据，分析并诊断性能问题。
        
        请根据以下性能数据，进行分析：
        1. 性能瓶颈分析
        2. 可能的原因分析
        3. 优化建议
        
        请按以下JSON格式返回诊断结果：
        {
            "result": "总体性能诊断结果",
            "rootCause": "性能瓶颈根本原因",
            "suggestedFix": "优化建议",
            "possibleCauses": ["原因1", "原因2"],
            "improvementSuggestions": ["优化建议1", "优化建议2"],
            "confidenceScore": 80
        }
        
        请确保返回有效的JSON格式，不要包含其他内容。
        """;

    /**
     * 错误日志诊断的系统提示词
     */
    private static final String ERROR_LOG_PROMPT = """
        你是一个专业的系统运维工程师和调试专家。你的任务是根据提供的错误日志，分析并诊断系统问题。
        
        请根据以下错误日志，进行分析：
        1. 错误类型识别
        2. 问题定位
        3. 解决方案
        
        请按以下JSON格式返回诊断结果：
        {
            "result": "总体诊断结果",
            "rootCause": "问题根本原因",
            "suggestedFix": "解决方案",
            "possibleCauses": ["原因1", "原因2"],
            "improvementSuggestions": ["建议1", "建议2"],
            "confidenceScore": 90
        }
        
        请确保返回有效的JSON格式，不要包含其他内容。
        """;

    @Override
    public AIDiagnosisResponseDTO diagnose(AIDiagnosisRequestDTO request) {
        if (!diagnosisEnabled) {
            return buildErrorResponse("AI诊断功能未启用");
        }

        // 如果提供了executionId，优先使用完整数据诊断
        if (request.getExecutionId() != null) {
            return diagnoseByExecutionId(request.getExecutionId(), null);
        }

        String diagnosisType = request.getDiagnosisType();
        if (diagnosisType == null) {
            diagnosisType = "test_failure";
        }

        return switch (diagnosisType) {
            case "test_failure" -> diagnoseTestFailure(
                    request.getErrorMessage(),
                    request.getErrorLog(),
                    request.getDescription()
            );
            case "performance" -> diagnosePerformance(
                    request.getContext() != null ? request.getContext().toString() : null,
                    request.getDescription()
            );
            case "error_log" -> diagnoseErrorLog(
                    request.getErrorLog(),
                    request.getDescription()
            );
            default -> diagnoseTestFailure(
                    request.getErrorMessage(),
                    request.getErrorLog(),
                    request.getDescription()
            );
        };
    }

    @Override
    public AIDiagnosisResponseDTO diagnoseByExecutionId(Long executionId, Integer userId) {
        if (!diagnosisEnabled) {
            return buildErrorResponse("AI诊断功能未启用");
        }

        if (executionId == null) {
            return buildErrorResponse("执行记录ID不能为空");
        }

        try {
            // 1. 查询完整的测试执行数据
            DiagnosisContextDTO context = buildDiagnosisContext(executionId);
            if (context == null) {
                return buildErrorResponse("无法获取执行记录数据，记录ID: " + executionId);
            }

            // 2. 构建发送给大模型的提示词
            String userMessage = buildDiagnosisPrompt(context);

            // 3. 调用大模型进行诊断
            String aiResponse = deepSeekUtils.chat(TEST_FAILURE_PROMPT, userMessage);

            if (aiResponse == null || aiResponse.isEmpty()) {
                return buildErrorResponse("AI诊断服务调用失败");
            }

            // 4. 解析返回结果
            AIDiagnosisResponseDTO result = parseAIResponse(aiResponse, "test_failure");
            result.setExecutionId(executionId);
            return result;

        } catch (Exception e) {
            log.error("AI诊断异常，执行记录ID: {}", executionId, e);
            return buildErrorResponse("诊断过程发生异常: " + e.getMessage());
        }
    }

    /**
     * 构建诊断上下文数据
     */
    private DiagnosisContextDTO buildDiagnosisContext(Long executionId) {
        try {
            DiagnosisContextDTO context = new DiagnosisContextDTO();
            context.setExecutionId(executionId);

            // 1. 查询测试执行结果
            TestCaseResult testResult = testExecutionMapper.findTestCaseResultByExecutionId(executionId);
            
            // 判断是单个用例还是接口/模块级别的执行
            // 如果是接口级别执行，会返回null，需要通过其他方式查询
            if (testResult == null) {
                // 尝试查询是否是接口级别的执行记录
                return buildApiLevelDiagnosisContext(executionId);
            }

            // 处理单个用例级别的执行结果
            return buildSingleCaseDiagnosisContext(executionId, testResult);

        } catch (Exception e) {
            log.error("构建诊断上下文失败，执行记录ID: {}", executionId, e);
            return null;
        }
    }
    
    /**
     * 构建接口/模块级别的诊断上下文（多条用例）
     */
    private DiagnosisContextDTO buildApiLevelDiagnosisContext(Long executionId) {
        try {
            DiagnosisContextDTO context = new DiagnosisContextDTO();
            context.setExecutionId(executionId);
            
            // 1. 查询该执行ID下的所有用例结果
            List<TestCaseResult> allCaseResults = testExecutionMapper.findTestCaseResultsByExecutionId(executionId);
            
            if (allCaseResults == null || allCaseResults.isEmpty()) {
                log.warn("未找到执行记录: {}", executionId);
                return null;
            }
            
            // 2. 统计执行情况
            int totalCases = allCaseResults.size();
            int passed = 0;
            int failed = 0;
            int skipped = 0;
            
            List<DiagnosisContextDTO.AssertionDetailDTO> allAssertions = new ArrayList<>();
            StringBuilder failureSummary = new StringBuilder();
            StringBuilder allFailureTraces = new StringBuilder();
            
            for (TestCaseResult caseResult : allCaseResults) {
                String status = caseResult.getStatus();
                if ("passed".equals(status)) {
                    passed++;
                } else if ("failed".equals(status)) {
                    failed++;
                    // 收集失败信息
                    if (caseResult.getFailureMessage() != null) {
                        failureSummary.append("【用例: ").append(caseResult.getFullName()).append("】\n");
                        failureSummary.append("失败原因: ").append(caseResult.getFailureMessage()).append("\n\n");
                    }
                    if (caseResult.getFailureTrace() != null) {
                        allFailureTraces.append("【用例: ").append(caseResult.getFullName()).append("】\n");
                        allFailureTraces.append(caseResult.getFailureTrace()).append("\n\n");
                    }
                } else if ("skipped".equals(status)) {
                    skipped++;
                }
            }
            
            // 3. 设置统计信息
            context.setTotalCases(totalCases);
            context.setAssertionsPassed(passed);
            context.setAssertionsFailed(failed);
            context.setStatus(failed > 0 ? "failed" : "passed");
            context.setFailureMessage(failureSummary.length() > 0 ? failureSummary.toString() : null);
            context.setFailureTrace(allFailureTraces.length() > 0 ? allFailureTraces.toString() : null);
            
            // 4. 计算成功率
            if (totalCases > 0) {
                context.setSuccessRate((passed * 100.0) / totalCases);
            }
            
            // 5. 获取第一条用例的信息作为参考（接口信息等）
            if (!allCaseResults.isEmpty()) {
                TestCaseResult firstResult = allCaseResults.get(0);
                
                // 查询用例详情
                if (firstResult.getCaseId() != null) {
                    TestCase testCase = testCaseMapper.selectById(firstResult.getCaseId());
                    if (testCase != null) {
                        context.setCaseName(testCase.getName());
                        context.setCaseCode(testCase.getCaseCode());
                        context.setCaseDescription(testCase.getDescription());
                        context.setPriority(testCase.getPriority());
                        
                        // 查询接口信息
                        if (testCase.getApiId() != null) {
                            Api api = apiMapper.selectById(testCase.getApiId());
                            if (api != null) {
                                context.setApiId(api.getApiId());
                                context.setApiName(api.getName());
                                context.setApiPath(api.getPath());
                                context.setMethod(api.getMethod());
                                String fullUrl = (api.getBaseUrl() != null ? api.getBaseUrl() : "") + 
                                                (api.getPath() != null ? api.getPath() : "");
                                context.setUrl(fullUrl);
                                context.setRequestBody(api.getRequestBody());
                                
                                // 查询模块和项目
                                if (api.getModuleId() != null) {
                                    Module module = moduleMapper.selectById(api.getModuleId());
                                    if (module != null) {
                                        context.setModuleName(module.getName());
                                        if (module.getProjectId() != null) {
                                            Project project = projectMapper.selectById(module.getProjectId());
                                            if (project != null) {
                                                context.setProjectName(project.getName());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                // 执行时间和耗时
                context.setStartTime(firstResult.getStartTime());
                context.setEndTime(firstResult.getEndTime());
                Long totalDuration = 0L;
                for (TestCaseResult r : allCaseResults) {
                    if (r.getDuration() != null) {
                        totalDuration += r.getDuration();
                    }
                }
                context.setDuration(totalDuration);
            }
            
            // 6. 设置用例列表信息
            context.setCaseResultsSummary(generateCaseResultsSummary(allCaseResults));
            
            return context;

        } catch (Exception e) {
            log.error("构建接口级别诊断上下文失败，执行记录ID: {}", executionId, e);
            return null;
        }
    }
    
    /**
     * 生成用例结果摘要
     */
    private String generateCaseResultsSummary(List<TestCaseResult> caseResults) {
        StringBuilder summary = new StringBuilder();
        summary.append("【用例执行详情（共").append(caseResults.size()).append("个用例）】\n\n");
        
        for (int i = 0; i < caseResults.size(); i++) {
            TestCaseResult result = caseResults.get(i);
            summary.append("用例").append(i + 1).append(": ").append(result.getFullName()).append("\n");
            summary.append("  状态: ").append(result.getStatus()).append("\n");
            summary.append("  耗时: ").append(result.getDuration() != null ? result.getDuration() + "ms" : "-").append("\n");
            if (result.getFailureMessage() != null) {
                summary.append("  失败原因: ").append(result.getFailureMessage()).append("\n");
            }
            summary.append("\n");
        }
        
        return summary.toString();
    }

    /**
     * 构建诊断上下文数据 - 单个用例级别
     */
    private DiagnosisContextDTO buildSingleCaseDiagnosisContext(Long executionId, TestCaseResult testResult) {
        try {
            DiagnosisContextDTO context = new DiagnosisContextDTO();
            context.setExecutionId(executionId);

            // 设置执行结果信息
            context.setCaseId(testResult.getCaseId());
            context.setCaseName(testResult.getCaseName());
            context.setCaseCode(testResult.getCaseCode());
            context.setStatus(testResult.getStatus());
            context.setFailureMessage(testResult.getFailureMessage());
            context.setFailureType(testResult.getFailureType());
            context.setFailureTrace(testResult.getFailureTrace());
            context.setDuration(testResult.getDuration());
            context.setStartTime(testResult.getStartTime());
            context.setEndTime(testResult.getEndTime());

            // 2. 查询测试用例详情
            if (testResult.getCaseId() != null) {
                TestCase testCase = testCaseMapper.selectById(testResult.getCaseId());
                if (testCase != null) {
                    context.setCaseDescription(testCase.getDescription());
                    context.setPriority(testCase.getPriority());
                    context.setSeverity(testCase.getSeverity());

                    // 解析标签
                    if (testCase.getTags() != null) {
                        context.setTags(Arrays.asList(testCase.getTags().split(",")));
                    }

                    // 查询接口信息
                    if (testCase.getApiId() != null) {
                        Api api = apiMapper.selectById(testCase.getApiId());
                        if (api != null) {
                            context.setApiId(api.getApiId());
                            context.setApiName(api.getName());
                            context.setApiPath(api.getPath());
                            context.setMethod(api.getMethod());
                            // 完整URL = baseUrl + path
                            String fullUrl = (api.getBaseUrl() != null ? api.getBaseUrl() : "") + (api.getPath() != null ? api.getPath() : "");
                            context.setUrl(fullUrl);
                            context.setRequestHeaders(parseJsonToMap(api.getRequestHeaders()));
                            context.setRequestBody(api.getRequestBody());
                            // Api实体中没有expectedResponseBody和expectedHttpStatus字段，需要从用例中获取

                            // 查询模块信息
                            if (api.getModuleId() != null) {
                                Module module = moduleMapper.selectById(api.getModuleId());
                                if (module != null) {
                                    context.setModuleId(module.getModuleId());
                                    context.setModuleName(module.getName());

                                    // 查询项目信息
                                    if (module.getProjectId() != null) {
                                        Project project = projectMapper.selectById(module.getProjectId());
                                        if (project != null) {
                                            context.setProjectName(project.getName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. 查询历史执行统计（可选，简化处理）
            // 注：历史统计查询需要额外的Mapper方法，这里暂时跳过
            // 如果需要可以后续添加

            return context;

        } catch (Exception e) {
            log.error("构建诊断上下文失败，执行记录ID: {}", executionId, e);
            return null;
        }
    }

    /**
     * 解析JSON字符串为Map
     */
    private Map<String, String> parseJsonToMap(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            log.warn("解析JSON失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 构建诊断提示词
     */
    private String buildDiagnosisPrompt(DiagnosisContextDTO context) {
        StringBuilder prompt = new StringBuilder();

        // 1. 测试概览
        prompt.append("【测试概览】\n");
        prompt.append("执行记录ID: ").append(context.getExecutionId()).append("\n");
        prompt.append("用例名称: ").append(context.getCaseName() != null ? context.getCaseName() : "无").append("\n");
        prompt.append("用例编码: ").append(context.getCaseCode() != null ? context.getCaseCode() : "无").append("\n");
        prompt.append("用例描述: ").append(context.getCaseDescription() != null ? context.getCaseDescription() : "无").append("\n");
        prompt.append("优先级: ").append(context.getPriority() != null ? context.getPriority() : "无").append("\n");
        prompt.append("严重程度: ").append(context.getSeverity() != null ? context.getSeverity() : "无").append("\n");
        if (context.getTags() != null && !context.getTags().isEmpty()) {
            prompt.append("标签: ").append(String.join(", ", context.getTags())).append("\n");
        }
        prompt.append("\n");

        // 2. 接口信息
        prompt.append("【接口信息】\n");
        prompt.append("接口名称: ").append(context.getApiName() != null ? context.getApiName() : "无").append("\n");
        prompt.append("接口路径: ").append(context.getApiPath() != null ? context.getApiPath() : "无").append("\n");
        prompt.append("请求方法: ").append(context.getMethod() != null ? context.getMethod() : "无").append("\n");
        prompt.append("请求URL: ").append(context.getUrl() != null ? context.getUrl() : "无").append("\n");
        prompt.append("期望HTTP状态码: ").append(context.getExpectedHttpStatus() != null ? context.getExpectedHttpStatus() : "无").append("\n");
        if (context.getRequestBody() != null && !context.getRequestBody().isEmpty()) {
            prompt.append("请求体: ").append(context.getRequestBody()).append("\n");
        }
        if (context.getExpectedResponseBody() != null && !context.getExpectedResponseBody().isEmpty()) {
            prompt.append("期望响应体: ").append(context.getExpectedResponseBody()).append("\n");
        }
        prompt.append("\n");

        // 3. 项目结构
        prompt.append("【项目结构】\n");
        prompt.append("项目名称: ").append(context.getProjectName() != null ? context.getProjectName() : "无").append("\n");
        prompt.append("模块名称: ").append(context.getModuleName() != null ? context.getModuleName() : "无").append("\n");
        prompt.append("\n");

        // 4. 执行结果
        prompt.append("【执行结果】\n");
        prompt.append("执行状态: ").append(context.getStatus() != null ? context.getStatus() : "无").append("\n");
        
        // 如果是接口/模块级别，显示统计信息
        if (context.getTotalCases() != null && context.getTotalCases() > 0) {
            prompt.append("总用例数: ").append(context.getTotalCases()).append("\n");
            prompt.append("通过: ").append(context.getAssertionsPassed() != null ? context.getAssertionsPassed() : 0).append("\n");
            prompt.append("失败: ").append(context.getAssertionsFailed() != null ? context.getAssertionsFailed() : 0).append("\n");
            prompt.append("成功率: ").append(context.getSuccessRate() != null ? String.format("%.2f%%", context.getSuccessRate()) : "无").append("\n");
        }
        
        prompt.append("执行环境: ").append(context.getEnvironment() != null ? context.getEnvironment() : "无").append("\n");
        prompt.append("执行耗时: ").append(context.getDuration() != null ? context.getDuration() + "ms" : "无").append("\n");
        prompt.append("开始时间: ").append(context.getStartTime() != null ? context.getStartTime() : "无").append("\n");
        prompt.append("结束时间: ").append(context.getEndTime() != null ? context.getEndTime() : "无").append("\n");
        prompt.append("HTTP响应状态码: ").append(context.getResponseStatus() != null ? context.getResponseStatus() : "无").append("\n");
        prompt.append("\n");

        // 5. 失败信息（关键）
        if (context.getFailureMessage() != null && !context.getFailureMessage().isEmpty()) {
            prompt.append("【失败信息】\n");
            prompt.append(context.getFailureMessage()).append("\n\n");
        }
        if (context.getFailureType() != null && !context.getFailureType().isEmpty()) {
            prompt.append("失败类型: ").append(context.getFailureType()).append("\n\n");
        }
        if (context.getFailureTrace() != null && !context.getFailureTrace().isEmpty()) {
            prompt.append("失败堆栈: ").append(context.getFailureTrace()).append("\n\n");
        }

        // 6. 响应体
        if (context.getResponseBody() != null && !context.getResponseBody().isEmpty()) {
            prompt.append("【实际响应体】\n");
            prompt.append(context.getResponseBody()).append("\n\n");
        }

        // 7. 断言结果
        if (context.getAssertionDetails() != null && !context.getAssertionDetails().isEmpty()) {
            prompt.append("【断言详情】\n");
            for (DiagnosisContextDTO.AssertionDetailDTO assertion : context.getAssertionDetails()) {
                prompt.append("- 断言类型: ").append(assertion.getAssertionType()).append("\n");
                prompt.append("  期望值: ").append(assertion.getExpectedValue() != null ? assertion.getExpectedValue() : "无").append("\n");
                prompt.append("  实际值: ").append(assertion.getActualValue() != null ? assertion.getActualValue() : "无").append("\n");
                prompt.append("  结果: ").append(assertion.getPassed() ? "通过" : "失败").append("\n");
                if (assertion.getErrorMessage() != null) {
                    prompt.append("  错误信息: ").append(assertion.getErrorMessage()).append("\n");
                }
            }
            prompt.append("\n");
        }

        // 8. 历史统计
        if (context.getTotalExecutions() != null && context.getTotalExecutions() > 0) {
            prompt.append("【历史执行统计】\n");
            prompt.append("总执行次数: ").append(context.getTotalExecutions()).append("\n");
            prompt.append("失败次数: ").append(context.getTotalFailures()).append("\n");
            prompt.append("最近执行状态: ").append(context.getLastExecutionStatus() != null ? context.getLastExecutionStatus() : "无").append("\n");
            prompt.append("最近执行时间: ").append(context.getLastExecutionTime() != null ? context.getLastExecutionTime() : "无").append("\n");
        }

        // 9. 用例结果摘要（接口/模块级别）
        if (context.getCaseResultsSummary() != null && !context.getCaseResultsSummary().isEmpty()) {
            prompt.append("\n").append(context.getCaseResultsSummary());
        }

        return prompt.toString();
    }

    @Override
    public AIDiagnosisResponseDTO diagnoseTestFailure(String errorMessage, String errorLog, String description) {
        if (!diagnosisEnabled) {
            return buildErrorResponse("AI诊断功能未启用");
        }

        try {
            StringBuilder userMessage = new StringBuilder();
            userMessage.append("【错误信息】\n").append(errorMessage != null ? errorMessage : "无");
            userMessage.append("\n\n【错误日志】\n").append(errorLog != null ? errorLog : "无");
            if (description != null && !description.isEmpty()) {
                userMessage.append("\n\n【问题描述】\n").append(description);
            }

            String aiResponse = deepSeekUtils.chat(TEST_FAILURE_PROMPT, userMessage.toString());

            if (aiResponse == null || aiResponse.isEmpty()) {
                return buildErrorResponse("AI诊断服务调用失败");
            }

            return parseAIResponse(aiResponse, "test_failure");

        } catch (Exception e) {
            log.error("测试失败诊断异常: ", e);
            return buildErrorResponse("诊断过程发生异常: " + e.getMessage());
        }
    }

    @Override
    public AIDiagnosisResponseDTO diagnosePerformance(String performanceData, String description) {
        if (!diagnosisEnabled) {
            return buildErrorResponse("AI诊断功能未启用");
        }

        try {
            StringBuilder userMessage = new StringBuilder();
            userMessage.append("【性能数据】\n").append(performanceData != null ? performanceData : "无");
            if (description != null && !description.isEmpty()) {
                userMessage.append("\n\n【问题描述】\n").append(description);
            }

            String aiResponse = deepSeekUtils.chat(PERFORMANCE_PROMPT, userMessage.toString());

            if (aiResponse == null || aiResponse.isEmpty()) {
                return buildErrorResponse("AI诊断服务调用失败");
            }

            return parseAIResponse(aiResponse, "performance");

        } catch (Exception e) {
            log.error("性能问题诊断异常: ", e);
            return buildErrorResponse("诊断过程发生异常: " + e.getMessage());
        }
    }

    @Override
    public AIDiagnosisResponseDTO diagnoseErrorLog(String errorLog, String description) {
        if (!diagnosisEnabled) {
            return buildErrorResponse("AI诊断功能未启用");
        }

        try {
            StringBuilder userMessage = new StringBuilder();
            userMessage.append("【错误日志】\n").append(errorLog != null ? errorLog : "无");
            if (description != null && !description.isEmpty()) {
                userMessage.append("\n\n【问题描述】\n").append(description);
            }

            String aiResponse = deepSeekUtils.chat(ERROR_LOG_PROMPT, userMessage.toString());

            if (aiResponse == null || aiResponse.isEmpty()) {
                return buildErrorResponse("AI诊断服务调用失败");
            }

            return parseAIResponse(aiResponse, "error_log");

        } catch (Exception e) {
            log.error("错误日志诊断异常: ", e);
            return buildErrorResponse("诊断过程发生异常: " + e.getMessage());
        }
    }

    @Override
    public boolean isAvailable() {
        return diagnosisEnabled && deepSeekUtils != null;
    }

    private AIDiagnosisResponseDTO parseAIResponse(String aiResponse, String diagnosisType) {
        AIDiagnosisResponseDTO response = new AIDiagnosisResponseDTO();
        response.setDiagnosisId(UUID.randomUUID().toString());
        response.setDiagnosisType(diagnosisType);
        response.setStatus("success");

        try {
            String jsonStr = extractJson(aiResponse);
            if (jsonStr != null) {
                com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSON.parseObject(jsonStr);
                response.setResult(json.getString("result"));
                response.setRootCause(json.getString("rootCause"));
                response.setSuggestedFix(json.getString("suggestedFix"));
                
                if (json.containsKey("possibleCauses")) {
                    response.setPossibleCauses(json.getJSONArray("possibleCauses").toJavaList(String.class));
                }
                if (json.containsKey("improvementSuggestions")) {
                    response.setImprovementSuggestions(json.getJSONArray("improvementSuggestions").toJavaList(String.class));
                }
                
                response.setConfidenceScore(json.getInteger("confidenceScore"));
            } else {
                response.setResult(aiResponse);
                response.setConfidenceScore(50);
            }
        } catch (Exception e) {
            log.warn("解析AI响应JSON失败，使用原始文本: ", e);
            response.setResult(aiResponse);
            response.setConfidenceScore(50);
        }

        return response;
    }

    private String extractJson(String response) {
        if (response == null) return null;
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return response.substring(start, end + 1);
        }
        return null;
    }

    private AIDiagnosisResponseDTO buildErrorResponse(String errorMessage) {
        AIDiagnosisResponseDTO response = new AIDiagnosisResponseDTO();
        response.setDiagnosisId(UUID.randomUUID().toString());
        response.setStatus("failed");
        response.setErrorMessage(errorMessage);
        response.setConfidenceScore(0);
        return response;
    }
}

