package com.victor.iatms.service.impl;

import com.victor.iatms.entity.constants.Constants;
import com.victor.iatms.entity.dto.CreateEnvironmentConfigDTO;
import com.victor.iatms.entity.dto.EnvironmentConfigDTO;
import com.victor.iatms.entity.dto.EnvironmentConfigListResponseDTO;
import com.victor.iatms.entity.dto.UpdateEnvironmentConfigDTO;
import com.victor.iatms.entity.enums.EnvironmentStatusEnum;
import com.victor.iatms.entity.enums.EnvironmentTypeEnum;
import com.victor.iatms.entity.po.EnvironmentConfig;
import com.victor.iatms.entity.query.EnvironmentConfigQuery;
import com.victor.iatms.mappers.EnvironmentConfigMapper;
import com.victor.iatms.service.EnvironmentConfigService;
import com.victor.iatms.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 环境配置服务实现类
 */
@Slf4j
@Service
public class EnvironmentConfigServiceImpl implements EnvironmentConfigService {
    
    @Autowired
    private EnvironmentConfigMapper environmentConfigMapper;
    
    // 环境编码格式验证：大写字母、小写字母、数字、下划线、中划线
    private static final Pattern ENV_CODE_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+$");
    
    // 环境编码最大长度
    private static final int ENV_CODE_MAX_LENGTH = 50;
    
    // 环境名称最大长度
    private static final int ENV_NAME_MAX_LENGTH = 100;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EnvironmentConfigDTO createEnvironmentConfig(CreateEnvironmentConfigDTO createDTO, Integer creatorId) {
        // 参数校验
        validateCreateEnvironmentConfig(createDTO, creatorId);
        
        // 检查环境编码是否已存在
        if (environmentConfigMapper.checkEnvCodeExists(createDTO.getEnvCode()) > 0) {
            throw new IllegalArgumentException("环境编码已存在");
        }
        
        // 如果设置为默认环境，先清除其他默认环境标记
        if (Boolean.TRUE.equals(createDTO.getIsDefault())) {
            environmentConfigMapper.clearAllDefaultFlags();
        }
        
        // 创建环境配置实体
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setEnvCode(createDTO.getEnvCode());
        environmentConfig.setEnvName(createDTO.getEnvName());
        environmentConfig.setEnvType(createDTO.getEnvType() != null ? createDTO.getEnvType() : "testing");
        environmentConfig.setDescription(createDTO.getDescription());
        environmentConfig.setBaseUrl(createDTO.getBaseUrl());
        environmentConfig.setDomain(createDTO.getDomain());
        environmentConfig.setProtocol(createDTO.getProtocol() != null ? createDTO.getProtocol() : "https");
        environmentConfig.setPort(createDTO.getPort());
        
        // 处理JSON字段
        if (createDTO.getDatabaseConfig() != null) {
            environmentConfig.setDatabaseConfig(JsonUtils.convertObj2Json(createDTO.getDatabaseConfig()));
        }
        if (createDTO.getExternalServices() != null) {
            environmentConfig.setExternalServices(JsonUtils.convertObj2Json(createDTO.getExternalServices()));
        }
        if (createDTO.getVariables() != null) {
            environmentConfig.setVariables(JsonUtils.convertObj2Json(createDTO.getVariables()));
        }
        if (createDTO.getAuthConfig() != null) {
            environmentConfig.setAuthConfig(JsonUtils.convertObj2Json(createDTO.getAuthConfig()));
        }
        if (createDTO.getFeatureFlags() != null) {
            environmentConfig.setFeatureFlags(JsonUtils.convertObj2Json(createDTO.getFeatureFlags()));
        }
        if (createDTO.getPerformanceConfig() != null) {
            environmentConfig.setPerformanceConfig(JsonUtils.convertObj2Json(createDTO.getPerformanceConfig()));
        }
        if (createDTO.getMonitoringConfig() != null) {
            environmentConfig.setMonitoringConfig(JsonUtils.convertObj2Json(createDTO.getMonitoringConfig()));
        }
        if (createDTO.getDeploymentInfo() != null) {
            environmentConfig.setDeploymentInfo(JsonUtils.convertObj2Json(createDTO.getDeploymentInfo()));
        }
        
        environmentConfig.setStatus(createDTO.getStatus() != null ? createDTO.getStatus() : "active");
        environmentConfig.setIsDefault(createDTO.getIsDefault() != null ? createDTO.getIsDefault() : false);
        environmentConfig.setMaintenanceMessage(createDTO.getMaintenanceMessage());
        environmentConfig.setDeployedVersion(createDTO.getDeployedVersion());
        environmentConfig.setCreatedBy(creatorId);
        environmentConfig.setUpdatedBy(creatorId);
        environmentConfig.setCreatedAt(LocalDateTime.now());
        environmentConfig.setUpdatedAt(LocalDateTime.now());
        
        // 插入数据库
        int result = environmentConfigMapper.insert(environmentConfig);
        if (result <= 0) {
            throw new RuntimeException("创建环境配置失败");
        }
        
        // 查询并返回创建的环境配置详情
        return environmentConfigMapper.selectDetailById(environmentConfig.getEnvId());
    }
    
