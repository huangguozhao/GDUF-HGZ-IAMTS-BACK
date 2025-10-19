package com.victor.iatms.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.victor.iatms.entity.constants.Constants;
import com.victor.iatms.entity.dto.CreateTestCaseDTO;
import com.victor.iatms.entity.dto.ExportQueryDTO;
import com.victor.iatms.entity.dto.ExportResultDTO;
import com.victor.iatms.entity.dto.ExportTestCaseDTO;
import com.victor.iatms.entity.dto.ImportResultDTO;
import com.victor.iatms.entity.dto.ImportTestCaseDTO;
import com.victor.iatms.entity.dto.PageResultDTO;
import com.victor.iatms.entity.dto.TestCaseDTO;
import com.victor.iatms.entity.dto.TestCaseResponseDTO;
import com.victor.iatms.entity.dto.UpdateTestCaseDTO;
import com.victor.iatms.entity.dto.UpdateTestCaseResponseDTO;
import com.victor.iatms.entity.enums.ApiStatusEnum;
import com.victor.iatms.entity.enums.PriorityEnum;
import com.victor.iatms.entity.enums.SeverityEnum;
import com.victor.iatms.entity.po.Api;
import com.victor.iatms.entity.po.TestCase;
import com.victor.iatms.entity.query.TestCaseQuery;
import com.victor.iatms.mappers.ApiMapper;
import com.victor.iatms.mappers.TestCaseMapper;
import com.victor.iatms.service.TestCaseService;
import com.victor.iatms.utils.FileExportUtils;
import com.victor.iatms.utils.FileImportUtils;
import com.victor.iatms.utils.TestCaseCodeGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试用例服务实现类
 */
@Service
public class TestCaseServiceImpl implements TestCaseService {

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private ApiMapper apiMapper;

    @Autowired
    private TestCaseCodeGenerator testCaseCodeGenerator;

    @Autowired
    private FileImportUtils fileImportUtils;

    @Autowired
    private FileExportUtils fileExportUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageResultDTO<TestCaseDTO> getTestCaseList(TestCaseQuery query) {
        // 1. 验证接口是否存在且状态为active
        Api api = apiMapper.findByIdAndStatus(query.getApiId(), ApiStatusEnum.ACTIVE.getCode());
        if (api == null) {
            throw new RuntimeException("接口不存在");
        }

        // 2. 设置默认分页参数
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(Constants.DEFAULT_PAGE);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        }
        if (query.getPageSize() > Constants.MAX_PAGE_SIZE) {
            query.setPageSize(Constants.MAX_PAGE_SIZE);
        }

        // 3. 使用PageHelper进行分页
        PageHelper.startPage(query.getPage(), query.getPageSize());

        // 4. 查询测试用例列表
        List<TestCaseDTO> testCaseList = testCaseMapper.findTestCaseList(query);

        // 5. 处理JSON字段转换
        for (TestCaseDTO testCase : testCaseList) {
            convertJsonFields(testCase);
        }

        // 6. 获取分页信息
        PageInfo<TestCaseDTO> pageInfo = new PageInfo<>(testCaseList);

        // 7. 构建分页结果
        PageResultDTO<TestCaseDTO> result = new PageResultDTO<>(
            pageInfo.getTotal(),
            pageInfo.getList(),
            query.getPage(),
            query.getPageSize()
        );

