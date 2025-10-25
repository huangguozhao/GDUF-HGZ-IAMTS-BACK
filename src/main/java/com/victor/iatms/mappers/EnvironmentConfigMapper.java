package com.victor.iatms.mappers;

import com.victor.iatms.entity.dto.EnvironmentConfigDTO;
import com.victor.iatms.entity.po.EnvironmentConfig;
import com.victor.iatms.entity.query.EnvironmentConfigQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 环境配置Mapper接口
 */
@Mapper
public interface EnvironmentConfigMapper {
    
    /**
     * 插入环境配置
     */
    int insert(EnvironmentConfig environmentConfig);
    
    /**
     * 根据ID查询环境配置
     */
    EnvironmentConfig selectById(@Param("envId") Integer envId);
    
    /**
     * 根据ID查询环境配置详情（包含创建人、更新人信息）
     */
    EnvironmentConfigDTO selectDetailById(@Param("envId") Integer envId);
    
    /**
     * 更新环境配置
     */
    int updateById(EnvironmentConfig environmentConfig);
    
    /**
     * 查询环境配置列表
     */
    List<EnvironmentConfigDTO> selectList(EnvironmentConfigQuery query);
    
    /**
     * 统计环境配置总数
     */
    Integer countList(EnvironmentConfigQuery query);
    
    /**
     * 检查环境编码是否存在
     */
    Integer checkEnvCodeExists(@Param("envCode") String envCode);
    
    /**
     * 检查环境编码是否存在（排除自身）
     */
    Integer checkEnvCodeExistsExcludeSelf(@Param("envCode") String envCode, @Param("envId") Integer envId);
    
    /**
     * 检查是否存在默认环境
     */
    Integer checkDefaultExists();
    
    /**
     * 检查是否存在默认环境（排除自身）
     */
    Integer checkDefaultExistsExcludeSelf(@Param("envId") Integer envId);
    
    /**
     * 清除所有默认环境标记
     */
    int clearAllDefaultFlags();
}

