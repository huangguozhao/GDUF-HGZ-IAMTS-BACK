package com.victor.iatms.service;

/**
 * 接口服务接口
 */
public interface ApiService {

    /**
     * 删除接口
     * @param apiId 接口ID
     * @param currentUserId 当前操作用户ID
     */
    void deleteApi(Integer apiId, Integer currentUserId);
}
