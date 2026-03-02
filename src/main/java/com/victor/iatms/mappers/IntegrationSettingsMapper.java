package com.victor.iatms.mappers;

import com.victor.iatms.entity.po.IntegrationSettings;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 集成设置Mapper接口
 */
@Mapper
public interface IntegrationSettingsMapper {
    
    /**
     * 获取所有集成设置
     *
     * @return 集成设置列表
     */
    List<IntegrationSettings> selectAllIntegrationSettings();
    
    /**
     * 根据类型获取集成设置
     *
     * @param integrationType 集成类型
     * @return 集成设置
     */
    IntegrationSettings selectIntegrationSettingsByType(String integrationType);
    
    /**
     * 更新集成设置
     *
     * @param settings 集成设置
     * @return 更新行数
     */
    int updateIntegrationSettings(IntegrationSettings settings);
    
    /**
     * 插入集成设置
     *
     * @param settings 集成设置
     * @return 插入行数
     */
    int insertIntegrationSettings(IntegrationSettings settings);
    
    /**
     * 删除集成设置
     *
     * @param id 主键ID
     * @return 删除行数
     */
    int deleteIntegrationSettings(Integer id);
}

