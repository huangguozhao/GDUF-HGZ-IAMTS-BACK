package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.dto.TestExecutionRecordDetailDTO;
import com.victor.iatms.entity.dto.TestExecutionRecordPageResultDTO;
import com.victor.iatms.entity.dto.TestExecutionRecordStatisticsDTO;
import com.victor.iatms.entity.dto.UpdateTestExecutionRecordDTO;
import com.victor.iatms.entity.query.TestExecutionRecordQuery;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.TestExecutionRecordService;
import com.victor.iatms.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 测试执行记录控制器
 */
@RestController
@RequestMapping("/execution-records")
@Validated
public class TestExecutionRecordController {
    
    @Autowired
    private TestExecutionRecordService testExecutionRecordService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    /**
     * 分页查询测试执行记录
     */
    @GetMapping("")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"testcase:view"})
    public ResponseVO<TestExecutionRecordPageResultDTO> getExecutionRecords(
            @RequestParam(value = "execution_scope", required = false) String executionScope,
            @RequestParam(value = "ref_id", required = false) Integer refId,
            @RequestParam(value = "executed_by", required = false) Integer executedBy,
            @RequestParam(value = "execution_type", required = false) String executionType,
            @RequestParam(value = "environment", required = false) String environment,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "start_time_begin", required = false) String startTimeBeginStr,
            @RequestParam(value = "start_time_end", required = false) String startTimeEndStr,
            @RequestParam(value = "search_keyword", required = false) String searchKeyword,
            @RequestParam(value = "browser", required = false) String browser,
            @RequestParam(value = "app_version", required = false) String appVersion,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort_order", required = false) String sortOrder,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "page_size", required = false) Integer pageSize,
            HttpServletRequest request) {
        try {
            // 构建查询参数
            TestExecutionRecordQuery query = new TestExecutionRecordQuery();
            query.setExecutionScope(executionScope);
            query.setRefId(refId);
            query.setExecutedBy(executedBy);
            query.setExecutionType(executionType);
            query.setEnvironment(environment);
            query.setStatus(status);
            query.setSearchKeyword(searchKeyword);
            query.setBrowser(browser);
            query.setAppVersion(appVersion);
            query.setSortBy(sortBy);
            query.setSortOrder(sortOrder);
            query.setPage(page);
            query.setPageSize(pageSize);
            
            // 解析时间参数
            if (startTimeBeginStr != null && !startTimeBeginStr.isEmpty()) {
                try {
                    query.setStartTimeBegin(LocalDateTime.parse(startTimeBeginStr, 
                        DateTimeFormatter.ISO_DATE_TIME));
                } catch (Exception e) {
                    return ResponseVO.paramError("开始时间格式错误，请使用ISO 8601格式");
                }
            }
            if (startTimeEndStr != null && !startTimeEndStr.isEmpty()) {
                try {
                    query.setStartTimeEnd(LocalDateTime.parse(startTimeEndStr, 
                        DateTimeFormatter.ISO_DATE_TIME));
                } catch (Exception e) {
                    return ResponseVO.paramError("结束时间格式错误，请使用ISO 8601格式");
                }
            }
            
            TestExecutionRecordPageResultDTO result = testExecutionRecordService.findExecutionRecords(query);
            return ResponseVO.success("查询执行记录成功", result);
            
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("查询执行记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID查询执行记录详情
     */
    @GetMapping("/{recordId}")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"testcase:view"})
    public ResponseVO<TestExecutionRecordDetailDTO> getExecutionRecordById(
            @PathVariable("recordId") Long recordId,
            HttpServletRequest request) {
        try {
            TestExecutionRecordDetailDTO result = testExecutionRecordService.findExecutionRecordById(recordId);
            return ResponseVO.success("查询执行记录详情成功", result);
            
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("不存在")) {
                return ResponseVO.notFound(e.getMessage());
            }
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("查询执行记录详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据执行范围查询最近的执行记录
     */
    @GetMapping("/scope/{executionScope}/{refId}")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"testcase:view"})
    public ResponseVO<List<TestExecutionRecordDetailDTO>> getRecentRecordsByScope(
            @PathVariable("executionScope") String executionScope,
            @PathVariable("refId") Integer refId,
            @RequestParam(value = "limit", required = false) Integer limit,
            HttpServletRequest request) {
        try {
            List<TestExecutionRecordDetailDTO> result = testExecutionRecordService
                .findRecentExecutionRecordsByScope(executionScope, refId, limit);
            return ResponseVO.success("查询最近执行记录成功", result);
            
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("查询最近执行记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新执行记录
     */
    @PutMapping("/{recordId}")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"testcase:execute"})
    public ResponseVO<TestExecutionRecordDetailDTO> updateExecutionRecord(
            @PathVariable("recordId") Long recordId,
            @RequestBody @Validated UpdateTestExecutionRecordDTO updateDTO,
            HttpServletRequest request) {
        try {
            TestExecutionRecordDetailDTO result = testExecutionRecordService
                .updateExecutionRecord(recordId, updateDTO);
            return ResponseVO.success("更新执行记录成功", result);
            
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("不存在")) {
                return ResponseVO.notFound(e.getMessage());
            }
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("更新执行记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除执行记录（软删除）
     */
    @DeleteMapping("/{recordId}")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"testcase:delete"})
    public ResponseVO<Boolean> deleteExecutionRecord(
            @PathVariable("recordId") Long recordId,
            HttpServletRequest request) {
        try {
            Integer userId = getCurrentUserId(request);
            boolean result = testExecutionRecordService.deleteExecutionRecord(recordId, userId);
            
            if (result) {
                return ResponseVO.success("删除执行记录成功", true);
            } else {
                return ResponseVO.businessError("删除执行记录失败");
            }
            
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("不存在")) {
                return ResponseVO.notFound(e.getMessage());
            }
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("删除执行记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量删除执行记录
     */
    @DeleteMapping("/batch")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"testcase:delete"})
    public ResponseVO<Integer> batchDeleteExecutionRecords(
            @RequestBody List<Long> recordIds,
            HttpServletRequest request) {
        try {
            Integer userId = getCurrentUserId(request);
            int deletedCount = testExecutionRecordService.batchDeleteExecutionRecords(recordIds, userId);
            
            return ResponseVO.success("批量删除执行记录成功，共删除" + deletedCount + "条记录", deletedCount);
            
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("批量删除执行记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取执行记录统计信息
     */
    @GetMapping("/statistics")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"testcase:view"})
    public ResponseVO<TestExecutionRecordStatisticsDTO> getExecutionStatistics(
            @RequestParam(value = "execution_scope", required = false) String executionScope,
            @RequestParam(value = "ref_id", required = false) Integer refId,
            @RequestParam(value = "executed_by", required = false) Integer executedBy,
            @RequestParam(value = "environment", required = false) String environment,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "start_time_begin", required = false) String startTimeBeginStr,
            @RequestParam(value = "start_time_end", required = false) String startTimeEndStr,
            HttpServletRequest request) {
        try {
            // 构建查询参数
            TestExecutionRecordQuery query = new TestExecutionRecordQuery();
            query.setExecutionScope(executionScope);
            query.setRefId(refId);
            query.setExecutedBy(executedBy);
            query.setEnvironment(environment);
            query.setStatus(status);
            
            // 解析时间参数
            if (startTimeBeginStr != null && !startTimeBeginStr.isEmpty()) {
                try {
                    query.setStartTimeBegin(LocalDateTime.parse(startTimeBeginStr, 
                        DateTimeFormatter.ISO_DATE_TIME));
                } catch (Exception e) {
                    return ResponseVO.paramError("开始时间格式错误，请使用ISO 8601格式");
                }
            }
            if (startTimeEndStr != null && !startTimeEndStr.isEmpty()) {
                try {
                    query.setStartTimeEnd(LocalDateTime.parse(startTimeEndStr, 
                        DateTimeFormatter.ISO_DATE_TIME));
                } catch (Exception e) {
                    return ResponseVO.paramError("结束时间格式错误，请使用ISO 8601格式");
                }
            }
            
            TestExecutionRecordStatisticsDTO result = testExecutionRecordService.getExecutionStatistics(query);
            return ResponseVO.success("获取统计信息成功", result);
            
        } catch (Exception e) {
            return ResponseVO.serverError("获取统计信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据执行人查询执行记录
     */
    @GetMapping("/executor/{executedBy}")
    @GlobalInterceptor(checkLogin = true, checkPermission = {"testcase:view"})
    public ResponseVO<List<TestExecutionRecordDetailDTO>> getRecordsByExecutor(
            @PathVariable("executedBy") Integer executedBy,
            @RequestParam(value = "limit", required = false) Integer limit,
            HttpServletRequest request) {
        try {
            List<TestExecutionRecordDetailDTO> result = testExecutionRecordService
                .findExecutionRecordsByExecutor(executedBy, limit);
            return ResponseVO.success("查询执行人的执行记录成功", result);
            
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("查询执行人的执行记录失败：" + e.getMessage());
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
}

