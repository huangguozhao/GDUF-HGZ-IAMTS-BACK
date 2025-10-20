package com.victor.iatms.service.impl;

import com.victor.iatms.entity.constants.Constants;
import com.victor.iatms.entity.dto.AddTestCaseDTO;
import com.victor.iatms.entity.dto.AddTestCaseResponseDTO;
import com.victor.iatms.entity.dto.CreateTestCaseDTO;
import com.victor.iatms.entity.dto.CreateTestCaseResponseDTO;
import com.victor.iatms.entity.dto.TestCaseItemDTO;
import com.victor.iatms.entity.dto.TestCaseListQueryDTO;
import com.victor.iatms.entity.dto.TestCaseListResponseDTO;
import com.victor.iatms.entity.dto.TestCaseSummaryDTO;
import com.victor.iatms.entity.dto.UpdateTestCaseDTO;
import com.victor.iatms.entity.dto.UpdateTestCaseResponseDTO;
import com.victor.iatms.entity.enums.TestCasePriorityEnum;
import com.victor.iatms.entity.enums.TestCaseSeverityEnum;
import com.victor.iatms.entity.po.Api;
import com.victor.iatms.entity.po.TestCase;
import com.victor.iatms.mappers.ApiMapper;
import com.victor.iatms.mappers.TestCaseMapper;
import com.victor.iatms.service.TestCaseService;
import com.victor.iatms.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 测试用例服务实现类
 */
@Service
public class TestCaseServiceImpl implements TestCaseService {

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private ApiMapper apiMapper;

    @Override
    public CreateTestCaseResponseDTO createTestCase(CreateTestCaseDTO createTestCaseDTO, Integer currentUserId) {
        // 参数校验
        validateCreateTestCase(createTestCaseDTO);

        // 检查接口是否存在
        Api api = apiMapper.selectById(createTestCaseDTO.getApiId());
        if (api == null) {
            throw new IllegalArgumentException("接口不存在");
        }

        // 检查接口是否已被删除
        if (api.getIsDeleted()) {
            throw new IllegalArgumentException("接口不存在");
        }

        // 检查接口状态是否为active
        if (!"active".equalsIgnoreCase(api.getStatus())) {
            throw new IllegalArgumentException("接口已禁用，无法创建用例");
        }

        // 检查权限（需要用例管理权限）
        if (!hasTestCaseManagePermission(api, currentUserId)) {
            throw new IllegalArgumentException("权限不足，无法创建测试用例");
        }

        // 验证用例编码唯一性（如果提供了用例编码）
        if (StringUtils.hasText(createTestCaseDTO.getCaseCode())) {
            if (testCaseMapper.checkCaseCodeExists(createTestCaseDTO.getCaseCode(), createTestCaseDTO.getApiId()) > 0) {
                throw new IllegalArgumentException("用例编码已存在");
            }
        } else {
            // 自动生成用例编码
            createTestCaseDTO.setCaseCode(generateCaseCode(createTestCaseDTO.getApiId()));
        }

        // 验证模板用例（如果提供了模板ID）
        if (createTestCaseDTO.getTemplateId() != null) {
            validateTemplateTestCase(createTestCaseDTO.getTemplateId());
        }

        // 设置默认值
        setTestCaseDefaultValues(createTestCaseDTO);

        // 创建测试用例
        TestCase testCase = new TestCase();
        testCase.setCaseCode(createTestCaseDTO.getCaseCode());
        testCase.setApiId(createTestCaseDTO.getApiId());
        testCase.setName(createTestCaseDTO.getName());
        testCase.setDescription(createTestCaseDTO.getDescription());
        testCase.setPriority(createTestCaseDTO.getPriority());
        testCase.setSeverity(createTestCaseDTO.getSeverity());
        testCase.setIsEnabled(createTestCaseDTO.getIsEnabled());
        testCase.setIsTemplate(createTestCaseDTO.getIsTemplate());
        testCase.setTemplateId(createTestCaseDTO.getTemplateId());
        testCase.setVersion(Constants.DEFAULT_VERSION);
        testCase.setCreatedBy(currentUserId);
        testCase.setUpdatedBy(currentUserId);
        testCase.setCreatedAt(LocalDateTime.now());
        testCase.setUpdatedAt(LocalDateTime.now());
        testCase.setIsDeleted(false);

        // 处理JSON字段
        if (createTestCaseDTO.getTags() != null) {
            testCase.setTags(JsonUtils.convertObj2Json(createTestCaseDTO.getTags()));
        }
        if (createTestCaseDTO.getPreConditions() != null) {
            testCase.setPreConditions(JsonUtils.convertObj2Json(createTestCaseDTO.getPreConditions()));
        }
        if (createTestCaseDTO.getTestSteps() != null) {
            testCase.setTestSteps(JsonUtils.convertObj2Json(createTestCaseDTO.getTestSteps()));
        }
        if (createTestCaseDTO.getRequestOverride() != null) {
            testCase.setRequestOverride(JsonUtils.convertObj2Json(createTestCaseDTO.getRequestOverride()));
        }
        if (createTestCaseDTO.getExpectedResponseSchema() != null) {
            testCase.setExpectedResponseSchema(JsonUtils.convertObj2Json(createTestCaseDTO.getExpectedResponseSchema()));
        }
        if (createTestCaseDTO.getAssertions() != null) {
            testCase.setAssertions(JsonUtils.convertObj2Json(createTestCaseDTO.getAssertions()));
        }
        if (createTestCaseDTO.getExtractors() != null) {
            testCase.setExtractors(JsonUtils.convertObj2Json(createTestCaseDTO.getExtractors()));
        }
        if (createTestCaseDTO.getValidators() != null) {
            testCase.setValidators(JsonUtils.convertObj2Json(createTestCaseDTO.getValidators()));
        }

        testCase.setExpectedHttpStatus(createTestCaseDTO.getExpectedHttpStatus());
        testCase.setExpectedResponseBody(createTestCaseDTO.getExpectedResponseBody());

        int result = testCaseMapper.insert(testCase);
        if (result <= 0) {
            throw new RuntimeException("创建测试用例失败");
        }

        // 查询创建后的测试用例信息
        CreateTestCaseResponseDTO responseDTO = testCaseMapper.selectCreateTestCaseDetailById(testCase.getCaseId());
        if (responseDTO == null) {
            throw new RuntimeException("查询创建的测试用例信息失败");
        }

        // TODO: 记录审计日志
        // auditLogService.logTestCaseCreate(testCase.getCaseId(), currentUserId, createTestCaseDTO);

        return responseDTO;
    }
    
