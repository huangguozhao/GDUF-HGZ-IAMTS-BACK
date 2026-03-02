package com.victor.iatms.service;

import com.victor.iatms.entity.po.NotificationSettings;

/**
 * 通知设置服务接口
 */
public interface NotificationSettingsService {
    
    /**
     * 获取通知设置
     *
     * @return 通知设置
     */
    NotificationSettings getSettings();
    
    /**
     * 更新通知设置
     *
     * @param settings 通知设置
     * @return 是否成功
     */
    boolean updateSettings(NotificationSettings settings);
    
    /**
     * 测试邮件配置
     *
     * @param settings 邮件配置
     * @return 是否成功
     */
    boolean testEmail(NotificationSettings settings);
    
    /**
     * 测试短信配置
     *
     * @param settings 短信配置
     * @return 是否成功
     */
    boolean testSms(NotificationSettings settings);
}