        return result;
    }

    @Override
    public TestCaseResponseDTO createTestCase(Integer apiId, CreateTestCaseDTO createRequest, Integer userId) {
        // 1. 验证接口是否存在且状态为active
        Api api = apiMapper.findByIdAndStatus(apiId, ApiStatusEnum.ACTIVE.getCode());
        if (api == null) {
            throw new RuntimeException("接口不存在");
        }

        // 2. 参数校验
        validateCreateRequest(createRequest);

        // 3. 处理用例编码
        String caseCode = createRequest.getCaseCode();
        if (caseCode == null || caseCode.trim().isEmpty()) {
            // 自动生成用例编码
            caseCode = testCaseCodeGenerator.generateTestCaseCode(apiId);
        } else {
            // 验证用例编码是否唯一
            if (!testCaseCodeGenerator.isTestCaseCodeUnique(apiId, caseCode)) {
                throw new RuntimeException("用例编码已存在");
            }
        }

        // 4. 验证模板用例（如果提供了templateId）
        if (createRequest.getTemplateId() != null) {
            TestCase templateCase = testCaseMapper.findById(createRequest.getTemplateId());
            if (templateCase == null || !templateCase.getIsTemplate()) {
                throw new RuntimeException("模板用例不存在");
            }
        }

        // 5. 构建测试用例实体
        TestCase testCase = buildTestCase(apiId, createRequest, userId, caseCode);

        // 6. 插入数据库
        int result = testCaseMapper.insertTestCase(testCase);
        if (result <= 0) {
            throw new RuntimeException("测试用例创建失败");
        }

        // 7. 构建响应数据
        TestCaseResponseDTO response = new TestCaseResponseDTO();
        response.setCaseId(testCase.getCaseId());
        response.setCaseCode(testCase.getCaseCode());
        response.setApiId(testCase.getApiId());
        response.setName(testCase.getName());
        response.setCreatedAt(testCase.getCreatedAt());
        response.setUpdatedAt(testCase.getUpdatedAt());

        return response;
    }

    /**
     * 参数校验
     */
    private void validateCreateRequest(CreateTestCaseDTO createRequest) {
        // 验证优先级
        if (createRequest.getPriority() != null && !PriorityEnum.isValid(createRequest.getPriority())) {
            throw new RuntimeException("无效的优先级值");
        }

        // 验证严重程度
        if (createRequest.getSeverity() != null && !SeverityEnum.isValid(createRequest.getSeverity())) {
            throw new RuntimeException("无效的严重程度值");
        }
    }

    /**
     * 构建测试用例实体
     */
    private TestCase buildTestCase(Integer apiId, CreateTestCaseDTO createRequest, Integer userId, String caseCode) {
        TestCase testCase = new TestCase();
        
        // 基本信息
        testCase.setCaseCode(caseCode);
        testCase.setApiId(apiId);
        testCase.setName(createRequest.getName());
        testCase.setDescription(createRequest.getDescription());
        
        // 设置默认值
        testCase.setPriority(createRequest.getPriority() != null ? createRequest.getPriority() : Constants.DEFAULT_PRIORITY);
        testCase.setSeverity(createRequest.getSeverity() != null ? createRequest.getSeverity() : Constants.DEFAULT_SEVERITY);
        testCase.setIsEnabled(createRequest.getIsEnabled() != null ? createRequest.getIsEnabled() : Constants.DEFAULT_ENABLED);
        testCase.setIsTemplate(createRequest.getIsTemplate() != null ? createRequest.getIsTemplate() : Constants.DEFAULT_TEMPLATE);
        testCase.setVersion(Constants.DEFAULT_VERSION);
        
        // 其他配置
        testCase.setTemplateId(createRequest.getTemplateId());
        testCase.setExpectedHttpStatus(createRequest.getExpectedHttpStatus());
        testCase.setExpectedResponseBody(createRequest.getExpectedResponseBody());
        
        // 创建人信息
        testCase.setCreatedBy(userId);
        testCase.setUpdatedBy(userId);
        
        // JSON字段处理
        try {
            if (createRequest.getTags() != null && !createRequest.getTags().isEmpty()) {
                testCase.setTags(objectMapper.writeValueAsString(createRequest.getTags()));
            }
            if (createRequest.getPreConditions() != null && !createRequest.getPreConditions().trim().isEmpty()) {
                testCase.setPreConditions(createRequest.getPreConditions());
            }
            if (createRequest.getTestSteps() != null && !createRequest.getTestSteps().trim().isEmpty()) {
                testCase.setTestSteps(createRequest.getTestSteps());
            }
            if (createRequest.getRequestOverride() != null && !createRequest.getRequestOverride().trim().isEmpty()) {
                testCase.setRequestOverride(createRequest.getRequestOverride());
            }
            if (createRequest.getExpectedResponseSchema() != null && !createRequest.getExpectedResponseSchema().trim().isEmpty()) {
                testCase.setExpectedResponseSchema(createRequest.getExpectedResponseSchema());
            }
            if (createRequest.getAssertions() != null && !createRequest.getAssertions().trim().isEmpty()) {
                testCase.setAssertions(createRequest.getAssertions());
            }
            if (createRequest.getExtractors() != null && !createRequest.getExtractors().trim().isEmpty()) {
                testCase.setExtractors(createRequest.getExtractors());
            }
            if (createRequest.getValidators() != null && !createRequest.getValidators().trim().isEmpty()) {
                testCase.setValidators(createRequest.getValidators());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON格式转换失败", e);
        }
        
        return testCase;
    }

    @Override
    public UpdateTestCaseResponseDTO updateTestCase(Integer apiId, Integer caseId, UpdateTestCaseDTO updateRequest, Integer userId) {
        // 1. 验证接口是否存在且状态为active
        Api api = apiMapper.findByIdAndStatus(apiId, ApiStatusEnum.ACTIVE.getCode());
        if (api == null) {
            throw new RuntimeException("接口不存在");
        }

        // 2. 验证用例是否存在且属于指定接口
        TestCase existingTestCase = testCaseMapper.findByIdAndApiId(caseId, apiId);
        if (existingTestCase == null) {
            throw new RuntimeException("测试用例不存在");
        }

        // 3. 参数校验
        validateUpdateRequest(updateRequest);

        // 4. 验证用例编码唯一性（如果提供了新的编码）
        if (updateRequest.getCaseCode() != null && !updateRequest.getCaseCode().trim().isEmpty()) {
            if (!updateRequest.getCaseCode().equals(existingTestCase.getCaseCode())) {
                // 编码发生了变化，需要验证新编码的唯一性
                Long count = testCaseMapper.countByApiIdAndCaseCodeExcludeId(apiId, updateRequest.getCaseCode(), caseId);
                if (count > 0) {
                    throw new RuntimeException("用例编码已被其他用例使用");
                }
            }
        }

        // 5. 验证模板用例（如果提供了templateId）
        if (updateRequest.getTemplateId() != null) {
            TestCase templateCase = testCaseMapper.findById(updateRequest.getTemplateId());
            if (templateCase == null || !templateCase.getIsTemplate()) {
                throw new RuntimeException("模板用例不存在");
            }
        }

        // 6. 构建更新后的测试用例实体
        TestCase updatedTestCase = buildUpdatedTestCase(existingTestCase, updateRequest, userId);

        // 7. 更新数据库
        int result = testCaseMapper.updateTestCase(updatedTestCase);
        if (result <= 0) {
            throw new RuntimeException("测试用例更新失败");
        }

        // 8. 构建响应数据
        UpdateTestCaseResponseDTO response = new UpdateTestCaseResponseDTO();
        response.setCaseId(updatedTestCase.getCaseId());
        response.setCaseCode(updatedTestCase.getCaseCode());
        response.setApiId(updatedTestCase.getApiId());
        response.setName(updatedTestCase.getName());
        response.setPriority(updatedTestCase.getPriority());
        response.setSeverity(updatedTestCase.getSeverity());
        response.setIsEnabled(updatedTestCase.getIsEnabled());
        response.setUpdatedAt(updatedTestCase.getUpdatedAt());

        return response;
    }

    /**
     * 参数校验
     */
    private void validateUpdateRequest(UpdateTestCaseDTO updateRequest) {
        // 验证优先级
        if (updateRequest.getPriority() != null && !PriorityEnum.isValid(updateRequest.getPriority())) {
            throw new RuntimeException("无效的优先级值");
        }

        // 验证严重程度
        if (updateRequest.getSeverity() != null && !SeverityEnum.isValid(updateRequest.getSeverity())) {
            throw new RuntimeException("无效的严重程度值");
        }
    }

    /**
     * 构建更新后的测试用例实体
     */
    private TestCase buildUpdatedTestCase(TestCase existingTestCase, UpdateTestCaseDTO updateRequest, Integer userId) {
        TestCase testCase = new TestCase();
        
        // 设置用例ID（必须）
        testCase.setCaseId(existingTestCase.getCaseId());
        
        // 只更新提供的字段
        if (updateRequest.getCaseCode() != null && !updateRequest.getCaseCode().trim().isEmpty()) {
            testCase.setCaseCode(updateRequest.getCaseCode());
        }
        if (updateRequest.getName() != null && !updateRequest.getName().trim().isEmpty()) {
            testCase.setName(updateRequest.getName());
        }
        if (updateRequest.getDescription() != null) {
            testCase.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getPriority() != null && !updateRequest.getPriority().trim().isEmpty()) {
            testCase.setPriority(updateRequest.getPriority());
        }
        if (updateRequest.getSeverity() != null && !updateRequest.getSeverity().trim().isEmpty()) {
            testCase.setSeverity(updateRequest.getSeverity());
        }
        if (updateRequest.getIsEnabled() != null) {
            testCase.setIsEnabled(updateRequest.getIsEnabled());
        }
        if (updateRequest.getIsTemplate() != null) {
            testCase.setIsTemplate(updateRequest.getIsTemplate());
        }
        if (updateRequest.getTemplateId() != null) {
            testCase.setTemplateId(updateRequest.getTemplateId());
        }
        if (updateRequest.getVersion() != null && !updateRequest.getVersion().trim().isEmpty()) {
            testCase.setVersion(updateRequest.getVersion());
        }
        if (updateRequest.getExpectedHttpStatus() != null) {
            testCase.setExpectedHttpStatus(updateRequest.getExpectedHttpStatus());
        }
        if (updateRequest.getExpectedResponseBody() != null) {
            testCase.setExpectedResponseBody(updateRequest.getExpectedResponseBody());
        }
        
        // 设置更新人信息
        testCase.setUpdatedBy(userId);
        
        // JSON字段处理
        try {
            if (updateRequest.getTags() != null) {
                if (updateRequest.getTags().isEmpty()) {
                    testCase.setTags(null); // 清空标签
                } else {
                    testCase.setTags(objectMapper.writeValueAsString(updateRequest.getTags()));
                }
            }
            if (updateRequest.getPreConditions() != null) {
                testCase.setPreConditions(updateRequest.getPreConditions().trim().isEmpty() ? null : updateRequest.getPreConditions());
            }
            if (updateRequest.getTestSteps() != null) {
                testCase.setTestSteps(updateRequest.getTestSteps().trim().isEmpty() ? null : updateRequest.getTestSteps());
            }
            if (updateRequest.getRequestOverride() != null) {
                testCase.setRequestOverride(updateRequest.getRequestOverride().trim().isEmpty() ? null : updateRequest.getRequestOverride());
            }
            if (updateRequest.getExpectedResponseSchema() != null) {
                testCase.setExpectedResponseSchema(updateRequest.getExpectedResponseSchema().trim().isEmpty() ? null : updateRequest.getExpectedResponseSchema());
            }
            if (updateRequest.getAssertions() != null) {
                testCase.setAssertions(updateRequest.getAssertions().trim().isEmpty() ? null : updateRequest.getAssertions());
            }
            if (updateRequest.getExtractors() != null) {
                testCase.setExtractors(updateRequest.getExtractors().trim().isEmpty() ? null : updateRequest.getExtractors());
            }
            if (updateRequest.getValidators() != null) {
                testCase.setValidators(updateRequest.getValidators().trim().isEmpty() ? null : updateRequest.getValidators());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON格式转换失败", e);
        }
        
        return testCase;
    }

    @Override
    public boolean deleteTestCase(Integer apiId, Integer caseId, Integer userId) {
        // 1. 验证接口是否存在
        Api api = apiMapper.findById(apiId);
        if (api == null) {
            throw new RuntimeException("接口不存在");
        }

        // 2. 验证用例是否存在且属于指定接口
        TestCase existingTestCase = testCaseMapper.findByIdAndApiId(caseId, apiId);
        if (existingTestCase == null) {
            throw new RuntimeException("测试用例不存在");
        }

        // 3. 检查用例是否已被删除
        boolean isDeleted = testCaseMapper.isTestCaseDeleted(caseId);
        if (isDeleted) {
            throw new RuntimeException("测试用例已被删除");
        }

        // 4. 重要的业务逻辑检查
        validateTestCaseForDeletion(existingTestCase);

        // 5. 执行软删除
        int result = testCaseMapper.deleteTestCase(caseId, userId);
        if (result <= 0) {
            throw new RuntimeException("测试用例删除失败");
        }

        return true;
    }

    /**
     * 验证测试用例是否可以删除
     */
    private void validateTestCaseForDeletion(TestCase testCase) {
        // TODO: 检查用例是否正在被测试计划使用
        // 这里需要关联测试计划用例表，检查是否有测试计划在使用该用例
        // 示例代码：
        // Long planCount = testPlanCaseMapper.countByTestCaseId(testCase.getCaseId());
        // if (planCount > 0) {
        //     throw new RuntimeException("用例正在被测试计划使用，无法删除");
        // }

        // TODO: 检查用例是否正在执行中
        // 这里需要关联测试执行记录表，检查是否有正在执行的测试
        // 示例代码：
        // Long executionCount = testExecutionMapper.countRunningByTestCaseId(testCase.getCaseId());
        // if (executionCount > 0) {
        //     throw new RuntimeException("用例正在执行中，无法删除");
        // }

        // TODO: 检查用例是否为系统内置模板用例
        // 根据业务规则决定是否允许删除系统内置模板
        // 示例代码：
        // if (testCase.getIsTemplate() && isSystemBuiltInTemplate(testCase)) {
        //     throw new RuntimeException("系统内置模板用例不允许删除");
        // }

        // 目前暂时跳过这些检查，在实际业务中需要根据具体需求实现
    }

    /**
     * 检查是否为系统内置模板用例
     * TODO: 实现系统内置模板的判断逻辑
     * 注意：此方法目前未被使用，在实现系统内置模板检查时会用到
     */
    @SuppressWarnings("unused")
    private boolean isSystemBuiltInTemplate(TestCase testCase) {
        // 这里可以根据业务规则判断是否为系统内置模板
        // 例如：检查创建人是否为系统用户，或者检查特定的标识字段
        return false; // 暂时返回false，表示不是系统内置模板
    }

    /**
     * 转换JSON字段
     */
    private void convertJsonFields(TestCaseDTO testCase) {
        try {
            // 转换tags字段
            if (testCase.getTagsJson() != null && !testCase.getTagsJson().trim().isEmpty()) {
                List<String> tags = objectMapper.readValue(testCase.getTagsJson(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                testCase.setTags(tags);
            }
        } catch (JsonProcessingException e) {
            // 如果JSON解析失败，设置为空列表
            testCase.setTags(null);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultDTO importTestCases(Integer apiId, MultipartFile file, String importMode, 
                                          String conflictStrategy, String templateType, Integer userId) {
        // 1. 验证接口是否存在且状态为active
        Api api = apiMapper.findByIdAndStatus(apiId, ApiStatusEnum.ACTIVE.getCode());
        if (api == null) {
            throw new RuntimeException("接口不存在");
        }

        // 2. 验证文件
        validateImportFile(file);

        // 3. 解析文件
        List<ImportTestCaseDTO> importTestCases;
        try {
            importTestCases = fileImportUtils.parseFile(file);
        } catch (IOException e) {
            throw new RuntimeException("文件解析失败: " + e.getMessage());
        }

        if (importTestCases.isEmpty()) {
            throw new RuntimeException("文件中没有有效的测试用例数据");
        }

        // 4. 生成导入ID
        String importId = generateImportId();

        // 5. 处理导入数据
        ImportResultDTO result = processImportData(apiId, importTestCases, importMode, conflictStrategy, userId);

        // 6. 设置导入ID
        result.setImportId(importId);

        return result;
    }

    /**
     * 验证导入文件
     */
    private void validateImportFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传的文件为空");
        }

        // 检查文件大小
        if (file.getSize() > Constants.MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小超过限制(" + (Constants.MAX_FILE_SIZE / 1024 / 1024) + "MB)");
        }

        // 检查文件格式
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new RuntimeException("文件名不能为空");
        }

        boolean supportedFormat = false;
        for (String extension : Constants.SUPPORTED_FILE_EXTENSIONS) {
            if (fileName.toLowerCase().endsWith(extension)) {
                supportedFormat = true;
                break;
            }
        }

        if (!supportedFormat) {
            throw new RuntimeException("不支持的文件格式，请上传Excel或CSV文件");
        }
    }

    /**
     * 生成导入ID
     */
    private String generateImportId() {
        return "imp_" + System.currentTimeMillis();
    }

    /**
     * 处理导入数据
     */
    private ImportResultDTO processImportData(Integer apiId, List<ImportTestCaseDTO> importTestCases, 
                                             String importMode, String conflictStrategy, Integer userId) {
        ImportResultDTO result = new ImportResultDTO();
        result.setTotalCount(importTestCases.size());
        result.setSuccessCount(0);
        result.setFailureCount(0);
        result.setSkipCount(0);
        result.setImportedCases(new ArrayList<>());
        result.setFailedRecords(new ArrayList<>());

        // 收集所有用例编码
        List<String> allCaseCodes = importTestCases.stream()
            .map(ImportTestCaseDTO::getCaseCode)
            .filter(code -> code != null && !code.trim().isEmpty())
            .collect(Collectors.toList());

        // 查询已存在的用例编码
        List<String> existingCaseCodes = testCaseMapper.findExistingCaseCodes(apiId, allCaseCodes);

        // 处理每个导入的用例
        List<TestCase> testCasesToInsert = new ArrayList<>();
        
        for (ImportTestCaseDTO importTestCase : importTestCases) {
            try {
                // 验证用例数据
                validateImportTestCase(importTestCase);

                // 检查冲突
                if (existingCaseCodes.contains(importTestCase.getCaseCode())) {
                    handleConflict(importTestCase, conflictStrategy, result);
                    continue;
                }

                // 转换为TestCase实体
                TestCase testCase = convertToTestCase(importTestCase, apiId, userId);
                testCasesToInsert.add(testCase);

                // 记录成功导入的用例
                ImportResultDTO.ImportedCaseDTO importedCase = new ImportResultDTO.ImportedCaseDTO();
                importedCase.setCaseCode(testCase.getCaseCode());
                importedCase.setName(testCase.getName());
                result.getImportedCases().add(importedCase);
                result.setSuccessCount(result.getSuccessCount() + 1);

            } catch (Exception e) {
                // 记录失败的用例
                ImportResultDTO.FailedRecordDTO failedRecord = new ImportResultDTO.FailedRecordDTO();
                failedRecord.setRowNumber(importTestCase.getRowNumber());
                failedRecord.setCaseCode(importTestCase.getCaseCode());
                failedRecord.setName(importTestCase.getName());
                failedRecord.setError(e.getMessage());
                result.getFailedRecords().add(failedRecord);
                result.setFailureCount(result.getFailureCount() + 1);
            }
        }

        // 批量插入
        if (!testCasesToInsert.isEmpty()) {
            testCaseMapper.batchInsertTestCases(testCasesToInsert);
            
            // 更新成功导入的用例ID
            for (int i = 0; i < testCasesToInsert.size() && i < result.getImportedCases().size(); i++) {
                result.getImportedCases().get(i).setCaseId(testCasesToInsert.get(i).getCaseId());
            }
        }

        return result;
    }

    /**
     * 验证导入的测试用例
     */
    private void validateImportTestCase(ImportTestCaseDTO importTestCase) {
        if (importTestCase.getCaseCode() == null || importTestCase.getCaseCode().trim().isEmpty()) {
            throw new RuntimeException("用例编码不能为空");
        }

        if (importTestCase.getName() == null || importTestCase.getName().trim().isEmpty()) {
            throw new RuntimeException("用例名称不能为空");
        }

        // 验证优先级
        if (importTestCase.getPriority() != null && !PriorityEnum.isValid(importTestCase.getPriority())) {
            throw new RuntimeException("优先级值无效: " + importTestCase.getPriority());
        }

        // 验证严重程度
        if (importTestCase.getSeverity() != null && !SeverityEnum.isValid(importTestCase.getSeverity())) {
            throw new RuntimeException("严重程度值无效: " + importTestCase.getSeverity());
        }
    }

    /**
     * 处理冲突
     */
    private void handleConflict(ImportTestCaseDTO importTestCase, String conflictStrategy, ImportResultDTO result) {
        switch (conflictStrategy) {
            case Constants.CONFLICT_STRATEGY_SKIP:
                result.setSkipCount(result.getSkipCount() + 1);
                break;
            case Constants.CONFLICT_STRATEGY_OVERWRITE:
                // TODO: 实现覆盖逻辑
                result.setSkipCount(result.getSkipCount() + 1);
                break;
            case Constants.CONFLICT_STRATEGY_RENAME:
                // TODO: 实现重命名逻辑
                result.setSkipCount(result.getSkipCount() + 1);
                break;
            default:
                result.setSkipCount(result.getSkipCount() + 1);
                break;
        }
    }

    /**
     * 转换为TestCase实体
     */
    private TestCase convertToTestCase(ImportTestCaseDTO importTestCase, Integer apiId, Integer userId) {
        TestCase testCase = new TestCase();
        testCase.setCaseCode(importTestCase.getCaseCode());
        testCase.setApiId(apiId);
        testCase.setName(importTestCase.getName());
        testCase.setDescription(importTestCase.getDescription());
        testCase.setPriority(importTestCase.getPriority() != null ? importTestCase.getPriority() : Constants.DEFAULT_PRIORITY);
        testCase.setSeverity(importTestCase.getSeverity() != null ? importTestCase.getSeverity() : Constants.DEFAULT_SEVERITY);
        testCase.setPreConditions(importTestCase.getPreConditions());
        testCase.setTestSteps(importTestCase.getTestSteps());
        testCase.setRequestOverride(importTestCase.getRequestOverride());
        testCase.setExpectedHttpStatus(importTestCase.getExpectedHttpStatus());
        testCase.setExpectedResponseBody(importTestCase.getExpectedResponseBody());
        testCase.setAssertions(importTestCase.getAssertions());
        testCase.setExtractors(importTestCase.getExtractors());
        testCase.setValidators(importTestCase.getValidators());
        testCase.setIsEnabled(importTestCase.getIsEnabled() != null ? importTestCase.getIsEnabled() : Constants.DEFAULT_ENABLED);
        testCase.setIsTemplate(importTestCase.getIsTemplate() != null ? importTestCase.getIsTemplate() : Constants.DEFAULT_TEMPLATE);
        testCase.setTemplateId(importTestCase.getTemplateId());
        testCase.setVersion(importTestCase.getVersion() != null ? importTestCase.getVersion() : Constants.DEFAULT_VERSION);
        testCase.setCreatedBy(userId);

        // 处理标签
        if (importTestCase.getTags() != null && !importTestCase.getTags().isEmpty()) {
            try {
                testCase.setTags(objectMapper.writeValueAsString(importTestCase.getTags()));
            } catch (JsonProcessingException e) {
                // 如果JSON转换失败，设置为空
                testCase.setTags(null);
            }
        }

        return testCase;
    }

    @Override
    public ExportResultDTO exportTestCases(Integer apiId, String format, Boolean includeDisabled, 
                                          Boolean includeTemplates, List<String> fields, String filename, Integer userId) {
        // 1. 验证接口是否存在
        Api api = apiMapper.findById(apiId);
        if (api == null) {
            throw new RuntimeException("接口不存在");
        }

        // 2. 构建导出查询参数
        ExportQueryDTO query = new ExportQueryDTO();
        query.setApiId(apiId);
        query.setIncludeDisabled(includeDisabled);
        query.setIncludeTemplates(includeTemplates);

        // 3. 查询测试用例数据
        List<ExportTestCaseDTO> testCases = testCaseMapper.findTestCasesForExport(query);
        
        if (testCases.isEmpty()) {
            throw new RuntimeException("该接口下没有可导出的测试用例");
        }

        // 4. 处理导出字段
        List<String> exportFields = processExportFields(fields);

        // 5. 处理JSON字段转换
        for (ExportTestCaseDTO testCase : testCases) {
            convertExportJsonFields(testCase);
        }

        // 6. 生成文件名
        String finalFilename = generateExportFilename(filename, format);

        // 7. 根据格式导出文件
        byte[] fileData;
        String mimeType;
        
        try {
            if (Constants.EXPORT_FORMAT_EXCEL.equals(format)) {
                fileData = fileExportUtils.exportToExcel(testCases, exportFields);
                mimeType = Constants.EXCEL_MIME_TYPE;
            } else if (Constants.EXPORT_FORMAT_CSV.equals(format)) {
                fileData = fileExportUtils.exportToCsv(testCases, exportFields);
                mimeType = Constants.CSV_MIME_TYPE;
            } else {
                throw new RuntimeException("不支持的导出格式: " + format);
            }
        } catch (IOException e) {
            throw new RuntimeException("文件导出失败: " + e.getMessage());
        }

        // 8. 构建导出结果
        ExportResultDTO result = new ExportResultDTO();
        result.setFileData(fileData);
        result.setMimeType(mimeType);
        result.setFilename(finalFilename);
        result.setFileSize(fileData.length);

        return result;
    }

    /**
     * 处理导出字段
     */
    private List<String> processExportFields(List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            // 使用默认字段
            return List.of(Constants.DEFAULT_EXPORT_FIELDS);
        }

        // 验证字段有效性
        List<String> validFields = new ArrayList<>();
        for (String field : fields) {
            if (isValidExportField(field)) {
                validFields.add(field);
            }
        }

        // 如果没有有效字段，使用默认字段
        if (validFields.isEmpty()) {
            return List.of(Constants.DEFAULT_EXPORT_FIELDS);
        }

        return validFields;
    }

    /**
     * 验证导出字段是否有效
     */
    private boolean isValidExportField(String field) {
        for (String validField : Constants.ALL_EXPORT_FIELDS) {
            if (validField.equals(field)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转换导出JSON字段
     */
    private void convertExportJsonFields(ExportTestCaseDTO testCase) {
        try {
            // 转换tags字段
            if (testCase.getTagsJson() != null && !testCase.getTagsJson().trim().isEmpty()) {
                List<String> tags = objectMapper.readValue(testCase.getTagsJson(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                testCase.setTags(tags);
            }
        } catch (JsonProcessingException e) {
            // 如果JSON解析失败，设置为空列表
            testCase.setTags(null);
        }
    }

    /**
     * 生成导出文件名
     */
    private String generateExportFilename(String filename, String format) {
        if (filename == null || filename.trim().isEmpty()) {
            filename = "testcases_export";
        }

        // 添加时间戳
        String timestamp = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        String extension = Constants.EXPORT_FORMAT_EXCEL.equals(format) ? ".xlsx" : ".csv";
        
        return filename + "_" + timestamp + extension;
    }
}
