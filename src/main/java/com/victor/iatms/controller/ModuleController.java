package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.dto.ApiListQueryDTO;
import com.victor.iatms.entity.dto.ApiListResponseDTO;
import com.victor.iatms.entity.dto.CreateModuleDTO;
import com.victor.iatms.entity.dto.CreateModuleResponseDTO;
import com.victor.iatms.entity.dto.UpdateModuleDTO;
import com.victor.iatms.entity.dto.UpdateModuleResponseDTO;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 模块控制器
 */
@RestController
@RequestMapping("/modules")
public class ModuleController {
    
    @Autowired
    private ModuleService moduleService;
    
    /**
     * 创建模块
     * 
     * @param createModuleDTO 创建模块请求
     * @return 创建结果
     */
    @PostMapping
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<CreateModuleResponseDTO> createModule(@RequestBody CreateModuleDTO createModuleDTO) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer creatorId = 1; // 临时硬编码，实际应该从认证上下文获取
            
            CreateModuleResponseDTO result = moduleService.createModule(createModuleDTO, creatorId);
            return ResponseVO.success("模块创建成功", result);
            
        } catch (IllegalArgumentException e) {
            // 根据不同的错误类型返回不同的错误响应
            if (e.getMessage().contains("模块编码已存在")) {
                return ResponseVO.businessError(e.getMessage());
            } else if (e.getMessage().contains("项目不存在") || 
                      e.getMessage().contains("父模块不存在") ||
                      e.getMessage().contains("负责人不存在")) {
                return ResponseVO.businessError(e.getMessage());
            } else if (e.getMessage().contains("权限不足")) {
                return ResponseVO.forbidden(e.getMessage());
            } else {
                return ResponseVO.paramError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("创建模块失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除模块
     * 
     * @param moduleId 模块ID
     * @return 删除结果
     */
    @DeleteMapping("/{moduleId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Void> deleteModule(@PathVariable("moduleId") Integer moduleId) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer deletedBy = 1; // 临时硬编码，实际应该从认证上下文获取
            
            moduleService.deleteModule(moduleId, deletedBy);
            return ResponseVO.success("模块删除成功", null);
            
        } catch (IllegalArgumentException e) {
            // 根据不同的错误类型返回不同的错误响应
            if (e.getMessage().contains("模块不存在")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("模块已被删除") ||
                      e.getMessage().contains("模块存在子模块") ||
                      e.getMessage().contains("模块存在接口数据") ||
                      e.getMessage().contains("模块正在被使用") ||
                      e.getMessage().contains("不能删除系统模块")) {
                return ResponseVO.businessError(e.getMessage());
            } else if (e.getMessage().contains("权限不足")) {
                return ResponseVO.forbidden(e.getMessage());
            } else {
                return ResponseVO.paramError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("删除模块失败：" + e.getMessage());
        }
    }
    
    /**
     * 修改模块信息
     * 
     * @param moduleId 模块ID
     * @param updateModuleDTO 修改模块请求
     * @return 修改结果
     */
    @PutMapping("/{moduleId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<UpdateModuleResponseDTO> updateModule(
            @PathVariable("moduleId") Integer moduleId,
            @RequestBody UpdateModuleDTO updateModuleDTO) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer updatedBy = 1; // 临时硬编码，实际应该从认证上下文获取
            
            UpdateModuleResponseDTO result = moduleService.updateModule(moduleId, updateModuleDTO, updatedBy);
            return ResponseVO.success("模块信息更新成功", result);
            
        } catch (IllegalArgumentException e) {
            // 根据不同的错误类型返回不同的错误响应
            if (e.getMessage().contains("模块不存在")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("模块已被删除") ||
                      e.getMessage().contains("模块编码已被其他模块使用") ||
                      e.getMessage().contains("指定的父模块不存在") ||
                      e.getMessage().contains("不能将模块设置为自己的父模块") ||
                      e.getMessage().contains("检测到循环引用") ||
                      e.getMessage().contains("指定的负责人不存在")) {
                return ResponseVO.businessError(e.getMessage());
            } else if (e.getMessage().contains("权限不足") ||
                      e.getMessage().contains("不能修改系统模块")) {
                return ResponseVO.forbidden(e.getMessage());
            } else {
                return ResponseVO.paramError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("修改模块信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取接口列表
     * 
     * @param moduleId 模块ID
     * @param method 请求方法过滤
     * @param status 接口状态过滤
     * @param tags 标签过滤
     * @param authType 认证类型过滤
     * @param searchKeyword 关键字搜索
     * @param includeDeleted 是否包含已删除的接口
     * @param includeStatistics 是否包含统计信息
     * @param sortBy 排序字段
     * @param sortOrder 排序顺序
     * @param page 页码
     * @param pageSize 每页条数
     * @return 接口列表
     */
    @GetMapping("/{moduleId}/apis")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<ApiListResponseDTO> getApiList(
            @PathVariable("moduleId") Integer moduleId,
            @RequestParam(value = "method", required = false) String method,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "auth_type", required = false) String authType,
            @RequestParam(value = "search_keyword", required = false) String searchKeyword,
            @RequestParam(value = "include_deleted", required = false) Boolean includeDeleted,
            @RequestParam(value = "include_statistics", required = false) Boolean includeStatistics,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort_order", required = false) String sortOrder,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "page_size", required = false) Integer pageSize) {
        try {
            ApiListQueryDTO queryDTO = new ApiListQueryDTO();
            queryDTO.setModuleId(moduleId);
            queryDTO.setMethod(method);
            queryDTO.setStatus(status);
            queryDTO.setTags(tags);
            queryDTO.setAuthType(authType);
            queryDTO.setSearchKeyword(searchKeyword);
            queryDTO.setIncludeDeleted(includeDeleted);
            queryDTO.setIncludeStatistics(includeStatistics);
            queryDTO.setSortBy(sortBy);
            queryDTO.setSortOrder(sortOrder);
            queryDTO.setPage(page);
            queryDTO.setPageSize(pageSize);

            ApiListResponseDTO result = moduleService.getApiList(queryDTO);
            return ResponseVO.success("查询接口列表成功", result);

        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("模块不存在")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("权限不足")) {
                return ResponseVO.forbidden(e.getMessage());
            } else {
                return ResponseVO.paramError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("查询接口列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取模块统计数据
     * 
     * @param moduleId 模块ID
     * @return 模块统计信息
     */
    @GetMapping("/{moduleId}/statistics")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<com.victor.iatms.entity.dto.ModuleStatisticsDTO> getModuleStatistics(
            @PathVariable("moduleId") Integer moduleId) {
        try {
            com.victor.iatms.entity.dto.ModuleStatisticsDTO statistics = moduleService.getModuleStatistics(moduleId);
            return ResponseVO.success("查询模块统计数据成功", statistics);
            
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("模块不存在")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("已被删除")) {
                return ResponseVO.businessError(e.getMessage());
            } else {
                return ResponseVO.paramError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("查询模块统计数据失败：" + e.getMessage());
        }
    }
}
