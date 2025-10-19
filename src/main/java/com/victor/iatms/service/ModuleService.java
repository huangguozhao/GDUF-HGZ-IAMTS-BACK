package com.victor.iatms.service;

import com.victor.iatms.entity.dto.ApiListQueryDTO;
import com.victor.iatms.entity.dto.ApiListResponseDTO;
import com.victor.iatms.entity.dto.CreateModuleDTO;
import com.victor.iatms.entity.dto.CreateModuleResponseDTO;
import com.victor.iatms.entity.dto.UpdateModuleDTO;
import com.victor.iatms.entity.dto.UpdateModuleResponseDTO;

/**
 * 模块服务接口
 */
public interface ModuleService {
    
    /**
     * 创建模块
     * @param createModuleDTO 创建模块请求
     * @param creatorId 创建人ID
     * @return 创建结果
     */
    CreateModuleResponseDTO createModule(CreateModuleDTO createModuleDTO, Integer creatorId);
    
    /**
     * 删除模块
     * @param moduleId 模块ID
     * @param deletedBy 删除人ID
     */
    void deleteModule(Integer moduleId, Integer deletedBy);
    
    /**
     * 修改模块信息
     * @param moduleId 模块ID
     * @param updateModuleDTO 修改模块请求
     * @param updatedBy 更新人ID
     * @return 修改后的模块信息
     */
    UpdateModuleResponseDTO updateModule(Integer moduleId, UpdateModuleDTO updateModuleDTO, Integer updatedBy);
    
    /**
     * 获取接口列表
     * @param queryDTO 查询条件
     * @return 接口列表
     */
    ApiListResponseDTO getApiList(ApiListQueryDTO queryDTO);
}
