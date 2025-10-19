package com.victor.iatms.mappers;

import com.victor.iatms.entity.dto.ApiListQueryDTO;
import com.victor.iatms.entity.dto.ApiSummaryDTO;
import com.victor.iatms.entity.dto.CreateModuleResponseDTO;
import com.victor.iatms.entity.dto.UpdateModuleResponseDTO;
import com.victor.iatms.entity.po.Module;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模块Mapper接口
 */
@Mapper
public interface ModuleMapper {
    
    /**
     * 插入模块
     * @param module 模块信息
     * @return 影响行数
     */
    int insert(Module module);
    
    /**
     * 根据ID查询模块
     * @param moduleId 模块ID
     * @return 模块信息
     */
    Module selectById(@Param("moduleId") Integer moduleId);
    
    /**
     * 检查模块编码是否存在
     * @param moduleCode 模块编码
     * @param projectId 项目ID
     * @return 存在数量
     */
    int checkModuleCodeExists(@Param("moduleCode") String moduleCode, @Param("projectId") Integer projectId);
    
    /**
     * 根据模块ID获取模块详情（包含关联信息）
     * @param moduleId 模块ID
     * @return 模块详情
     */
    CreateModuleResponseDTO selectModuleDetailById(@Param("moduleId") Integer moduleId);
    
    /**
     * 软删除模块
     * @param moduleId 模块ID
     * @param deletedBy 删除人ID
     * @return 影响行数
     */
    int deleteById(@Param("moduleId") Integer moduleId, @Param("deletedBy") Integer deletedBy);
    
    /**
     * 统计子模块数量
     * @param moduleId 模块ID
     * @return 子模块数量
     */
    int countChildModules(@Param("moduleId") Integer moduleId);
    
    /**
     * 统计模块下的接口数量
     * @param moduleId 模块ID
     * @return 接口数量
     */
    int countModuleApis(@Param("moduleId") Integer moduleId);
    
    /**
     * 检查模块是否正在被使用
     * @param moduleId 模块ID
     * @return 是否正在被使用
     */
    boolean isModuleInUse(@Param("moduleId") Integer moduleId);
    
    /**
     * 更新模块信息
     * @param module 模块信息
     * @return 影响行数
     */
    int updateById(Module module);
    
    /**
     * 检查模块编码在指定项目下是否存在（排除指定模块）
     * @param moduleCode 模块编码
     * @param projectId 项目ID
     * @param excludeModuleId 排除的模块ID
     * @return 存在数量
     */
    int checkModuleCodeExistsExcludeSelf(@Param("moduleCode") String moduleCode, 
                                       @Param("projectId") Integer projectId, 
                                       @Param("excludeModuleId") Integer excludeModuleId);
    
    /**
     * 根据模块ID获取更新后的模块详情（包含关联信息）
     * @param moduleId 模块ID
     * @return 模块详情
     */
    UpdateModuleResponseDTO selectUpdateModuleDetailById(@Param("moduleId") Integer moduleId);
    
    /**
     * 查询接口列表
     * @param queryDTO 查询条件
     * @return 接口列表
     */
    List<com.victor.iatms.entity.dto.ApiDTO> selectApiList(ApiListQueryDTO queryDTO);
    
    /**
     * 统计接口列表总数
     * @param queryDTO 查询条件
     * @return 总数
     */
    Integer countApiList(ApiListQueryDTO queryDTO);
    
    /**
     * 查询接口统计摘要
     * @param moduleId 模块ID
     * @return 统计摘要
     */
    ApiSummaryDTO selectApiSummary(@Param("moduleId") Integer moduleId);
}