    @Override
    public AddTestCaseResponseDTO addTestCase(AddTestCaseDTO addTestCaseDTO, Integer currentUserId) {
        // 参数校验
        validateAddTestCaseDTO(addTestCaseDTO);

        // 验证接口是否存在且可用
        Api api = apiMapper.selectById(addTestCaseDTO.getApiId());
        if (api == null || api.getIsDeleted()) {
            throw new IllegalArgumentException("接口不存在");
        }
        if (!"active".equalsIgnoreCase(api.getStatus())) {
            throw new IllegalArgumentException("接口已禁用，无法创建用例");
        }

        // TODO: 权限验证 (检查用户是否有权限在目标接口下创建用例)
        // 暂时跳过，实际需要根据项目成员权限或模块权限进行判断
        // if (!hasPermissionToCreateTestCase(api.getModuleId(), currentUserId)) {
        //     throw new IllegalArgumentException("权限不足，无法创建测试用例");
        // }

        // 验证用例编码唯一性或自动生成
        String caseCode = addTestCaseDTO.getCaseCode();
        if (StringUtils.hasText(caseCode)) {
            // 验证格式
            if (!Pattern.matches(Constants.TEST_CASE_CODE_PATTERN, caseCode)) {
                throw new IllegalArgumentException("用例编码格式不正确，只能包含大写字母、数字、下划线和中划线");
            }
            if (caseCode.length() > Constants.TEST_CASE_CODE_MAX_LENGTH) {
                throw new IllegalArgumentException("用例编码长度不能超过" + Constants.TEST_CASE_CODE_MAX_LENGTH + "个字符");
            }
            // 检查唯一性
            if (testCaseMapper.checkCaseCodeExists(caseCode, addTestCaseDTO.getApiId()) > 0) {
                throw new IllegalArgumentException("用例编码已存在");
            }
        } else {
            // 自动生成用例编码
            caseCode = generateCaseCode(addTestCaseDTO.getApiId());
        }

        // 验证模板用例（如果提供）
        if (addTestCaseDTO.getTemplateId() != null) {
            TestCase templateCase = testCaseMapper.selectById(addTestCaseDTO.getTemplateId());
            if (templateCase == null || templateCase.getIsDeleted() || !templateCase.getIsTemplate()) {
                throw new IllegalArgumentException("模板用例不存在或不是有效的模板");
            }
            // TODO: 可以根据模板复制测试步骤、断言等配置
            // For now, we just validate its existence.
        }

        // 构建TestCase PO
        TestCase testCase = new TestCase();
        testCase.setApiId(addTestCaseDTO.getApiId());
        testCase.setCaseCode(caseCode);
        testCase.setName(addTestCaseDTO.getName());
        testCase.setDescription(addTestCaseDTO.getDescription());
        testCase.setPriority(addTestCaseDTO.getPriority() != null ? addTestCaseDTO.getPriority() : Constants.DEFAULT_TEST_CASE_PRIORITY);
        testCase.setSeverity(addTestCaseDTO.getSeverity() != null ? addTestCaseDTO.getSeverity() : Constants.DEFAULT_TEST_CASE_SEVERITY);
        testCase.setIsEnabled(addTestCaseDTO.getIsEnabled() != null ? addTestCaseDTO.getIsEnabled() : Constants.DEFAULT_TEST_CASE_ENABLED);
        testCase.setIsTemplate(addTestCaseDTO.getIsTemplate() != null ? addTestCaseDTO.getIsTemplate() : Constants.DEFAULT_TEST_CASE_TEMPLATE);
        testCase.setTemplateId(addTestCaseDTO.getTemplateId());
        testCase.setVersion(Constants.DEFAULT_VERSION); // 默认版本号
        testCase.setCreatedBy(currentUserId);
        testCase.setUpdatedBy(currentUserId);
        testCase.setCreatedAt(LocalDateTime.now());
        testCase.setUpdatedAt(LocalDateTime.now());
        testCase.setIsDeleted(false);

        // 处理JSON字段
        if (addTestCaseDTO.getTags() != null) {
            testCase.setTags(JsonUtils.convertObj2Json(addTestCaseDTO.getTags()));
        }
        if (addTestCaseDTO.getPreConditions() != null) {
            testCase.setPreConditions(JsonUtils.convertObj2Json(addTestCaseDTO.getPreConditions()));
        }
        if (addTestCaseDTO.getTestSteps() != null) {
            testCase.setTestSteps(JsonUtils.convertObj2Json(addTestCaseDTO.getTestSteps()));
        }
        if (addTestCaseDTO.getRequestOverride() != null) {
            testCase.setRequestOverride(JsonUtils.convertObj2Json(addTestCaseDTO.getRequestOverride()));
        }
        if (addTestCaseDTO.getExpectedResponseSchema() != null) {
            testCase.setExpectedResponseSchema(JsonUtils.convertObj2Json(addTestCaseDTO.getExpectedResponseSchema()));
        }
        if (addTestCaseDTO.getAssertions() != null) {
            testCase.setAssertions(JsonUtils.convertObj2Json(addTestCaseDTO.getAssertions()));
        }
        if (addTestCaseDTO.getExtractors() != null) {
            testCase.setExtractors(JsonUtils.convertObj2Json(addTestCaseDTO.getExtractors()));
        }
        if (addTestCaseDTO.getValidators() != null) {
            testCase.setValidators(JsonUtils.convertObj2Json(addTestCaseDTO.getValidators()));
        }
        testCase.setExpectedHttpStatus(addTestCaseDTO.getExpectedHttpStatus());
        testCase.setExpectedResponseBody(addTestCaseDTO.getExpectedResponseBody());

        // 插入数据库
        int result = testCaseMapper.insert(testCase);
        if (result <= 0 || testCase.getCaseId() == null) {
            throw new RuntimeException("创建测试用例失败");
        }

        // TODO: 记录审计日志
        // auditLogService.logTestCaseCreate(testCase.getCaseId(), currentUserId, testCase.getName(), testCase.getApiId());

        // 构建响应DTO
        AddTestCaseResponseDTO responseDTO = new AddTestCaseResponseDTO();
        responseDTO.setCaseId(testCase.getCaseId());
        responseDTO.setCaseCode(testCase.getCaseCode());
        responseDTO.setApiId(testCase.getApiId());
        responseDTO.setName(testCase.getName());
        responseDTO.setCreatedAt(testCase.getCreatedAt());
        responseDTO.setUpdatedAt(testCase.getUpdatedAt());

        return responseDTO;
    }
    
