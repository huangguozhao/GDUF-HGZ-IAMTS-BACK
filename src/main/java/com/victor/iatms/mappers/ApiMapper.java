package com.victor.iatms.mappers;

import com.victor.iatms.entity.po.Api;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 接口数据访问层
 */
@Mapper
public interface ApiMapper {

    /**
     * 根据接口ID查询接口信息
     * @param apiId 接口ID
     * @return 接口信息
     */
    Api findById(@Param("apiId") Integer apiId);

    /**
     * 根据接口ID和状态查询接口信息
     * @param apiId 接口ID
     * @param status 接口状态
     * @return 接口信息
     */
    Api findByIdAndStatus(@Param("apiId") Integer apiId, @Param("status") String status);
}
