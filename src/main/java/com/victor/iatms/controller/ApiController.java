package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.dto.*;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 接口控制器
 */
@RestController
@RequestMapping("/apis")
@Validated
public class ApiController {

    @Autowired
    private ApiService apiService;

    /**
     * 创建接口
     * 
     * @param createDTO 创建接口请求DTO
     * @return 创建后的接口信息
     */
    @PostMapping
    @GlobalInterceptor(checkLogin = true, checkPermission = {"api:create"})
    public ResponseVO<ApiDTO> createApi(@RequestBody CreateApiDTO createDTO) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer currentUserId = 1; // 临时硬编码，实际应该从认证上下文获取

            ApiDTO result = apiService.createApi(createDTO, currentUserId);
            return ResponseVO.success("创建接口成功", result);

        } catch (IllegalArgumentException e) {
            // 参数验证失败
            if (e.getMessage().contains("不能为空") ||
                e.getMessage().contains("已存在") ||
                e.getMessage().contains("无效")) {
                return ResponseVO.paramError(e.getMessage());
            } else if (e.getMessage().contains("权限不足")) {
                return ResponseVO.forbidden(e.getMessage());
            } else {
                return ResponseVO.businessError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("创建接口失败：" + e.getMessage());
        }
    }

    /**
     * 更新接口
     * 
     * @param apiId 接口ID
     * @param updateDTO 更新接口请求DTO
     * @return 更新后的接口信息
     */
    @PutMapping("/{apiId}")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"api:update"})
    public ResponseVO<ApiDTO> updateApi(
            @PathVariable("apiId") Integer apiId,
            @RequestBody UpdateApiDTO updateDTO) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer currentUserId = 1; // 临时硬编码

            ApiDTO result = apiService.updateApi(apiId, updateDTO, currentUserId);
            return ResponseVO.success("更新接口成功", result);

        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("接口不存在")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("不能为空") ||
                     e.getMessage().contains("已存在") ||
                     e.getMessage().contains("无效")) {
                return ResponseVO.paramError(e.getMessage());
            } else if (e.getMessage().contains("权限不足")) {
                return ResponseVO.forbidden(e.getMessage());
            } else {
                return ResponseVO.businessError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("更新接口失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID查询接口
     * 
     * @param apiId 接口ID
     * @return 接口信息
     */
    @GetMapping("/{apiId}")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"api:view"})
    public ResponseVO<ApiDTO> getApiById(@PathVariable("apiId") Integer apiId) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer currentUserId = 1; // 临时硬编码

            ApiDTO result = apiService.getApiById(apiId, currentUserId);
            return ResponseVO.success("查询成功", result);

        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("接口不存在")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("权限不足")) {
                return ResponseVO.forbidden(e.getMessage());
            } else {
                return ResponseVO.paramError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("查询接口失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询接口列表
     * 
     * @param queryDTO 查询参数
     * @return 接口列表
     */
    @GetMapping
    @GlobalInterceptor(checkLogin = true, checkPermission = {"api:view"})
    public ResponseVO<ApiListResponseDTO> getApiList(ApiListQueryDTO queryDTO) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer currentUserId = 1; // 临时硬编码

            ApiListResponseDTO result = apiService.getApiList(queryDTO, currentUserId);
            return ResponseVO.success("查询成功", result);

        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("权限不足")) {
                return ResponseVO.forbidden(e.getMessage());
            } else {
                return ResponseVO.paramError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("查询接口列表失败：" + e.getMessage());
        }
    }

    /**
     * 删除接口
     * 
     * @param apiId 接口ID
     * @return 删除结果
     */
    @DeleteMapping("/{apiId}")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"api:delete"})
    public ResponseVO<Void> deleteApi(@PathVariable("apiId") Integer apiId) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer currentUserId = 1; // 临时硬编码

            apiService.deleteApi(apiId, currentUserId);
            return ResponseVO.success("接口删除成功", null);

        } catch (IllegalArgumentException e) {
            // 根据不同的错误类型返回不同的错误响应
            if (e.getMessage().contains("接口不存在") ||
                e.getMessage().contains("接口已被删除")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("权限不足")) {
                return ResponseVO.forbidden(e.getMessage());
            } else if (e.getMessage().contains("接口存在测试用例") ||
                     e.getMessage().contains("接口存在前置条件") ||
                     e.getMessage().contains("不能删除系统接口") ||
                     e.getMessage().contains("接口正在被使用")) {
                return ResponseVO.businessError(e.getMessage());
            } else {
                return ResponseVO.paramError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("删除接口失败：" + e.getMessage());
        }
    }
}
