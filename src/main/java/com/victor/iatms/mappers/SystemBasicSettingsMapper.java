package com.victor.iatms.mappers;

import com.victor.iatms.entity.po.SystemBasicSettings;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统基本设置Mapper接口
 */
@Mapper
public interface SystemBasicSettingsMapper {
    
    /**
     * 获取系统基本设置
     *
     * @return 系统基本设置
     */
    SystemBasicSettings selectSystemBasicSettings();
    
    /**
     * 更新系统基本设置
     *
     * @param settings 系统基本设置
     * @return 更新行数
     */
    int updateSystemBasicSettings(SystemBasicSettings settings);
    
    /**
     * 插入系统基本设置
     *
     * @param settings 系统基本设置
     * @return 插入行数
     */
    int insertSystemBasicSettings(SystemBasicSettings settings);
}

