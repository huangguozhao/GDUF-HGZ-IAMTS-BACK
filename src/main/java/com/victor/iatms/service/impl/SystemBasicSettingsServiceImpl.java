package com.victor.iatms.service.impl;

import com.victor.iatms.entity.po.SystemBasicSettings;
import com.victor.iatms.mappers.SystemBasicSettingsMapper;
import com.victor.iatms.service.SystemBasicSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 系统基本设置服务实现类
 */
@Service
public class SystemBasicSettingsServiceImpl implements SystemBasicSettingsService {
    
    @Autowired
    private SystemBasicSettingsMapper systemBasicSettingsMapper;
    
    @Override
    public SystemBasicSettings getSettings() {
        SystemBasicSettings settings = systemBasicSettingsMapper.selectSystemBasicSettings();
        if (settings == null) {
            // 如果没有设置，返回默认设置
            settings = getDefaultSettings();
            systemBasicSettingsMapper.insertSystemBasicSettings(settings);
        }
        return settings;
    }
    
    @Override
    public boolean updateSettings(SystemBasicSettings settings) {
        SystemBasicSettings existingSettings = systemBasicSettingsMapper.selectSystemBasicSettings();
        if (existingSettings == null) {
            // 如果不存在，则插入
            settings.setId(null);
            return systemBasicSettingsMapper.insertSystemBasicSettings(settings) > 0;
        } else {
            // 如果存在，则更新
            settings.setId(existingSettings.getId());
            return systemBasicSettingsMapper.updateSystemBasicSettings(settings) > 0;
        }
    }
    
    @Override
    public boolean resetToDefaults() {
        SystemBasicSettings existingSettings = systemBasicSettingsMapper.selectSystemBasicSettings();
        if (existingSettings == null) {
            // 如果不存在，则插入默认设置
            SystemBasicSettings defaultSettings = getDefaultSettings();
            return systemBasicSettingsMapper.insertSystemBasicSettings(defaultSettings) > 0;
        } else {
            // 如果存在，则更新为默认设置
            SystemBasicSettings defaultSettings = getDefaultSettings();
            defaultSettings.setId(existingSettings.getId());
            return systemBasicSettingsMapper.updateSystemBasicSettings(defaultSettings) > 0;
        }
    }
    
    /**
     * 获取默认设置
     *
     * @return 默认系统设置
     */
    private SystemBasicSettings getDefaultSettings() {
        SystemBasicSettings settings = new SystemBasicSettings();
        settings.setSystemName("接口自动化管理系统");
        settings.setSystemVersion("V2.5.3");
        settings.setSystemId("API-OPS-202405-1234");
        settings.setSystemDescription("接口自动化管理系统是一个高效的API测试和管理工具");
        settings.setDeploymentMode("private");
        settings.setThemeColor("blue");
        settings.setCustomColor("#2C6FD1");
        settings.setLayout("sidebar");
        settings.setTableDensity("standard");
        settings.setLanguage("zh-CN");
        settings.setThemeMode("light");
        settings.setAnimationEnabled(true);
        settings.setDefaultHomepage("dashboard");
        settings.setDefaultProjectView("list");
        settings.setDefaultPageSize(10);
        settings.setTimeFormat("YYYY-MM-DD HH:mm:ss");
        settings.setAutoSave(true);
        settings.setShowWelcomePage(true);
        settings.setRememberTabs(true);
        settings.setSessionTimeout(30);
        settings.setMaxFailures(5);
        settings.setLockoutDuration(30);
        settings.setTwoFactorAuth(true);
        settings.setIpRestriction("off");
        settings.setPasswordPolicy("高强度 (至少8位,包含大小写字母、数字和特殊字符)");
        settings.setDataRetention("30天");
        settings.setBackupStrategy("daily");
        settings.setLogLevel("debug");
        settings.setLogPath("/var/log/apiops/");
        return settings;
    }
}

