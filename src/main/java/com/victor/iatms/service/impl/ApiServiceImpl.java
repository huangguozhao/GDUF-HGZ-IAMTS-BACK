package com.victor.iatms.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.iatms.entity.constants.Constants;
import com.victor.iatms.entity.dto.*;
import com.victor.iatms.entity.po.Api;
import com.victor.iatms.mappers.ApiMapper;
import com.victor.iatms.mappers.TestCaseMapper;
import com.victor.iatms.service.ApiService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 接口服务实现类
 */
@Service
public class ApiServiceImpl implements ApiService {

    @Autowired
    private ApiMapper apiMapper;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public ApiDTO createApi(CreateApiDTO createDTO, Integer currentUserId) {
        // 参数校验
        validateCreateApi(createDTO);

        // 检查接口编码是否已存在
        if (StringUtils.hasText(createDTO.getApiCode())) {
            if (apiMapper.checkApiCodeExists(createDTO.getApiCode(), createDTO.getModuleId()) > 0) {
                throw new IllegalArgumentException("接口编码已存在");
            }
        } else {
            // 自动生成接口编码
            createDTO.setApiCode(generateApiCode(createDTO.getModuleId()));
        }

        // 构建接口实体
        Api api = new Api();
        api.setApiCode(createDTO.getApiCode());
        api.setModuleId(createDTO.getModuleId());
        api.setName(createDTO.getName());
        api.setMethod(createDTO.getMethod().toUpperCase());
        api.setPath(createDTO.getPath());
        api.setBaseUrl(createDTO.getBaseUrl());
        api.setRequestParameters(toJson(createDTO.getRequestParameters()));
        api.setPathParameters(toJson(createDTO.getPathParameters()));
        api.setRequestHeaders(toJson(createDTO.getRequestHeaders()));
        api.setRequestBody(createDTO.getRequestBody());
        api.setRequestBodyType(createDTO.getRequestBodyType());
        api.setResponseBodyType(createDTO.getResponseBodyType());
        api.setDescription(createDTO.getDescription());
        api.setStatus(StringUtils.hasText(createDTO.getStatus()) ? createDTO.getStatus() : "active");
        api.setVersion(StringUtils.hasText(createDTO.getVersion()) ? createDTO.getVersion() : "1.0");
        api.setAuthType(StringUtils.hasText(createDTO.getAuthType()) ? createDTO.getAuthType() : "none");
        api.setAuthConfig(toJson(createDTO.getAuthConfig()));
        api.setTags(toJson(createDTO.getTags()));
        api.setExamples(toJson(createDTO.getExamples()));
        api.setTimeoutSeconds(createDTO.getTimeoutSeconds() != null ? createDTO.getTimeoutSeconds() : 30);
        api.setCreatedBy(currentUserId);
        api.setUpdatedBy(currentUserId);
        api.setCreatedAt(LocalDateTime.now());
        api.setUpdatedAt(LocalDateTime.now());
        api.setIsDeleted(false);

        // 保存到数据库
        int result = apiMapper.insert(api);
        if (result <= 0) {
            throw new RuntimeException("创建接口失败");
        }

        // 返回创建后的接口信息
        return getApiById(api.getApiId(), currentUserId);
    }