    @Override
    public UpdateTestCaseResponseDTO updateTestCase(Integer caseId, UpdateTestCaseDTO updateTestCaseDTO, Integer currentUserId) {
        // 参数校验
        validateUpdateTestCase(caseId, updateTestCaseDTO);

        // 检查测试用例是否存在
        TestCase testCase = testCaseMapper.selectById(caseId);
        if (testCase == null) {
            throw new IllegalArgumentException("测试用例不存在");
        }

        // 检查测试用例是否已被删除
        if (testCase.getIsDeleted()) {
            throw new IllegalArgumentException("测试用例已被删除，无法编辑");
        }

        // 检查权限（需要用例管理权限）
        if (!hasTestCaseManagePermission(testCase, currentUserId)) {
            throw new IllegalArgumentException("权限不足，无法更新测试用例");
        }

        // 验证用例编码唯一性（如果提供了新的用例编码）
        if (StringUtils.hasText(updateTestCaseDTO.getCaseCode()) &&
            !updateTestCaseDTO.getCaseCode().equals(testCase.getCaseCode())) {
            if (testCaseMapper.checkCaseCodeExistsExcludeSelf(updateTestCaseDTO.getCaseCode(),
                testCase.getApiId(), caseId) > 0) {
                throw new IllegalArgumentException("用例编码已被其他用例使用");
            }
        }

        // 验证模板用例（如果提供了新的模板ID）
        if (updateTestCaseDTO.getTemplateId() != null &&
            !updateTestCaseDTO.getTemplateId().equals(testCase.getTemplateId())) {
            validateTemplateTestCase(updateTestCaseDTO.getTemplateId());
        }

        // 验证枚举字段
        if (StringUtils.hasText(updateTestCaseDTO.getPriority())) {
            if (!isValidPriority(updateTestCaseDTO.getPriority())) {
                throw new IllegalArgumentException("优先级值无效");
            }
        }
        if (StringUtils.hasText(updateTestCaseDTO.getSeverity())) {
            if (!isValidSeverity(updateTestCaseDTO.getSeverity())) {
                throw new IllegalArgumentException("严重程度值无效");
            }
        }

        // 执行更新
        TestCase updateTestCase = new TestCase();
        updateTestCase.setCaseId(caseId);
        updateTestCase.setCaseCode(updateTestCaseDTO.getCaseCode());
        updateTestCase.setName(updateTestCaseDTO.getName());
        updateTestCase.setDescription(updateTestCaseDTO.getDescription());
        updateTestCase.setPriority(updateTestCaseDTO.getPriority());
        updateTestCase.setSeverity(updateTestCaseDTO.getSeverity());
        updateTestCase.setIsEnabled(updateTestCaseDTO.getIsEnabled());
        updateTestCase.setIsTemplate(updateTestCaseDTO.getIsTemplate());
        updateTestCase.setTemplateId(updateTestCaseDTO.getTemplateId());
        updateTestCase.setVersion(updateTestCaseDTO.getVersion());
        updateTestCase.setUpdatedBy(currentUserId);
        updateTestCase.setUpdatedAt(LocalDateTime.now());

        // 处理JSON字段
        if (updateTestCaseDTO.getTags() != null) {
            updateTestCase.setTags(JsonUtils.convertObj2Json(updateTestCaseDTO.getTags()));
        }
        if (updateTestCaseDTO.getPreConditions() != null) {
            updateTestCase.setPreConditions(JsonUtils.convertObj2Json(updateTestCaseDTO.getPreConditions()));
        }
        if (updateTestCaseDTO.getTestSteps() != null) {
            updateTestCase.setTestSteps(JsonUtils.convertObj2Json(updateTestCaseDTO.getTestSteps()));
        }
        if (updateTestCaseDTO.getRequestOverride() != null) {
            updateTestCase.setRequestOverride(JsonUtils.convertObj2Json(updateTestCaseDTO.getRequestOverride()));
        }
        if (updateTestCaseDTO.getExpectedResponseSchema() != null) {
            updateTestCase.setExpectedResponseSchema(JsonUtils.convertObj2Json(updateTestCaseDTO.getExpectedResponseSchema()));
        }
        if (updateTestCaseDTO.getAssertions() != null) {
            updateTestCase.setAssertions(JsonUtils.convertObj2Json(updateTestCaseDTO.getAssertions()));
        }
        if (updateTestCaseDTO.getExtractors() != null) {
            updateTestCase.setExtractors(JsonUtils.convertObj2Json(updateTestCaseDTO.getExtractors()));
        }
        if (updateTestCaseDTO.getValidators() != null) {
            updateTestCase.setValidators(JsonUtils.convertObj2Json(updateTestCaseDTO.getValidators()));
        }

        updateTestCase.setExpectedHttpStatus(updateTestCaseDTO.getExpectedHttpStatus());
        updateTestCase.setExpectedResponseBody(updateTestCaseDTO.getExpectedResponseBody());

        int result = testCaseMapper.updateById(updateTestCase);
        if (result <= 0) {
            throw new RuntimeException("更新测试用例失败");
        }

        // 查询更新后的测试用例信息
        UpdateTestCaseResponseDTO responseDTO = testCaseMapper.selectUpdateTestCaseDetailById(caseId);
        if (responseDTO == null) {
            throw new RuntimeException("查询更新后的测试用例信息失败");
        }

        // TODO: 记录审计日志
        // auditLogService.logTestCaseUpdate(caseId, currentUserId, updateTestCaseDTO);

        return responseDTO;
    }
    
