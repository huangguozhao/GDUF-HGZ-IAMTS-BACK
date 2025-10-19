package com.victor.iatms.mappers;

import com.victor.iatms.entity.po.Api;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 接口Mapper接口
 */
@Mapper
public interface ApiMapper {

    /**
     * 根据ID查询接口
     * @param apiId 接口ID
     * @return 接口信息
     */
    Api selectById(@Param("apiId") Integer apiId);
    
    /**
     * 软删除接口
     * @param apiId 接口ID
     * @param deletedBy 删除人ID
     * @return 影响行数
     */
    int deleteById(@Param("apiId") Integer apiId, @Param("deletedBy") Integer deletedBy);
    
    /**
     * 统计接口下的前置条件数量
     * @param apiId 接口ID
     * @return 前置条件数量
     */
    int countPreconditionsByApiId(@Param("apiId") Integer apiId);
}