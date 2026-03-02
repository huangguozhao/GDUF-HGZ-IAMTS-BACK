package com.victor.iatms.mappers;

import com.victor.iatms.entity.po.NotificationSettings;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知设置Mapper接口
 */
@Mapper
public interface NotificationSettingsMapper {
    
    /**
     * 获取通知设置
     *
     * @return 通知设置
     */
    NotificationSettings selectNotificationSettings();
    
    /**
     * 更新通知设置
     *
     * @param settings 通知设置
     * @return 更新行数
     */
    int updateNotificationSettings(NotificationSettings settings);
    
    /**
     * 插入通知设置
     *
     * @param settings 通知设置
     * @return 插入行数
     */
    int insertNotificationSettings(NotificationSettings settings);
}