    @Override
    @Transactional
    public ApiDTO updateApi(Integer apiId, UpdateApiDTO updateDTO, Integer currentUserId) {
        // 参数校验
        validateUpdateApi(apiId, updateDTO);

        // 检查接口是否存在
        Api api = apiMapper.selectById(apiId);
        if (api == null || api.getIsDeleted()) {
            throw new IllegalArgumentException("接口不存在");
        }

        // 检查权限
        if (!hasApiManagePermission(api, currentUserId)) {
            throw new IllegalArgumentException("权限不足，无法更新接口");
        }

        // 检查接口编码是否重复
        if (StringUtils.hasText(updateDTO.getApiCode()) && 
            !updateDTO.getApiCode().equals(api.getApiCode())) {
            if (apiMapper.checkApiCodeExistsExcludeSelf(
                updateDTO.getApiCode(), api.getModuleId(), apiId) > 0) {
                throw new IllegalArgumentException("接口编码已存在");
            }
        }

        // 更新字段
        if (StringUtils.hasText(updateDTO.getApiCode())) {
            api.setApiCode(updateDTO.getApiCode());
        }
        if (updateDTO.getModuleId() != null) {
            api.setModuleId(updateDTO.getModuleId());
        }
        if (StringUtils.hasText(updateDTO.getName())) {
            api.setName(updateDTO.getName());
        }
        if (StringUtils.hasText(updateDTO.getMethod())) {
            api.setMethod(updateDTO.getMethod().toUpperCase());
        }
        if (StringUtils.hasText(updateDTO.getPath())) {
            api.setPath(updateDTO.getPath());
        }
        if (updateDTO.getBaseUrl() != null) {
            api.setBaseUrl(updateDTO.getBaseUrl());
        }
        if (updateDTO.getRequestParameters() != null) {
            api.setRequestParameters(toJson(updateDTO.getRequestParameters()));
        }
        if (updateDTO.getPathParameters() != null) {
            api.setPathParameters(toJson(updateDTO.getPathParameters()));
        }
        if (updateDTO.getRequestHeaders() != null) {
            api.setRequestHeaders(toJson(updateDTO.getRequestHeaders()));
        }
        if (updateDTO.getRequestBody() != null) {
            api.setRequestBody(updateDTO.getRequestBody());
        }
        if (StringUtils.hasText(updateDTO.getRequestBodyType())) {
            api.setRequestBodyType(updateDTO.getRequestBodyType());
        }
        if (StringUtils.hasText(updateDTO.getResponseBodyType())) {
            api.setResponseBodyType(updateDTO.getResponseBodyType());
        }
        if (updateDTO.getDescription() != null) {
            api.setDescription(updateDTO.getDescription());
        }
        if (StringUtils.hasText(updateDTO.getStatus())) {
            api.setStatus(updateDTO.getStatus());
        }
        if (StringUtils.hasText(updateDTO.getVersion())) {
            api.setVersion(updateDTO.getVersion());
        }
        if (StringUtils.hasText(updateDTO.getAuthType())) {
            api.setAuthType(updateDTO.getAuthType());
        }
        if (updateDTO.getAuthConfig() != null) {
            api.setAuthConfig(toJson(updateDTO.getAuthConfig()));
        }
        if (updateDTO.getTags() != null) {
            api.setTags(toJson(updateDTO.getTags()));
        }
        if (updateDTO.getExamples() != null) {
            api.setExamples(toJson(updateDTO.getExamples()));
        }
        if (updateDTO.getTimeoutSeconds() != null) {
            api.setTimeoutSeconds(updateDTO.getTimeoutSeconds());
        }
        api.setUpdatedBy(currentUserId);
        api.setUpdatedAt(LocalDateTime.now());

        // 更新到数据库
        int result = apiMapper.updateById(api);
        if (result <= 0) {
            throw new RuntimeException("更新接口失败");
        }

        // 返回更新后的接口信息
        return getApiById(apiId, currentUserId);
    }

    @Override
    public ApiDTO getApiById(Integer apiId, Integer currentUserId) {
        // 参数校验
        if (apiId == null) {
            throw new IllegalArgumentException("接口ID不能为空");
        }

        // 查询接口
        Api api = apiMapper.selectById(apiId);
        if (api == null || api.getIsDeleted()) {
            throw new IllegalArgumentException("接口不存在");
        }

        // 转换为DTO
        return convertToDTO(api);
    }

    @Override
    public ApiListResponseDTO getApiList(ApiListQueryDTO queryDTO, Integer currentUserId) {
        // 参数校验和默认值设置
        validateAndSetDefaults(queryDTO);

        // 计算偏移量
        queryDTO.setOffset((queryDTO.getPage() - 1) * queryDTO.getPageSize());

        // 查询列表
        List<Api> apis = apiMapper.selectApiList(queryDTO);
        List<ApiDTO> apiDTOs = new ArrayList<>();
        for (Api api : apis) {
            apiDTOs.add(convertToDTO(api));
        }

        // 查询总数
        Long total = apiMapper.countApiList(queryDTO);

        // 构建响应
        ApiListResponseDTO response = new ApiListResponseDTO();
        response.setTotal(total);
        response.setPage(queryDTO.getPage());
        response.setPageSize(queryDTO.getPageSize());
        response.setTotalPages((int) Math.ceil((double) total / queryDTO.getPageSize()));
        response.setItems(apiDTOs);

        // 如果需要统计信息
        if (Boolean.TRUE.equals(queryDTO.getIncludeStatistics())) {
            response.setStatistics(apiMapper.selectApiStatistics(queryDTO));
        }

        return response;
    }

