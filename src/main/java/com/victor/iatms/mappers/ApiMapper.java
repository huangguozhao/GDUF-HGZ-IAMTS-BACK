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
}