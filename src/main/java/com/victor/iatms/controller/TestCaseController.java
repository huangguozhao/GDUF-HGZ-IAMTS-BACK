package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.dto.CreateTestCaseDTO;
import com.victor.iatms.entity.dto.ExportResultDTO;
import com.victor.iatms.entity.dto.ImportResultDTO;
import com.victor.iatms.entity.dto.PageResultDTO;
import com.victor.iatms.entity.dto.TestCaseDTO;
import com.victor.iatms.entity.dto.TestCaseResponseDTO;
import com.victor.iatms.entity.dto.UpdateTestCaseDTO;
import com.victor.iatms.entity.dto.UpdateTestCaseResponseDTO;
import com.victor.iatms.entity.query.TestCaseQuery;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.TestCaseService;
import com.victor.iatms.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 测试用例控制器
 */
@RestController
@RequestMapping("/apis")
public class TestCaseController {

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 分页获取接口相关用例列表
     * @param apiId 接口ID
     * @param name 用例名称（模糊查询）
     * @param priority 优先级过滤
     * @param severity 严重程度过滤
     * @param isEnabled 是否启用过滤
     * @param isTemplate 是否模板用例过滤
     * @param tags 标签过滤（支持多个标签）
     * @param createdBy 创建人ID过滤
     * @param page 页码
     * @param pageSize 每页条数
     * @param request HTTP请求对象
     * @return 分页结果
     */
    @GetMapping("/{apiId}/test-cases")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:view"},
        checkResourceAccess = true,
        resourceType = "api",
        resourceIdParam = "apiId"
    )
    public ResponseVO<PageResultDTO<TestCaseDTO>> getTestCaseList(
            @PathVariable("apiId") Integer apiId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "priority", required = false) String priority,
            @RequestParam(value = "severity", required = false) String severity,
            @RequestParam(value = "is_enabled", required = false) Boolean isEnabled,
            @RequestParam(value = "is_template", required = false) Boolean isTemplate,
            @RequestParam(value = "tags", required = false) String[] tags,
            @RequestParam(value = "created_by", required = false) Integer createdBy,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "page_size", required = false, defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        
        try {
            // 从拦截器中获取用户ID（如果需要权限验证）
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                return ResponseVO.authError("认证失败，请重新登录");
            }

            // 构建查询参数
            TestCaseQuery query = new TestCaseQuery();
            query.setApiId(apiId);
            query.setName(name);
            query.setPriority(priority);
            query.setSeverity(severity);
            query.setIsEnabled(isEnabled);
            query.setIsTemplate(isTemplate);
            query.setTags(tags);
            query.setCreatedBy(createdBy);
            query.setPage(page);
            query.setPageSize(pageSize);

            // 调用服务层获取分页结果
            PageResultDTO<TestCaseDTO> result = testCaseService.getTestCaseList(query);

            return ResponseVO.success("success", result);
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("接口不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 添加测试用例
     * @param apiId 接口ID
     * @param createRequest 创建请求
     * @param request HTTP请求对象
     * @return 创建的测试用例信息
     */
    @PostMapping("/{apiId}/test-cases")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:create"},
        checkResourceAccess = true,
        resourceType = "api",
        resourceIdParam = "apiId"
    )
    public ResponseVO<TestCaseResponseDTO> createTestCase(
            @PathVariable("apiId") Integer apiId,
            @Validated @RequestBody CreateTestCaseDTO createRequest,
            HttpServletRequest request) {
        
        try {
            // 从拦截器中获取用户ID
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                return ResponseVO.authError("认证失败，请重新登录");
            }

            // 调用服务层创建测试用例
            TestCaseResponseDTO response = testCaseService.createTestCase(apiId, createRequest, userId);

            return ResponseVO.success("测试用例创建成功", response);
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("接口不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("用例编码已存在".equals(errorMsg)) {
                return ResponseVO.businessError(errorMsg);
            } else if ("模板用例不存在".equals(errorMsg)) {
                return ResponseVO.businessError(errorMsg);
            } else if (errorMsg.contains("无效的") || errorMsg.contains("不能为空")) {
                return ResponseVO.paramError(errorMsg);
            } else if ("测试用例创建失败".equals(errorMsg)) {
                return ResponseVO.serverError(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 编辑测试用例
     * @param apiId 接口ID
     * @param caseId 用例ID
     * @param updateRequest 更新请求
     * @param request HTTP请求对象
     * @return 更新后的测试用例信息
     */
    @PutMapping("/{apiId}/test-cases/{caseId}")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:update"},
        checkResourceAccess = true,
        resourceType = "api",
        resourceIdParam = "apiId"
    )
    public ResponseVO<UpdateTestCaseResponseDTO> updateTestCase(
            @PathVariable("apiId") Integer apiId,
            @PathVariable("caseId") Integer caseId,
            @Validated @RequestBody UpdateTestCaseDTO updateRequest,
            HttpServletRequest request) {
        
        try {
            // 从拦截器中获取用户ID
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                return ResponseVO.authError("认证失败，请重新登录");
            }

            // 调用服务层更新测试用例
            UpdateTestCaseResponseDTO response = testCaseService.updateTestCase(apiId, caseId, updateRequest, userId);

            return ResponseVO.success("测试用例更新成功", response);
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("接口不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("测试用例不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("用例编码已被其他用例使用".equals(errorMsg)) {
                return ResponseVO.businessError(errorMsg);
            } else if ("模板用例不存在".equals(errorMsg)) {
                return ResponseVO.businessError(errorMsg);
            } else if (errorMsg.contains("无效的") || errorMsg.contains("不能为空")) {
                return ResponseVO.paramError(errorMsg);
            } else if ("测试用例更新失败".equals(errorMsg)) {
                return ResponseVO.serverError(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 删除测试用例
     * @param apiId 接口ID
     * @param caseId 用例ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    @DeleteMapping("/{apiId}/test-cases/{caseId}")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:delete"},
        checkResourceAccess = true,
        resourceType = "api",
        resourceIdParam = "apiId"
    )
    public ResponseVO<Void> deleteTestCase(
            @PathVariable("apiId") Integer apiId,
            @PathVariable("caseId") Integer caseId,
            HttpServletRequest request) {
        
        try {
            // 从拦截器中获取用户ID
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                return ResponseVO.authError("认证失败，请重新登录");
            }

            // 调用服务层删除测试用例
            boolean result = testCaseService.deleteTestCase(apiId, caseId, userId);

            if (result) {
                return ResponseVO.success("测试用例删除成功", null);
            } else {
                return ResponseVO.serverError("测试用例删除失败");
            }
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("接口不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("测试用例不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("测试用例已被删除".equals(errorMsg)) {
                return ResponseVO.businessError(errorMsg);
            } else if ("用例正在被测试计划使用，无法删除".equals(errorMsg)) {
                return ResponseVO.businessError(errorMsg);
            } else if ("用例正在执行中，无法删除".equals(errorMsg)) {
                return ResponseVO.businessError(errorMsg);
            } else if ("系统内置模板用例不允许删除".equals(errorMsg)) {
                return ResponseVO.businessError(errorMsg);
            } else if ("测试用例删除失败".equals(errorMsg)) {
                return ResponseVO.serverError(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 导入测试用例
     */
    @PostMapping("/{apiId}/test-cases/import")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:create"},
        checkResourceAccess = true,
        resourceType = "api",
        resourceIdParam = "apiId"
    )
    public ResponseVO<ImportResultDTO> importTestCases(
            @PathVariable("apiId") Integer apiId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "import_mode", defaultValue = "insert") String importMode,
            @RequestParam(value = "conflict_strategy", defaultValue = "skip") String conflictStrategy,
            @RequestParam(value = "template_type", defaultValue = "standard") String templateType,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 调用服务层方法
            ImportResultDTO result = testCaseService.importTestCases(
                apiId, file, importMode, conflictStrategy, templateType, userId);

            return ResponseVO.success("用例导入完成", result);

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("接口不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("上传的文件为空".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("文件大小超过限制".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("不支持的文件格式".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("文件解析失败".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if ("文件中没有有效的测试用例数据".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 获取当前用户ID
     */
    private Integer getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("认证失败，请重新登录");
        }

        String token = authHeader.substring(7);
        
        try {
            if (!jwtUtils.validateToken(token)) {
                throw new RuntimeException("认证失败，请重新登录");
            }

            Integer userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                throw new RuntimeException("认证失败，请重新登录");
            }

            return userId;
        } catch (Exception e) {
            throw new RuntimeException("认证失败，请重新登录");
        }
    }

    /**
     * 导出测试用例
     */
    @GetMapping("/{apiId}/test-cases/export")
    @GlobalInterceptor(
        checkLogin = true,
        checkPermission = {"testcase:view"},
        checkResourceAccess = true,
        resourceType = "api",
        resourceIdParam = "apiId"
    )
    public void exportTestCases(
            @PathVariable("apiId") Integer apiId,
            @RequestParam(value = "format", defaultValue = "excel") String format,
            @RequestParam(value = "include_disabled", defaultValue = "false") Boolean includeDisabled,
            @RequestParam(value = "include_templates", defaultValue = "false") Boolean includeTemplates,
            @RequestParam(value = "fields", required = false) String fieldsParam,
            @RequestParam(value = "filename", required = false) String filename,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            // 获取当前用户ID
            Integer userId = getCurrentUserId(request);

            // 处理字段参数
            List<String> fields = null;
            if (fieldsParam != null && !fieldsParam.trim().isEmpty()) {
                fields = Arrays.asList(fieldsParam.split(","));
            }

            // 调用服务层方法
            ExportResultDTO result = testCaseService.exportTestCases(
                apiId, format, includeDisabled, includeTemplates, fields, filename, userId);

            // 设置响应头
            response.setContentType(result.getMimeType());
            response.setContentLengthLong(result.getFileSize());
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + result.getFilename() + "\"");

            // 写入文件流
            response.getOutputStream().write(result.getFileData());
            response.getOutputStream().flush();

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            try {
                if ("接口不存在".equals(errorMsg)) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":-4,\"msg\":\"接口不存在\",\"data\":null}");
                } else if ("该接口下没有可导出的测试用例".equals(errorMsg)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":0,\"msg\":\"该接口下没有可导出的测试用例\",\"data\":null}");
                } else if ("不支持的导出格式".equals(errorMsg)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":-3,\"msg\":\"不支持的导出格式\",\"data\":null}");
                } else if ("文件导出失败".equals(errorMsg)) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":-5,\"msg\":\"文件导出失败\",\"data\":null}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":-5,\"msg\":\"系统异常，请稍后重试\",\"data\":null}");
                }
            } catch (IOException ioException) {
                // 忽略IO异常
            }
        } catch (Exception e) {
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":-5,\"msg\":\"系统异常，请稍后重试\",\"data\":null}");
            } catch (IOException ioException) {
                // 忽略IO异常
            }
        }
    }
}
