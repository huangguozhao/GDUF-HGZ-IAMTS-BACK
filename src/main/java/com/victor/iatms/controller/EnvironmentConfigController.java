package com.victor.iatms.controller;

import com.victor.iatms.entity.dto.CreateEnvironmentConfigDTO;
import com.victor.iatms.entity.dto.EnvironmentConfigDTO;
import com.victor.iatms.entity.dto.EnvironmentConfigListResponseDTO;
import com.victor.iatms.entity.dto.UpdateEnvironmentConfigDTO;
import com.victor.iatms.entity.query.EnvironmentConfigQuery;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.EnvironmentConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 环境配置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/environments")
public class EnvironmentConfigController {
    
    @Autowired
    private EnvironmentConfigService environmentConfigService;
    
    /**
     * 创建环境配置
     */
    @PostMapping
    public ResponseVO<EnvironmentConfigDTO> createEnvironmentConfig(
            @RequestBody CreateEnvironmentConfigDTO createDTO) {
        try {
            // TODO: 从当前登录用户上下文获取用户ID
            Integer creatorId = 1; // 临时使用固定值
            
            EnvironmentConfigDTO result = environmentConfigService.createEnvironmentConfig(createDTO, creatorId);
            return ResponseVO.success("创建环境配置成功", result);
        } catch (IllegalArgumentException e) {
            log.warn("创建环境配置参数错误: {}", e.getMessage());
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            log.error("创建环境配置失败", e);
            return ResponseVO.serverError("创建环境配置失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID查询环境配置详情
     */
    @GetMapping("/{envId}")
    public ResponseVO<EnvironmentConfigDTO> getEnvironmentConfigById(@PathVariable Integer envId) {
        try {
            EnvironmentConfigDTO result = environmentConfigService.getEnvironmentConfigById(envId);
            return ResponseVO.success("查询环境配置成功", result);
        } catch (IllegalArgumentException e) {
            log.warn("查询环境配置参数错误: {}", e.getMessage());
            return ResponseVO.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("查询环境配置失败", e);
            return ResponseVO.serverError("查询环境配置失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新环境配置
     */
    @PutMapping("/{envId}")
    public ResponseVO<EnvironmentConfigDTO> updateEnvironmentConfig(
            @PathVariable Integer envId,
            @RequestBody UpdateEnvironmentConfigDTO updateDTO) {
        try {
            // TODO: 从当前登录用户上下文获取用户ID
            Integer updaterId = 1; // 临时使用固定值
            
            EnvironmentConfigDTO result = environmentConfigService.updateEnvironmentConfig(envId, updateDTO, updaterId);
            return ResponseVO.success("更新环境配置成功", result);
        } catch (IllegalArgumentException e) {
            log.warn("更新环境配置参数错误: {}", e.getMessage());
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            log.error("更新环境配置失败", e);
            return ResponseVO.serverError("更新环境配置失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除环境配置
     */
    @DeleteMapping("/{envId}")
    public ResponseVO<Void> deleteEnvironmentConfig(@PathVariable Integer envId) {
        try {
            // TODO: 从当前登录用户上下文获取用户ID
            Integer deleterId = 1; // 临时使用固定值
            
            environmentConfigService.deleteEnvironmentConfig(envId, deleterId);
            return ResponseVO.success("删除环境配置成功", null);
        } catch (IllegalArgumentException e) {
            log.warn("删除环境配置参数错误: {}", e.getMessage());
            return ResponseVO.paramError(e.getMessage());
        } catch (UnsupportedOperationException e) {
            log.warn("删除环境配置不支持: {}", e.getMessage());
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            log.error("删除环境配置失败", e);
            return ResponseVO.serverError("删除环境配置失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询环境配置列表
     */
    @GetMapping
    public ResponseVO<EnvironmentConfigListResponseDTO> getEnvironmentConfigList(
            @RequestParam(required = false) String envType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false) Boolean isDefault,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            EnvironmentConfigQuery query = new EnvironmentConfigQuery();
            query.setEnvType(envType);
            query.setStatus(status);
            query.setSearchKeyword(searchKeyword);
            query.setIsDefault(isDefault);
            query.setSortBy(sortBy);
            query.setSortOrder(sortOrder);
            query.setPage(page);
            query.setPageSize(pageSize);
            
            EnvironmentConfigListResponseDTO result = environmentConfigService.getEnvironmentConfigList(query);
            return ResponseVO.success("查询环境配置列表成功", result);
        } catch (IllegalArgumentException e) {
            log.warn("查询环境配置列表参数错误: {}", e.getMessage());
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            log.error("查询环境配置列表失败", e);
            return ResponseVO.serverError("查询环境配置列表失败：" + e.getMessage());
        }
    }
}

