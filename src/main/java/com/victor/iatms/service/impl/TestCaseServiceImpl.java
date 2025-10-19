package com.victor.iatms.service.impl;

import com.victor.iatms.entity.constants.Constants;
import com.victor.iatms.entity.dto.CreateTestCaseDTO;
import com.victor.iatms.entity.dto.CreateTestCaseResponseDTO;
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

    /**
     * 创建测试用例参数校验
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
     * 生成用例编码
     */
    private String generateCaseCode(Integer apiId) {
        // 查询该接口下已存在的用例数量
        int count = testCaseMapper.countByApiId(apiId);
        return String.format("TC-API-%d-%03d", apiId, count + 1);
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
}