package com.victor.iatms.mappers;

import com.victor.iatms.entity.dto.ApiListQueryDTO;
import com.victor.iatms.entity.dto.ApiStatisticsDTO;
import com.victor.iatms.entity.po.Api;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 接口Mapper接口
 */
@Mapper
public interface ApiMapper {

    /**
     * 插入接口
     * @param api 接口信息
     * @return 影响行数
     */
    int insert(Api api);

    /**
     * 更新接口
     * @param api 接口信息
     * @return 影响行数
     */
    int updateById(Api api);

    /**
     * 根据ID查询接口
     * @param apiId 接口ID
     * @return 接口信息
     */
    Api selectById(@Param("apiId") Integer apiId);
    
    /**
     * 分页查询接口列表
     * @param queryDTO 查询参数
     * @return 接口列表
     */
    List<Api> selectApiList(@Param("queryDTO") ApiListQueryDTO queryDTO);

    /**
     * 统计接口列表总数
     * @param queryDTO 查询参数
     * @return 总数
     */
    Long countApiList(@Param("queryDTO") ApiListQueryDTO queryDTO);

    /**
     * 查询接口统计信息
     * @param queryDTO 查询参数
     * @return 统计信息
     */
    ApiStatisticsDTO selectApiStatistics(@Param("queryDTO") ApiListQueryDTO queryDTO);

    /**
     * 检查接口编码是否存在
     * @param apiCode 接口编码
     * @param moduleId 模块ID
     * @return 存在的数量
     */
    int checkApiCodeExists(@Param("apiCode") String apiCode, @Param("moduleId") Integer moduleId);

    /**
     * 检查接口编码是否存在（排除自己）
     * @param apiCode 接口编码
     * @param moduleId 模块ID
     * @param apiId 要排除的接口ID
     * @return 存在的数量
     */
    int checkApiCodeExistsExcludeSelf(@Param("apiCode") String apiCode, 
                                       @Param("moduleId") Integer moduleId, 
                                       @Param("apiId") Integer apiId);
    
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