    @Override
    public void deleteTestCase(Integer caseId, Integer currentUserId) {
        // 参数校验
        if (caseId == null) {
            throw new IllegalArgumentException("测试用例ID不能为空");
        }

        // 检查测试用例是否存在
        TestCase testCase = testCaseMapper.selectById(caseId);
        if (testCase == null) {
            throw new IllegalArgumentException("测试用例不存在");
        }

        // 检查测试用例是否已被删除
        if (testCase.getIsDeleted()) {
            throw new IllegalArgumentException("测试用例已被删除");
        }

        // 检查权限（需要用例管理权限）
        if (!hasTestCaseManagePermission(testCase, currentUserId)) {
            throw new IllegalArgumentException("权限不足，无法删除测试用例");
        }

        // 检查是否为模板用例
        if (testCase.getIsTemplate()) {
            throw new IllegalArgumentException("模板用例不能被删除");
        }

        // 检查是否为系统用例
        if (isSystemTestCase(testCase)) {
            throw new IllegalArgumentException("不能删除系统用例");
        }

        // 检查用例是否正在被使用
        if (isTestCaseInUse(caseId)) {
            throw new IllegalArgumentException("用例正在被测试计划使用，无法删除");
        }

        // 执行软删除
        int result = testCaseMapper.deleteById(caseId, currentUserId);
        if (result <= 0) {
            throw new RuntimeException("删除测试用例失败");
        }

        // TODO: 记录审计日志
        // auditLogService.logTestCaseDelete(caseId, currentUserId, testCase.getName(), testCase.getApiId());
    }
    
