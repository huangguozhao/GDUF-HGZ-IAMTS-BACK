package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 接口控制器
 */
@RestController
@RequestMapping("/apis")
public class ApiController {

    @Autowired
    private ApiService apiService;

    /**
     * 删除接口
     * 
     * @param apiId 接口ID
     * @return 删除结果
     */
    @DeleteMapping("/{apiId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Void> deleteApi(@PathVariable("apiId") Integer apiId) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer currentUserId = 1; // 临时硬编码，实际应该从认证上下文获取

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
