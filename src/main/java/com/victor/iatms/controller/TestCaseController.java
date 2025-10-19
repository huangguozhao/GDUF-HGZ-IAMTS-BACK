package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.dto.CreateTestCaseDTO;
import com.victor.iatms.entity.dto.CreateTestCaseResponseDTO;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.TestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 测试用例控制器
 */
@RestController
@RequestMapping("/apis")
public class TestCaseController {

    @Autowired
    private TestCaseService testCaseService;

    /**
     * 创建测试用例
     * 
     * @param createTestCaseDTO 创建测试用例请求
     * @return 创建的测试用例信息
     */
    @PostMapping
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<CreateTestCaseResponseDTO> createTestCase(@RequestBody CreateTestCaseDTO createTestCaseDTO) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer currentUserId = 1; // 临时硬编码，实际应该从认证上下文获取

            CreateTestCaseResponseDTO result = testCaseService.createTestCase(createTestCaseDTO, currentUserId);
            return ResponseVO.success("测试用例创建成功", result);

        } catch (IllegalArgumentException e) {
            // 根据不同的错误类型返回不同的错误响应
            if (e.getMessage().contains("接口不存在") ||
                e.getMessage().contains("接口已禁用") ||
                e.getMessage().contains("模板用例不存在")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("用例编码已存在") ||
                     e.getMessage().contains("用例名称不能为空") ||
                     e.getMessage().contains("参数验证失败")) {
                return ResponseVO.paramError(e.getMessage());
            } else if (e.getMessage().contains("权限不足")) {
                return ResponseVO.forbidden(e.getMessage());
            } else {
                return ResponseVO.businessError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("创建测试用例失败：" + e.getMessage());
        }
    }
}