    @Override
    public TestCaseListResponseDTO getTestCaseList(TestCaseListQueryDTO queryDTO, Integer currentUserId) {
        // 参数校验
        validateTestCaseListQuery(queryDTO);

        // 设置默认值
        setDefaultValues(queryDTO);

        // 权限检查
        if (!hasTestCaseListPermission(currentUserId)) {
            throw new IllegalArgumentException("权限不足，无法查看测试用例列表");
        }

        // 查询测试用例列表
        List<TestCaseItemDTO> items = testCaseMapper.selectTestCaseList(queryDTO, currentUserId);
        
        // 查询总数
        Long total = testCaseMapper.countTestCaseList(queryDTO, currentUserId);
        
        // 查询统计摘要
        TestCaseSummaryDTO summary = testCaseMapper.selectTestCaseSummary(queryDTO, currentUserId);

        // 构建响应DTO
        TestCaseListResponseDTO responseDTO = new TestCaseListResponseDTO();
        responseDTO.setTotal(total);
        responseDTO.setItems(items);
        responseDTO.setPage(queryDTO.getPage());
        responseDTO.setPageSize(queryDTO.getPageSize());
        responseDTO.setSummary(summary);

        return responseDTO;
    }
    
    /**
     * 验证添加测试用例DTO
     */
    private void validateAddTestCaseDTO(AddTestCaseDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        if (dto.getApiId() == null) {
            throw new IllegalArgumentException("接口ID不能为空");
        }
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("用例名称不能为空");
        }
        if (dto.getName().length() > Constants.TEST_CASE_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("用例名称长度不能超过" + Constants.TEST_CASE_NAME_MAX_LENGTH + "个字符");
        }
        if (StringUtils.hasText(dto.getDescription()) && dto.getDescription().length() > Constants.TEST_CASE_DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("用例描述长度不能超过" + Constants.TEST_CASE_DESCRIPTION_MAX_LENGTH + "个字符");
        }