    @Override
    @Transactional
    public void deleteApi(Integer apiId, Integer currentUserId) {
        // 参数校验
        validateDeleteApi(apiId);

        // 检查接口是否存在
        Api api = apiMapper.selectById(apiId);
        if (api == null) {
            throw new IllegalArgumentException("接口不存在");
        }

        // 检查接口是否已被删除
        if (api.getIsDeleted()) {
            throw new IllegalArgumentException("接口已被删除");
        }

        // 检查权限
        if (!hasApiManagePermission(api, currentUserId)) {
            throw new IllegalArgumentException("权限不足，无法删除接口");
        }

        // 检查是否为系统接口
        if (isSystemApi(api)) {
            throw new IllegalArgumentException("不能删除系统接口");
        }

        // 检查接口是否存在测试用例
        if (hasTestCases(apiId)) {
            throw new IllegalArgumentException("接口存在测试用例，无法删除");
        }

        // 检查接口是否存在前置条件
//        if (hasPreconditions(apiId)) {
//            throw new IllegalArgumentException("接口存在前置条件配置，无法删除");
//        }

        // 检查接口是否正在被使用
        if (isApiInUse(apiId)) {
            throw new IllegalArgumentException("接口正在被测试计划使用，无法删除");
        }

        // 执行软删除
        int result = apiMapper.deleteById(apiId, currentUserId);
        if (result <= 0) {
            throw new RuntimeException("删除接口失败");
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 创建接口参数校验
     */
    private void validateCreateApi(CreateApiDTO createDTO) {
        if (createDTO == null) {
            throw new IllegalArgumentException("创建参数不能为空");
        }
        if (createDTO.getModuleId() == null) {
            throw new IllegalArgumentException("模块ID不能为空");
        }
        if (!StringUtils.hasText(createDTO.getName())) {
            throw new IllegalArgumentException("接口名称不能为空");
        }
        if (!StringUtils.hasText(createDTO.getMethod())) {
            throw new IllegalArgumentException("请求方法不能为空");
        }
        if (!StringUtils.hasText(createDTO.getPath())) {
            throw new IllegalArgumentException("接口路径不能为空");
        }
    }

    /**
     * 更新接口参数校验
     */
    private void validateUpdateApi(Integer apiId, UpdateApiDTO updateDTO) {
        if (apiId == null) {
            throw new IllegalArgumentException("接口ID不能为空");
        }
        if (updateDTO == null) {
            throw new IllegalArgumentException("更新参数不能为空");
        }
    }

    /**
     * 删除接口参数校验
     */
    private void validateDeleteApi(Integer apiId) {
        if (apiId == null) {
            throw new IllegalArgumentException("接口ID不能为空");
        }
    }

    /**
     * 校验查询参数并设置默认值
     */
    private void validateAndSetDefaults(ApiListQueryDTO queryDTO) {
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
            queryDTO.setPage(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
            queryDTO.setPageSize(20);
        }
        if (queryDTO.getPageSize() > 100) {
            queryDTO.setPageSize(100);
        }
        if (!StringUtils.hasText(queryDTO.getSortBy())) {
            queryDTO.setSortBy("created_at");
        }
        if (!StringUtils.hasText(queryDTO.getSortOrder())) {
            queryDTO.setSortOrder("desc");
        }
    }

    /**
     * 生成接口编码
     */
    private String generateApiCode(Integer moduleId) {
        return "API_M" + moduleId + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 检查是否有接口管理权限
     */
    private boolean hasApiManagePermission(Api api, Integer userId) {
        // 规则1：可以管理自己创建的接口
        if (api.getCreatedBy().equals(userId)) {
            return true;
        }

        // 规则2：项目成员可以管理接口
        // TODO: 这里应该检查用户的项目成员权限
        return true;
    }

    /**
     * 检查是否为系统接口
     */
    private boolean isSystemApi(Api api) {
        // 规则1：系统接口通常有特定的编码前缀
        if (StringUtils.hasText(api.getApiCode()) && 
            api.getApiCode().startsWith(Constants.SYSTEM_API_CODE_PREFIX)) {
            return true;
        }

        // 规则2：系统接口通常有特定的名称关键字
        if (StringUtils.hasText(api.getName()) && 
            api.getName().contains(Constants.SYSTEM_API_NAME_KEYWORD)) {
            return true;
        }

        return false;
    }

    /**
     * 检查接口是否存在测试用例
     */
    private boolean hasTestCases(Integer apiId) {
        return testCaseMapper.countByApiId(apiId) > 0;
    }

    /**
     * 检查接口是否存在前置条件
     */
    private boolean hasPreconditions(Integer apiId) {
        return apiMapper.countPreconditionsByApiId(apiId) > 0;
    }

    /**
     * 检查接口是否正在被使用
     */
    private boolean isApiInUse(Integer apiId) {
        // TODO: 检查接口是否正在被测试计划、测试套件等使用
        return false;
    }

    /**
     * 转换为DTO
     */
    private ApiDTO convertToDTO(Api api) {
        ApiDTO dto = new ApiDTO();
        BeanUtils.copyProperties(api, dto);
        
        // 转换JSON字段
        dto.setRequestParameters(fromJson(api.getRequestParameters()));
        dto.setPathParameters(fromJson(api.getPathParameters()));
        dto.setRequestHeaders(fromJson(api.getRequestHeaders()));
        dto.setAuthConfig(fromJson(api.getAuthConfig()));
        dto.setTags(fromJsonToList(api.getTags()));
        dto.setExamples(fromJson(api.getExamples()));

        // 构建完整URL
        if (StringUtils.hasText(api.getBaseUrl()) && StringUtils.hasText(api.getPath())) {
            dto.setFullUrl(api.getBaseUrl() + api.getPath());
        }

        // 查询测试用例数量
        dto.setTestCaseCount(testCaseMapper.countByApiId(api.getApiId()));

        // 查询前置条件数量
        dto.setPreconditionCount(apiMapper.countPreconditionsByApiId(api.getApiId()));

        return dto;
    }

    /**
     * 对象转JSON字符串
     */
    private String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转换失败", e);
        }
    }

    /**
     * JSON字符串转对象
     */
    private Object fromJson(String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * JSON字符串转List
     */
    private List<String> fromJsonToList(String json) {
        if (!StringUtils.hasText(json)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }
}

