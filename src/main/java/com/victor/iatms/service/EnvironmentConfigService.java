package com.victor.iatms.service;

import com.victor.iatms.entity.dto.CreateEnvironmentConfigDTO;
import com.victor.iatms.entity.dto.EnvironmentConfigDTO;
import com.victor.iatms.entity.dto.EnvironmentConfigListResponseDTO;
import com.victor.iatms.entity.dto.UpdateEnvironmentConfigDTO;
import com.victor.iatms.entity.query.EnvironmentConfigQuery;

/**
 * 环境配置服务接口
 */
public interface EnvironmentConfigService {
    
    /**
     * 创建环境配置
     */
    EnvironmentConfigDTO createEnvironmentConfig(CreateEnvironmentConfigDTO createDTO, Integer creatorId);
    
    /**
     * 根据ID查询环境配置详情
     */
    EnvironmentConfigDTO getEnvironmentConfigById(Integer envId);
    
    /**
     * 更新环境配置
     */
    EnvironmentConfigDTO updateEnvironmentConfig(Integer envId, UpdateEnvironmentConfigDTO updateDTO, Integer updaterId);
    
    /**
     * 删除环境配置（逻辑删除）
     */
    void deleteEnvironmentConfig(Integer envId, Integer deleterId);
    
    /**
     * 查询环境配置列表
     */
    EnvironmentConfigListResponseDTO getEnvironmentConfigList(EnvironmentConfigQuery query);
}