        // 验证优先级
        if (StringUtils.hasText(dto.getPriority()) && !TestCasePriorityEnum.isValidPriority(dto.getPriority())) {
            throw new IllegalArgumentException("无效的优先级：" + dto.getPriority());
        }
        // 验证严重程度
        if (StringUtils.hasText(dto.getSeverity()) && !TestCaseSeverityEnum.isValidSeverity(dto.getSeverity())) {
            throw new IllegalArgumentException("无效的严重程度：" + dto.getSeverity());
        }
        // TODO: 进一步验证JSON字段的格式和内容
    }
    
    /**
     * 自动生成用例编码
     */
    private String generateCaseCode(Integer apiId) {
        // 获取当前API下用例数量
        Integer count = testCaseMapper.countTestCasesByApiId(apiId);
        return String.format("TC-API-%d-%03d", apiId, count + 1);
    }
    
    /**
     * 更新测试用例
     */
    private void validateUpdateTestCase(Integer caseId, UpdateTestCaseDTO updateTestCaseDTO) {
        if (caseId == null) {
            throw new IllegalArgumentException("测试用例ID不能为空");
        }
        if (updateTestCaseDTO == null) {
            throw new IllegalArgumentException("更新测试用例参数不能为空");
        }
        if (StringUtils.hasText(updateTestCaseDTO.getName()) && 
            updateTestCaseDTO.getName().length() > Constants.TEST_CASE_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("用例名称长度不能超过" + Constants.TEST_CASE_NAME_MAX_LENGTH + "个字符");
        }
        if (StringUtils.hasText(updateTestCaseDTO.getDescription()) && 
            updateTestCaseDTO.getDescription().length() > Constants.TEST_CASE_DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("用例描述长度不能超过" + Constants.TEST_CASE_DESCRIPTION_MAX_LENGTH + "个字符");
        }
        if (StringUtils.hasText(updateTestCaseDTO.getCaseCode())) {
            if (!Pattern.matches(Constants.TEST_CASE_CODE_PATTERN, updateTestCaseDTO.getCaseCode())) {
                throw new IllegalArgumentException("用例编码格式不正确");
            }
            if (updateTestCaseDTO.getCaseCode().length() > Constants.TEST_CASE_CODE_MAX_LENGTH) {
                throw new IllegalArgumentException("用例编码长度不能超过" + Constants.TEST_CASE_CODE_MAX_LENGTH + "个字符");
            }
        }
    }
    
    /**
     * 创建测试用例
     */
    private void validateCreateTestCase(CreateTestCaseDTO createTestCaseDTO) {
        if (createTestCaseDTO == null) {
            throw new IllegalArgumentException("创建测试用例参数不能为空");
        }
        if (createTestCaseDTO.getApiId() == null) {
            throw new IllegalArgumentException("接口ID不能为空");
        }
        if (!StringUtils.hasText(createTestCaseDTO.getName())) {
            throw new IllegalArgumentException("用例名称不能为空");
        }
        if (createTestCaseDTO.getName().length() > Constants.TEST_CASE_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("用例名称长度不能超过" + Constants.TEST_CASE_NAME_MAX_LENGTH + "个字符");
        }
        if (StringUtils.hasText(createTestCaseDTO.getDescription()) && 
            createTestCaseDTO.getDescription().length() > Constants.TEST_CASE_DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("用例描述长度不能超过" + Constants.TEST_CASE_DESCRIPTION_MAX_LENGTH + "个字符");
        }
        if (StringUtils.hasText(createTestCaseDTO.getCaseCode())) {
            if (!Pattern.matches(Constants.TEST_CASE_CODE_PATTERN, createTestCaseDTO.getCaseCode())) {
                throw new IllegalArgumentException("用例编码格式不正确");
            }
            if (createTestCaseDTO.getCaseCode().length() > Constants.TEST_CASE_CODE_MAX_LENGTH) {
                throw new IllegalArgumentException("用例编码长度不能超过" + Constants.TEST_CASE_CODE_MAX_LENGTH + "个字符");
            }
        }
    }

    /**
     * 设置测试用例默认值
     */
    private void setTestCaseDefaultValues(CreateTestCaseDTO createTestCaseDTO) {
        if (!StringUtils.hasText(createTestCaseDTO.getPriority())) {
            createTestCaseDTO.setPriority(Constants.DEFAULT_TEST_CASE_PRIORITY);
        }
        if (!StringUtils.hasText(createTestCaseDTO.getSeverity())) {
            createTestCaseDTO.setSeverity(Constants.DEFAULT_TEST_CASE_SEVERITY);
        }
        if (createTestCaseDTO.getIsEnabled() == null) {
            createTestCaseDTO.setIsEnabled(Constants.DEFAULT_TEST_CASE_ENABLED);
        }
        if (createTestCaseDTO.getIsTemplate() == null) {
            createTestCaseDTO.setIsTemplate(Constants.DEFAULT_TEST_CASE_TEMPLATE);
        }

        // 验证优先级
        if (!isValidPriority(createTestCaseDTO.getPriority())) {
            createTestCaseDTO.setPriority(Constants.DEFAULT_TEST_CASE_PRIORITY);
        }

        // 验证严重程度
        if (!isValidSeverity(createTestCaseDTO.getSeverity())) {
            createTestCaseDTO.setSeverity(Constants.DEFAULT_TEST_CASE_SEVERITY);
        }
    }

    /**
     * 校验优先级是否有效
     */
    private boolean isValidPriority(String priority) {
        for (com.victor.iatms.entity.enums.TestCasePriorityEnum priorityEnum : com.victor.iatms.entity.enums.TestCasePriorityEnum.values()) {
            if (priorityEnum.getCode().equalsIgnoreCase(priority)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验严重程度是否有效
     */
    private boolean isValidSeverity(String severity) {
        for (com.victor.iatms.entity.enums.TestCaseSeverityEnum severityEnum : com.victor.iatms.entity.enums.TestCaseSeverityEnum.values()) {
            if (severityEnum.getCode().equalsIgnoreCase(severity)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 验证模板用例
     */
    private void validateTemplateTestCase(Integer templateId) {
        TestCase template = testCaseMapper.selectById(templateId);
        if (template == null) {
            throw new IllegalArgumentException("模板用例不存在");
        }
        if (!template.getIsTemplate()) {
            throw new IllegalArgumentException("指定的用例不是模板用例");
        }
        if (template.getIsDeleted()) {
            throw new IllegalArgumentException("模板用例已被删除");
        }
    }

    /**
     * 检查是否有测试用例管理权限
     */
    private boolean hasTestCaseManagePermission(Api api, Integer userId) {
        // 规则1：可以管理自己创建的接口的用例
        if (api.getCreatedBy().equals(userId)) {
            return true;
        }

        // 规则2：项目成员可以管理用例
        // TODO: 这里应该检查用户的项目成员权限
        // 暂时返回true，实际应该查询项目成员权限
        return true;
    }
    
    /**
     * 检查是否有测试用例管理权限（重载方法）
     */
    private boolean hasTestCaseManagePermission(TestCase testCase, Integer userId) {
        // 规则1：可以管理自己创建的用例
        if (testCase.getCreatedBy().equals(userId)) {
            return true;
        }

        // 规则2：项目成员可以管理用例
        // TODO: 这里应该检查用户的项目成员权限
        // 暂时返回true，实际应该查询项目成员权限
        return true;
    }
    
    /**
     * 检查是否为系统用例
     */
    private boolean isSystemTestCase(TestCase testCase) {
        // 检查用例编码是否以系统前缀开头
        if (testCase.getCaseCode() != null && 
            testCase.getCaseCode().startsWith(Constants.SYSTEM_TEST_CASE_CODE_PREFIX)) {
            return true;
        }
        
        // 检查用例名称是否包含系统关键字
        if (testCase.getName() != null && 
            testCase.getName().contains(Constants.SYSTEM_TEST_CASE_NAME_KEYWORD)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 检查用例是否正在被使用
     */
    private boolean isTestCaseInUse(Integer caseId) {
        // TODO: 检查用例是否正在被测试计划使用
        // 这里需要根据实际的业务逻辑来实现
        // 例如：检查TestPlanCases表中是否有引用该用例的记录
        
        // 暂时返回false，表示用例未被使用
        // 实际实现时需要查询相关的关联表
        return false;
    }
    
    /**
     * 验证测试用例列表查询参数
     */
    private void validateTestCaseListQuery(TestCaseListQueryDTO queryDTO) {
        if (queryDTO == null) {
            throw new IllegalArgumentException("查询参数不能为空");
        }
        
        // 验证分页大小
        if (queryDTO.getPageSize() != null && queryDTO.getPageSize() > Constants.MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("分页大小不能超过" + Constants.MAX_PAGE_SIZE);
        }
        
        // 验证排序字段
        if (StringUtils.hasText(queryDTO.getSortBy())) {
            if (!isValidSortField(queryDTO.getSortBy())) {
                throw new IllegalArgumentException("排序字段无效");
            }
        }
        
        // 验证排序顺序
        if (StringUtils.hasText(queryDTO.getSortOrder())) {
            if (!"asc".equalsIgnoreCase(queryDTO.getSortOrder()) && 
                !"desc".equalsIgnoreCase(queryDTO.getSortOrder())) {
                throw new IllegalArgumentException("排序顺序无效");
            }
        }
    }
    
    /**
     * 设置默认值
     */
    private void setDefaultValues(TestCaseListQueryDTO queryDTO) {
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
            queryDTO.setPage(Constants.DEFAULT_PAGE);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
            queryDTO.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        }
        
        // 计算分页偏移量
        int offset = (queryDTO.getPage() - 1) * queryDTO.getPageSize();
        queryDTO.setOffset(offset);
        
        if (!StringUtils.hasText(queryDTO.getSortBy())) {
            queryDTO.setSortBy(Constants.DEFAULT_TEST_CASE_SORT_BY);
        }
        if (!StringUtils.hasText(queryDTO.getSortOrder())) {
            queryDTO.setSortOrder(Constants.DEFAULT_SORT_ORDER);
        }
        if (queryDTO.getIncludeDeleted() == null) {
            queryDTO.setIncludeDeleted(false);
        }
    }
    
    /**
     * 检查是否有测试用例列表查看权限
     */
    private boolean hasTestCaseListPermission(Integer userId) {
        // TODO: 这里应该检查用户的项目成员权限
        // 暂时返回true，实际应该查询项目成员权限
        return true;
    }
    
    /**
     * 验证排序字段是否有效
     */
    private boolean isValidSortField(String sortField) {
        return "name".equalsIgnoreCase(sortField) ||
               "case_code".equalsIgnoreCase(sortField) ||
               "priority".equalsIgnoreCase(sortField) ||
               "severity".equalsIgnoreCase(sortField) ||
               "created_at".equalsIgnoreCase(sortField) ||
               "updated_at".equalsIgnoreCase(sortField);
    }
}