    @Override
    public EnvironmentConfigDTO getEnvironmentConfigById(Integer envId) {
        if (envId == null) {
            throw new IllegalArgumentException("环境ID不能为空");
        }
        
        EnvironmentConfigDTO config = environmentConfigMapper.selectDetailById(envId);
        if (config == null) {
            throw new IllegalArgumentException("环境配置不存在");
        }
        
        return config;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EnvironmentConfigDTO updateEnvironmentConfig(Integer envId, UpdateEnvironmentConfigDTO updateDTO, Integer updaterId) {
        // 参数校验
        validateUpdateEnvironmentConfig(envId, updateDTO, updaterId);
        
        // 检查环境配置是否存在
        EnvironmentConfig existingConfig = environmentConfigMapper.selectById(envId);
        if (existingConfig == null) {
            throw new IllegalArgumentException("环境配置不存在");
        }
        
        // 检查环境编码唯一性（如果修改了编码）
        if (updateDTO.getEnvCode() != null && !updateDTO.getEnvCode().equals(existingConfig.getEnvCode())) {
            if (environmentConfigMapper.checkEnvCodeExistsExcludeSelf(updateDTO.getEnvCode(), envId) > 0) {
                throw new IllegalArgumentException("环境编码已被其他环境使用");
            }
        }
        
        // 如果设置为默认环境，先清除其他默认环境标记
        if (Boolean.TRUE.equals(updateDTO.getIsDefault())) {
            environmentConfigMapper.clearAllDefaultFlags();
        }
        
        // 构建更新实体
        EnvironmentConfig updateConfig = new EnvironmentConfig();
        updateConfig.setEnvId(envId);
        updateConfig.setEnvCode(updateDTO.getEnvCode());
        updateConfig.setEnvName(updateDTO.getEnvName());
        updateConfig.setEnvType(updateDTO.getEnvType());
        updateConfig.setDescription(updateDTO.getDescription());
        updateConfig.setBaseUrl(updateDTO.getBaseUrl());
        updateConfig.setDomain(updateDTO.getDomain());
        updateConfig.setProtocol(updateDTO.getProtocol());
        updateConfig.setPort(updateDTO.getPort());
        
        // 处理JSON字段
        if (updateDTO.getDatabaseConfig() != null) {
            updateConfig.setDatabaseConfig(JsonUtils.convertObj2Json(updateDTO.getDatabaseConfig()));
        }
        if (updateDTO.getExternalServices() != null) {
            updateConfig.setExternalServices(JsonUtils.convertObj2Json(updateDTO.getExternalServices()));
        }
        if (updateDTO.getVariables() != null) {
            updateConfig.setVariables(JsonUtils.convertObj2Json(updateDTO.getVariables()));
        }
        if (updateDTO.getAuthConfig() != null) {
            updateConfig.setAuthConfig(JsonUtils.convertObj2Json(updateDTO.getAuthConfig()));
        }
        if (updateDTO.getFeatureFlags() != null) {
            updateConfig.setFeatureFlags(JsonUtils.convertObj2Json(updateDTO.getFeatureFlags()));
        }
        if (updateDTO.getPerformanceConfig() != null) {
            updateConfig.setPerformanceConfig(JsonUtils.convertObj2Json(updateDTO.getPerformanceConfig()));
        }
        if (updateDTO.getMonitoringConfig() != null) {
            updateConfig.setMonitoringConfig(JsonUtils.convertObj2Json(updateDTO.getMonitoringConfig()));
        }
        if (updateDTO.getDeploymentInfo() != null) {
            updateConfig.setDeploymentInfo(JsonUtils.convertObj2Json(updateDTO.getDeploymentInfo()));
        }
        
        updateConfig.setStatus(updateDTO.getStatus());
        updateConfig.setIsDefault(updateDTO.getIsDefault());
        updateConfig.setMaintenanceMessage(updateDTO.getMaintenanceMessage());
        updateConfig.setDeployedVersion(updateDTO.getDeployedVersion());
        updateConfig.setUpdatedBy(updaterId);
        
        // 执行更新
        int result = environmentConfigMapper.updateById(updateConfig);
        if (result <= 0) {
            throw new RuntimeException("更新环境配置失败");
        }
        
        // 查询并返回更新后的环境配置详情
        return environmentConfigMapper.selectDetailById(envId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEnvironmentConfig(Integer envId, Integer deleterId) {
        if (envId == null) {
            throw new IllegalArgumentException("环境ID不能为空");
        }
        if (deleterId == null) {
            throw new IllegalArgumentException("删除人ID不能为空");
        }
        
        // 检查环境配置是否存在
        EnvironmentConfig config = environmentConfigMapper.selectById(envId);
        if (config == null) {
            throw new IllegalArgumentException("环境配置不存在");
        }
        
        // 检查是否为默认环境
        if (Boolean.TRUE.equals(config.getIsDefault())) {
            throw new IllegalArgumentException("不能删除默认环境，请先设置其他环境为默认环境");
        }
        
        // TODO: 检查是否有项目正在使用该环境
        // 这里可以添加检查ProjectEnvironments表的逻辑
        
        // 执行软删除（这里简化处理，直接物理删除。实际应该添加is_deleted字段）
        // 由于表结构中没有is_deleted字段，这里暂时抛出异常提示
        throw new UnsupportedOperationException("当前版本不支持删除环境配置，请联系管理员");
    }
    
    @Override
    public EnvironmentConfigListResponseDTO getEnvironmentConfigList(EnvironmentConfigQuery query) {
        // 参数校验和默认值设置
        validateAndSetDefaultQuery(query);
        
        // 查询列表
        List<EnvironmentConfigDTO> list = environmentConfigMapper.selectList(query);
        
        // 查询总数
        Integer total = environmentConfigMapper.countList(query);
        
        // 构建响应
        EnvironmentConfigListResponseDTO response = new EnvironmentConfigListResponseDTO();
        response.setTotal(total != null ? total.longValue() : 0L);
        response.setItems(list);
        response.setPage(query.getPage());
        response.setPageSize(query.getPageSize());
        
        return response;
    }
    
    /**
     * 校验创建环境配置参数
     */
    private void validateCreateEnvironmentConfig(CreateEnvironmentConfigDTO createDTO, Integer creatorId) {
        if (createDTO == null) {
            throw new IllegalArgumentException("环境配置信息不能为空");
        }
        if (creatorId == null) {
            throw new IllegalArgumentException("创建人ID不能为空");
        }
        
        // 校验环境编码
        if (!StringUtils.hasText(createDTO.getEnvCode())) {
            throw new IllegalArgumentException("环境编码不能为空");
        }
        if (createDTO.getEnvCode().length() > ENV_CODE_MAX_LENGTH) {
            throw new IllegalArgumentException("环境编码长度不能超过" + ENV_CODE_MAX_LENGTH + "个字符");
        }
        if (!ENV_CODE_PATTERN.matcher(createDTO.getEnvCode()).matches()) {
            throw new IllegalArgumentException("环境编码只能包含字母、数字、下划线和中划线");
        }
        
        // 校验环境名称
        if (!StringUtils.hasText(createDTO.getEnvName())) {
            throw new IllegalArgumentException("环境名称不能为空");
        }
        if (createDTO.getEnvName().length() > ENV_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("环境名称长度不能超过" + ENV_NAME_MAX_LENGTH + "个字符");
        }
        
        // 校验环境类型
        if (createDTO.getEnvType() != null && !EnvironmentTypeEnum.isValid(createDTO.getEnvType())) {
            throw new IllegalArgumentException("环境类型无效");
        }
        
        // 校验环境状态
        if (createDTO.getStatus() != null && !EnvironmentStatusEnum.isValid(createDTO.getStatus())) {
            throw new IllegalArgumentException("环境状态无效");
        }
        
        // 校验协议
        if (createDTO.getProtocol() != null && 
            !"http".equals(createDTO.getProtocol()) && !"https".equals(createDTO.getProtocol())) {
            throw new IllegalArgumentException("协议只能是http或https");
        }
        
        // 校验端口
        if (createDTO.getPort() != null && (createDTO.getPort() < 1 || createDTO.getPort() > 65535)) {
            throw new IllegalArgumentException("端口号必须在1-65535之间");
        }
    }
    
    /**
     * 校验更新环境配置参数
     */
    private void validateUpdateEnvironmentConfig(Integer envId, UpdateEnvironmentConfigDTO updateDTO, Integer updaterId) {
        if (envId == null) {
            throw new IllegalArgumentException("环境ID不能为空");
        }
        if (updateDTO == null) {
            throw new IllegalArgumentException("更新信息不能为空");
        }
        if (updaterId == null) {
            throw new IllegalArgumentException("更新人ID不能为空");
        }
        
        // 校验环境编码
        if (updateDTO.getEnvCode() != null) {
            if (updateDTO.getEnvCode().trim().isEmpty()) {
                throw new IllegalArgumentException("环境编码不能为空");
            }
            if (updateDTO.getEnvCode().length() > ENV_CODE_MAX_LENGTH) {
                throw new IllegalArgumentException("环境编码长度不能超过" + ENV_CODE_MAX_LENGTH + "个字符");
            }
            if (!ENV_CODE_PATTERN.matcher(updateDTO.getEnvCode()).matches()) {
                throw new IllegalArgumentException("环境编码只能包含字母、数字、下划线和中划线");
            }
        }
        
        // 校验环境名称
        if (updateDTO.getEnvName() != null) {
            if (updateDTO.getEnvName().trim().isEmpty()) {
                throw new IllegalArgumentException("环境名称不能为空");
            }
            if (updateDTO.getEnvName().length() > ENV_NAME_MAX_LENGTH) {
                throw new IllegalArgumentException("环境名称长度不能超过" + ENV_NAME_MAX_LENGTH + "个字符");
            }
        }
        
        // 校验环境类型
        if (updateDTO.getEnvType() != null && !EnvironmentTypeEnum.isValid(updateDTO.getEnvType())) {
            throw new IllegalArgumentException("环境类型无效");
        }
        
        // 校验环境状态
        if (updateDTO.getStatus() != null && !EnvironmentStatusEnum.isValid(updateDTO.getStatus())) {
            throw new IllegalArgumentException("环境状态无效");
        }
        
        // 校验协议
        if (updateDTO.getProtocol() != null && 
            !"http".equals(updateDTO.getProtocol()) && !"https".equals(updateDTO.getProtocol())) {
            throw new IllegalArgumentException("协议只能是http或https");
        }
        
        // 校验端口
        if (updateDTO.getPort() != null && (updateDTO.getPort() < 1 || updateDTO.getPort() > 65535)) {
            throw new IllegalArgumentException("端口号必须在1-65535之间");
        }
    }
    
    /**
     * 校验查询参数并设置默认值
     */
    private void validateAndSetDefaultQuery(EnvironmentConfigQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("查询参数不能为空");
        }
        
        // 设置默认分页参数
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        if (query.getPageSize() > 100) {
            query.setPageSize(100);
        }
        
        // 计算偏移量
        int offset = (query.getPage() - 1) * query.getPageSize();
        query.setOffset(offset);
        
        // 设置默认排序
        if (!StringUtils.hasText(query.getSortBy())) {
            query.setSortBy("created_at");
        }
        if (!StringUtils.hasText(query.getSortOrder())) {
            query.setSortOrder("desc");
        }
    }
}

