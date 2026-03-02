package com.victor.iatms.service;

import com.victor.iatms.entity.po.SystemBasicSettings;

/**
 * 系统基本设置服务接口
 */
public interface SystemBasicSettingsService {
    
    /**
     * 获取系统基本设置
     *
     * @return 系统基本设置
     */
    SystemBasicSettings getSettings();
    
    /**
     * 更新系统基本设置
     *
     * @param settings 系统基本设置
     * @return 是否成功
     */
    boolean updateSettings(SystemBasicSettings settings);
    
    /**
     * 重置为默认设置
     *
     * @return 是否成功
     */
    boolean resetToDefaults();
}

