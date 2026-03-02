package com.victor.iatms.service;

import com.victor.iatms.entity.po.IntegrationSettings;
import java.util.List;

/**
 * 集成设置服务接口
 */
public interface IntegrationSettingsService {
    
    /**
     * 获取所有集成设置
     *
     * @return 集成设置列表
     */
    List<IntegrationSettings> getAllSettings();
    
    /**
     * 根据类型获取集成设置
     *
     * @param integrationType 集成类型
     * @return 集成设置
     */
    IntegrationSettings getSettingsByType(String integrationType);
    
    /**
     * 更新集成设置
     *
     * @param settings 集成设置
     * @return 是否成功
     */
    boolean updateSettings(IntegrationSettings settings);
    
    /**
     * 添加集成设置
     *
     * @param settings 集成设置
     * @return 是否成功
     */
    boolean addSettings(IntegrationSettings settings);
    
    /**
     * 删除集成设置
     *
     * @param id 主键ID
     * @return 是否成功
     */
    boolean deleteSettings(Integer id);
    
    /**
     * 测试集成连接
     *
     * @param settings 集成设置
     * @return 是否成功
     */
    boolean testConnection(IntegrationSettings settings);
}

