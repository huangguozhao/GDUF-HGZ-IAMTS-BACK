package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.dto.CopyTestCaseRequestDTO;
import com.victor.iatms.entity.dto.CopyTestCaseResponseDTO;
import com.victor.iatms.entity.dto.CreateTestCaseDTO;
import com.victor.iatms.entity.dto.CreateTestCaseResponseDTO;
import com.victor.iatms.entity.dto.TestCaseListQueryDTO;
import com.victor.iatms.entity.dto.TestCaseListResponseDTO;
import com.victor.iatms.entity.dto.UpdateTestCaseDTO;
import com.victor.iatms.entity.dto.UpdateTestCaseResponseDTO;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.TestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 测试用例控制器
 */
@RestController
@RequestMapping("/testcases")
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
    
    /**
     * 修改测试用例
     * 
     * @param caseId 测试用例ID
     * @param updateTestCaseDTO 修改测试用例请求
     * @return 修改后的测试用例信息
     */
    @PutMapping("/{caseId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<UpdateTestCaseResponseDTO> updateTestCase(
            @PathVariable("caseId") Integer caseId,
            @RequestBody UpdateTestCaseDTO updateTestCaseDTO) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer currentUserId = 1; // 临时硬编码，实际应该从认证上下文获取

            UpdateTestCaseResponseDTO result = testCaseService.updateTestCase(caseId, updateTestCaseDTO, currentUserId);
            return ResponseVO.success("测试用例更新成功", result);

        } catch (IllegalArgumentException e) {
            // 根据不同的错误类型返回不同的错误响应
            if (e.getMessage().contains("测试用例不存在") ||
                e.getMessage().contains("测试用例已被删除") ||
                e.getMessage().contains("模板用例不存在")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("用例编码已被其他用例使用") ||
                     e.getMessage().contains("优先级值无效") ||
                     e.getMessage().contains("严重程度值无效") ||
                     e.getMessage().contains("参数验证失败")) {
                return ResponseVO.paramError(e.getMessage());
            } else if (e.getMessage().contains("权限不足")) {
                return ResponseVO.forbidden(e.getMessage());
            } else {
                return ResponseVO.businessError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("更新测试用例失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除测试用例
     * 
     * @param caseId 测试用例ID
     * @return 删除结果
     */
    @DeleteMapping("/{caseId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Void> deleteTestCase(@PathVariable("caseId") Integer caseId) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer currentUserId = 1; // 临时硬编码，实际应该从认证上下文获取

            testCaseService.deleteTestCase(caseId, currentUserId);
            return ResponseVO.success("测试用例删除成功", null);

        } catch (IllegalArgumentException e) {
            // 根据不同的错误类型返回不同的错误响应
            if (e.getMessage().contains("测试用例不存在") ||
                e.getMessage().contains("测试用例已被删除")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("权限不足")) {
                return ResponseVO.forbidden(e.getMessage());
            } else if (e.getMessage().contains("模板用例不能被删除") ||
                     e.getMessage().contains("不能删除系统用例") ||
                     e.getMessage().contains("用例正在被测试计划使用")) {
                return ResponseVO.businessError(e.getMessage());
            } else {
                return ResponseVO.paramError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("删除测试用例失败：" + e.getMessage());
        }
    }
    
    /**
     * 分页获取测试用例列表
     * 
     * @param queryDTO 查询参数
     * @return 分页的测试用例列表
     */
    @GetMapping
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<TestCaseListResponseDTO> getTestCaseList(TestCaseListQueryDTO queryDTO) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer currentUserId = 1; // 临时硬编码，实际应该从认证上下文获取

            TestCaseListResponseDTO result = testCaseService.getTestCaseList(queryDTO, currentUserId);
            return ResponseVO.success("查询成功", result);

        } catch (IllegalArgumentException e) {
            // 根据不同的错误类型返回不同的错误响应
            if (e.getMessage().contains("权限不足")) {
                return ResponseVO.forbidden(e.getMessage());
            } else if (e.getMessage().contains("参数验证失败") ||
                     e.getMessage().contains("分页大小不能超过") ||
                     e.getMessage().contains("排序字段无效")) {
                return ResponseVO.paramError(e.getMessage());
            } else {
                return ResponseVO.businessError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("查询测试用例列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 复制测试用例
     * 
     * @param caseId 源测试用例ID
     * @param requestDTO 复制测试用例请求
     * @return 复制后的测试用例信息
     */
    @PostMapping("/{caseId}/copy")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<CopyTestCaseResponseDTO> copyTestCase(
            @PathVariable("caseId") Integer caseId,
            @RequestBody CopyTestCaseRequestDTO requestDTO) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer currentUserId = 1; // 临时硬编码，实际应该从认证上下文获取
            
            CopyTestCaseResponseDTO result = testCaseService.copyTestCase(caseId, requestDTO, currentUserId);
            return ResponseVO.success("测试用例复制成功", result);
            
        } catch (IllegalArgumentException e) {
            // 根据不同的错误类型返回不同的错误响应
            if (e.getMessage().contains("测试用例不存在")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("用例编码已存在") ||
                     e.getMessage().contains("用例编码不能为空") ||
                     e.getMessage().contains("用例名称不能为空") ||
                     e.getMessage().contains("用例编码只能包含") ||
                     e.getMessage().contains("用例编码长度") ||
                     e.getMessage().contains("用例名称长度") ||
                     e.getMessage().contains("描述不能超过")) {
                return ResponseVO.paramError(e.getMessage());
            } else if (e.getMessage().contains("权限不足")) {
                return ResponseVO.forbidden(e.getMessage());
            } else {
                return ResponseVO.businessError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("复制测试用例失败：" + e.getMessage());
        }
    }
}