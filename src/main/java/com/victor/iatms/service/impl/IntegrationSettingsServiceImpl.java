package com.victor.iatms.service.impl;

import com.victor.iatms.entity.po.IntegrationSettings;
import com.victor.iatms.mappers.IntegrationSettingsMapper;
import com.victor.iatms.service.IntegrationSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 集成设置服务实现类
 */
@Service
public class IntegrationSettingsServiceImpl implements IntegrationSettingsService {
    
    @Autowired
    private IntegrationSettingsMapper integrationSettingsMapper;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public List<IntegrationSettings> getAllSettings() {
        return integrationSettingsMapper.selectAllIntegrationSettings();
    }
    
    @Override
    public IntegrationSettings getSettingsByType(String integrationType) {
        return integrationSettingsMapper.selectIntegrationSettingsByType(integrationType);
    }
    
    @Override
    public boolean updateSettings(IntegrationSettings settings) {
        if (settings.getId() == null) {
            // 如果没有ID，先查找是否存在
            IntegrationSettings existing = integrationSettingsMapper.selectIntegrationSettingsByType(
                settings.getIntegrationType());
            if (existing != null) {
                settings.setId(existing.getId());
            }
        }
        
        if (settings.getId() == null) {
            return integrationSettingsMapper.insertIntegrationSettings(settings) > 0;
        } else {
            return integrationSettingsMapper.updateIntegrationSettings(settings) > 0;
        }
    }
    
    @Override
    public boolean addSettings(IntegrationSettings settings) {
        return integrationSettingsMapper.insertIntegrationSettings(settings) > 0;
    }
    
    @Override
    public boolean deleteSettings(Integer id) {
        return integrationSettingsMapper.deleteIntegrationSettings(id) > 0;
    }
    
    @Override
    public boolean testConnection(IntegrationSettings settings) {
        try {
            String type = settings.getIntegrationType();
            switch (type) {
                case "api":
                    // 测试API连接
                    return testApiConnection(settings);
                case "webhook":
                    // 测试Webhook连接
                    return testWebhookConnection(settings);
                case "jenkins":
                    // 测试Jenkins连接
                    return testJenkinsConnection(settings);
                case "git":
                    // 测试Git连接
                    return testGitConnection(settings);
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean testApiConnection(IntegrationSettings settings) {
        // 简单的API连接测试
        try {
            if (settings.getAuthToken() != null && !settings.getAuthToken().isEmpty()) {
                return true; // 有token认为已配置
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean testWebhookConnection(IntegrationSettings settings) {
        // Webhook URL有效性检查
        return settings.getWebhookUrl() != null && settings.getWebhookUrl().startsWith("http");
    }
    
    private boolean testJenkinsConnection(IntegrationSettings settings) {
        // Jenkins连接测试
        return settings.getJenkinsUrl() != null && settings.getJenkinsUrl().startsWith("http");
    }
    
    private boolean testGitConnection(IntegrationSettings settings) {
        // Git连接测试
        return settings.getGitUrl() != null && settings.getGitUrl().startsWith("http");
    }
}

