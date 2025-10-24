package com.victor.iatms.service;

import com.victor.iatms.entity.dto.*;

/**
 * 接口服务接口
 */
public interface ApiService {

    /**
     * 创建接口
     * @param createDTO 创建接口DTO
     * @param currentUserId 当前操作用户ID
     * @return 创建后的接口信息
     */
    ApiDTO createApi(CreateApiDTO createDTO, Integer currentUserId);

    /**
     * 更新接口
     * @param apiId 接口ID
     * @param updateDTO 更新接口DTO
     * @param currentUserId 当前操作用户ID
     * @return 更新后的接口信息
     */
    ApiDTO updateApi(Integer apiId, UpdateApiDTO updateDTO, Integer currentUserId);

    /**
     * 根据ID查询接口
     * @param apiId 接口ID
     * @param currentUserId 当前用户ID
     * @return 接口信息
     */
    ApiDTO getApiById(Integer apiId, Integer currentUserId);

    /**
     * 分页查询接口列表
     * @param queryDTO 查询参数
     * @param currentUserId 当前用户ID
     * @return 接口列表
     */
    ApiListResponseDTO getApiList(ApiListQueryDTO queryDTO, Integer currentUserId);

    /**
     * 删除接口
     * @param apiId 接口ID
     * @param currentUserId 当前操作用户ID
     */
    void deleteApi(Integer apiId, Integer currentUserId);
}
