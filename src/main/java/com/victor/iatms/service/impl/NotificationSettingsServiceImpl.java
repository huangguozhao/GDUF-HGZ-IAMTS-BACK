package com.victor.iatms.service.impl;

import com.victor.iatms.entity.po.NotificationSettings;
import com.victor.iatms.mappers.NotificationSettingsMapper;
import com.victor.iatms.service.NotificationSettingsService;
import com.victor.iatms.utils.EmailUtils;
import com.victor.iatms.utils.SmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 通知设置服务实现类
 */
@Service
public class NotificationSettingsServiceImpl implements NotificationSettingsService {
    
    @Autowired
    private NotificationSettingsMapper notificationSettingsMapper;
    
    @Autowired
    private EmailUtils emailUtils;
    
    @Autowired
    private SmsUtils smsUtils;
    
    @Override
    public NotificationSettings getSettings() {
        NotificationSettings settings = notificationSettingsMapper.selectNotificationSettings();
        if (settings == null) {
            // 如果没有设置，返回默认设置
            settings = getDefaultSettings();
            notificationSettingsMapper.insertNotificationSettings(settings);
        }
        return settings;
    }
    
    @Override
    public boolean updateSettings(NotificationSettings settings) {
        NotificationSettings existingSettings = notificationSettingsMapper.selectNotificationSettings();
        if (existingSettings == null) {
            // 如果不存在，则插入
            settings.setId(null);
            return notificationSettingsMapper.insertNotificationSettings(settings) > 0;
        } else {
            // 如果存在，则更新
            settings.setId(existingSettings.getId());
            return notificationSettingsMapper.updateNotificationSettings(settings) > 0;
        }
    }
    
    @Override
    public boolean testEmail(NotificationSettings settings) {
        try {
            return emailUtils.testConnection(settings.getEmailHost(), settings.getEmailPort(),
                settings.getEmailUsername(), settings.getEmailPassword(), settings.getEmailSsl());
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean testSms(NotificationSettings settings) {
        try {
            return smsUtils.testConnection(settings.getSmsProvider(), settings.getSmsAccessKey(),
                settings.getSmsSecretKey());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取默认设置
     *
     * @return 默认通知设置
     */
    private NotificationSettings getDefaultSettings() {
        NotificationSettings settings = new NotificationSettings();
        settings.setEmailEnabled(true);
        settings.setSmsEnabled(false);
        settings.setSystemEnabled(true);
        settings.setEmailHost("");
        settings.setEmailPort(587);
        settings.setEmailUsername("");
        settings.setEmailPassword("");
        settings.setEmailSsl(true);
        settings.setEmailFrom("");
        settings.setSmsProvider("aliyun");
        settings.setSmsAccessKey("");
        settings.setSmsSecretKey("");
        settings.setSmsSignName("");
        return settings;
    }
}

