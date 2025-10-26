package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.constants.Constants;
import com.victor.iatms.entity.dto.ReportListQueryDTO;
import com.victor.iatms.entity.dto.ReportPageResultDTO;
import com.victor.iatms.entity.dto.ReportExportQueryDTO;
import com.victor.iatms.entity.dto.DeleteReportResponseDTO;
import com.victor.iatms.entity.enums.ReportExportFormatEnum;
import com.victor.iatms.entity.po.TestReportSummary;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.ReportService;
import com.victor.iatms.service.ReportExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 报告管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/reports")
public class ReportController {
    
    @Autowired
    private ReportService reportService;
    
    @Autowired
    private ReportExportService reportExportService;
    
    @Autowired
    private com.victor.iatms.service.EnterpriseReportService enterpriseReportService;
    
    @Autowired
    private com.victor.iatms.service.ISOEnterpriseReportService isoEnterpriseReportService;
    
    @Autowired
    private com.victor.iatms.service.AllureReportService allureReportService;
    
    /**
     * 分页查询测试报告列表
     * 
     * @param projectId 项目ID过滤
     * @param reportType 报告类型过滤
     * @param environment 环境过滤
     * @param reportStatus 报告状态过滤
     * @param fileFormat 文件格式过滤
     * @param startTimeBegin 开始时间范围查询（开始）
     * @param startTimeEnd 开始时间范围查询（结束）
     * @param successRateMin 最小成功率过滤
     * @param successRateMax 最大成功率过滤
     * @param tags 标签过滤
     * @param searchKeyword 关键字搜索
     * @param sortBy 排序字段
     * @param sortOrder 排序顺序
     * @param includeDeleted 是否包含已删除的报告
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @GetMapping
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<ReportPageResultDTO> getReportList(
            @RequestParam(value = "project_id", required = false) Integer projectId,
            @RequestParam(value = "report_type", required = false) String reportType,
            @RequestParam(value = "environment", required = false) String environment,
            @RequestParam(value = "report_status", required = false) String reportStatus,
            @RequestParam(value = "file_format", required = false) String fileFormat,
            @RequestParam(value = "start_time_begin", required = false) String startTimeBegin,
            @RequestParam(value = "start_time_end", required = false) String startTimeEnd,
            @RequestParam(value = "success_rate_min", required = false) BigDecimal successRateMin,
            @RequestParam(value = "success_rate_max", required = false) BigDecimal successRateMax,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "search_keyword", required = false) String searchKeyword,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort_order", required = false) String sortOrder,
            @RequestParam(value = "include_deleted", required = false) Boolean includeDeleted,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "page_size", required = false) Integer pageSize) {
        
        try {
            // 构建查询参数
            ReportListQueryDTO queryDTO = new ReportListQueryDTO();
            queryDTO.setProjectId(projectId);
            queryDTO.setReportType(reportType);
            queryDTO.setEnvironment(environment);
            queryDTO.setReportStatus(reportStatus);
            queryDTO.setFileFormat(fileFormat);
            queryDTO.setSuccessRateMin(successRateMin);
            queryDTO.setSuccessRateMax(successRateMax);
            queryDTO.setTags(tags);
            queryDTO.setSearchKeyword(searchKeyword);
            queryDTO.setSortBy(sortBy);
            queryDTO.setSortOrder(sortOrder);
            queryDTO.setIncludeDeleted(includeDeleted);
            queryDTO.setPage(page);
            queryDTO.setPageSize(pageSize);
            
            // 处理时间参数
            if (startTimeBegin != null && !startTimeBegin.trim().isEmpty()) {
                try {
                    queryDTO.setStartTimeBegin(LocalDateTime.parse(startTimeBegin, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } catch (Exception e) {
                    return ResponseVO.paramError("开始时间格式错误，请使用ISO格式：YYYY-MM-DDTHH:mm:ss");
                }
            }
            
            if (startTimeEnd != null && !startTimeEnd.trim().isEmpty()) {
                try {
                    queryDTO.setStartTimeEnd(LocalDateTime.parse(startTimeEnd, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } catch (Exception e) {
                    return ResponseVO.paramError("结束时间格式错误，请使用ISO格式：YYYY-MM-DDTHH:mm:ss");
                }
            }
            
            // 查询报告列表
            ReportPageResultDTO result = reportService.getReportList(queryDTO);
            return ResponseVO.success("查询报告列表成功", result);
            
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("查询报告列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID查询报告详情
     * 
     * @param reportId 报告ID
     * @return 报告详情
     */
    @GetMapping("/{reportId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<TestReportSummary> getReportById(@PathVariable("reportId") Long reportId) {
        try {
            TestReportSummary report = reportService.getReportById(reportId);
            return ResponseVO.success("查询报告详情成功", report);
        } catch (IllegalArgumentException e) {
            return ResponseVO.notFound(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("查询报告详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据项目ID查询报告列表
     * 
     * @param projectId 项目ID
     * @return 报告列表
     */
    @GetMapping("/project/{projectId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<List<TestReportSummary>> getReportsByProjectId(@PathVariable("projectId") Integer projectId) {
        try {
            List<TestReportSummary> reports = reportService.getReportsByProjectId(projectId);
            return ResponseVO.success("查询项目报告列表成功", reports);
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("查询项目报告列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据执行ID查询报告
     * 
     * @param executionId 执行ID
     * @return 报告
     */
    @GetMapping("/execution/{executionId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<TestReportSummary> getReportByExecutionId(@PathVariable("executionId") Long executionId) {
        try {
            TestReportSummary report = reportService.getReportByExecutionId(executionId);
            if (report == null) {
                return ResponseVO.notFound("未找到对应的报告");
            }
            return ResponseVO.success("查询执行报告成功", report);
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("查询执行报告失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建报告
     * 
     * @param report 报告信息
     * @return 创建的报告
     */
    @PostMapping
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<TestReportSummary> createReport(@RequestBody TestReportSummary report) {
        try {
            TestReportSummary createdReport = reportService.createReport(report);
            return ResponseVO.success("创建报告成功", createdReport);
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("创建报告失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新报告
     * 
     * @param reportId 报告ID
     * @param report 报告信息
     * @return 更新后的报告
     */
    @PutMapping("/{reportId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<TestReportSummary> updateReport(@PathVariable("reportId") Long reportId, 
                                                      @RequestBody TestReportSummary report) {
        try {
            report.setReportId(reportId);
            TestReportSummary updatedReport = reportService.updateReport(report);
            return ResponseVO.success("更新报告成功", updatedReport);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("不存在")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("已被删除")) {
                return ResponseVO.businessError(e.getMessage());
            } else {
                return ResponseVO.paramError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("更新报告失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量删除报告
     * 
     * @param reportIds 报告ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Integer> batchDeleteReports(@RequestBody List<Long> reportIds) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer deletedBy = 1; // 临时硬编码，实际应该从JWT token中获取
            int result = reportService.batchDeleteReports(reportIds, deletedBy);
            return ResponseVO.success("批量删除报告成功", result);
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("批量删除报告失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据项目ID删除报告
     * 
     * @param projectId 项目ID
     * @return 删除结果
     */
    @DeleteMapping("/project/{projectId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Integer> deleteReportsByProjectId(@PathVariable("projectId") Integer projectId) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer deletedBy = 1; // 临时硬编码，实际应该从JWT token中获取
            int result = reportService.deleteReportsByProjectId(projectId, deletedBy);
            return ResponseVO.success("删除项目报告成功", result);
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("删除项目报告失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新报告状态
     * 
     * @param reportId 报告ID
     * @param reportStatus 报告状态
     * @return 更新结果
     */
    @PatchMapping("/{reportId}/status")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Boolean> updateReportStatus(@PathVariable("reportId") Long reportId,
                                                 @RequestParam("report_status") String reportStatus) {
        try {
            boolean result = reportService.updateReportStatus(reportId, reportStatus);
            return ResponseVO.success("更新报告状态成功", result);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("不存在")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("已被删除")) {
                return ResponseVO.businessError(e.getMessage());
            } else {
                return ResponseVO.paramError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("更新报告状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新报告文件信息
     * 
     * @param reportId 报告ID
     * @param filePath 文件路径
     * @param fileSize 文件大小
     * @param downloadUrl 下载地址
     * @return 更新结果
     */
    @PatchMapping("/{reportId}/file")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Boolean> updateReportFileInfo(@PathVariable("reportId") Long reportId,
                                                    @RequestParam("file_path") String filePath,
                                                    @RequestParam("file_size") Long fileSize,
                                                    @RequestParam("download_url") String downloadUrl) {
        try {
            boolean result = reportService.updateReportFileInfo(reportId, filePath, fileSize, downloadUrl);
            return ResponseVO.success("更新报告文件信息成功", result);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("不存在")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("已被删除")) {
                return ResponseVO.businessError(e.getMessage());
            } else {
                return ResponseVO.paramError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("更新报告文件信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 导出测试报告
     * 
     * @param reportId 报告ID
     * @param exportFormat 导出格式
     * @param includeDetails 是否包含详细的用例执行结果
     * @param includeAttachments 是否包含附件信息（链接）
     * @param includeFailureDetails 是否包含失败详情
     * @param timezone 时区设置
     * @return 导出文件
     */
    @GetMapping("/{reportId}/export")
    @GlobalInterceptor(checkLogin = true)
    public ResponseEntity<Resource> exportReport(@PathVariable("reportId") Long reportId,
                                                 @RequestParam("export_format") String exportFormat,
                                                 @RequestParam(value = "include_details", required = false) Boolean includeDetails,
                                                 @RequestParam(value = "include_attachments", required = false) Boolean includeAttachments,
                                                 @RequestParam(value = "include_failure_details", required = false) Boolean includeFailureDetails,
                                                 @RequestParam(value = "timezone", required = false) String timezone) {
        try {
            // 构建导出查询参数
            ReportExportQueryDTO queryDTO = new ReportExportQueryDTO();
            queryDTO.setReportId(reportId);
            queryDTO.setExportFormat(exportFormat);
            queryDTO.setIncludeDetails(includeDetails != null ? includeDetails : Constants.DEFAULT_INCLUDE_DETAILS);
            queryDTO.setIncludeAttachments(includeAttachments != null ? includeAttachments : Constants.DEFAULT_INCLUDE_ATTACHMENTS);
            queryDTO.setIncludeFailureDetails(includeFailureDetails != null ? includeFailureDetails : Constants.DEFAULT_INCLUDE_FAILURE_DETAILS);
            queryDTO.setTimezone(timezone != null ? timezone : Constants.DEFAULT_TIMEZONE);
            
            // 导出报告
            Resource resource = reportExportService.exportReport(queryDTO);
            
            // 获取MIME类型
            ReportExportFormatEnum formatEnum = ReportExportFormatEnum.getByCode(exportFormat);
            String mimeType = formatEnum != null ? formatEnum.getMimeType() : Constants.JSON_MIME_TYPE;
            
            // 生成文件名
            String fileName = reportExportService.generateExportFileName(reportId, exportFormat);
            
            // 设置响应头（优化版）
            HttpHeaders headers = new HttpHeaders();
            
            // 设置Content-Disposition，支持中文文件名
            String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            headers.setContentDispositionFormData("attachment", encodedFileName);
            
            // 设置Content-Type
            headers.setContentType(MediaType.parseMediaType(mimeType));
            
            // 设置缓存控制
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
                    
        } catch (IllegalArgumentException e) {
            log.error("导出报告参数错误: reportId={}, format={}, error={}", reportId, exportFormat, e.getMessage(), e);
            if (e.getMessage().contains("不存在")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("生成中")) {
                return ResponseEntity.status(409).build(); // Conflict
            } else if (e.getMessage().contains("不支持的导出格式")) {
                return ResponseEntity.badRequest().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("导出报告失败: reportId={}, format={}, error={}", reportId, exportFormat, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 导出企业级测试报告
     * 
     * @param reportId 报告ID
     * @param locale 语言环境（zh_CN/en_US）
     * @return HTML报告文件
     */
    @GetMapping("/{reportId}/export/enterprise")
    @GlobalInterceptor(checkLogin = true)
    public ResponseEntity<Resource> exportEnterpriseReport(@PathVariable("reportId") Long reportId,
                                                           @RequestParam(value = "locale", required = false, defaultValue = "zh_CN") String locale) {
        try {
            log.info("开始导出企业级报告: reportId={}, locale={}", reportId, locale);
            
            // 导出企业级报告
            Resource resource = enterpriseReportService.exportEnterpriseReport(reportId, locale);
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            
            // 设置Content-Disposition，支持中文文件名
            String fileName = resource.getFilename();
            if (fileName != null) {
                String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
                headers.setContentDispositionFormData("attachment", encodedFileName);
            }
            
            // 设置Content-Type为HTML
            headers.setContentType(MediaType.parseMediaType("text/html;charset=UTF-8"));
            
            // 设置缓存控制
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            
            log.info("企业级报告导出成功: reportId={}, fileName={}", reportId, fileName);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
                    
        } catch (IllegalArgumentException e) {
            log.error("导出企业级报告参数错误: reportId={}, error={}", reportId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("导出企业级报告失败: reportId={}, error={}", reportId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 导出ISO/IEC/IEEE 29119标准企业级测试报告
     * 符合国际软件测试标准和ISTQB最佳实践
     * 
     * @param reportId 报告ID
     * @param locale 语言环境（zh_CN/en_US）
     * @return HTML报告文件
     */
    @GetMapping("/{reportId}/export/iso")
    @GlobalInterceptor(checkLogin = true)
    public ResponseEntity<Resource> exportISOEnterpriseReport(@PathVariable("reportId") Long reportId,
                                                              @RequestParam(value = "locale", required = false, defaultValue = "zh_CN") String locale) {
        try {
            log.info("开始导出ISO标准企业级报告: reportId={}, locale={}", reportId, locale);
            
            // 导出ISO标准企业级报告
            Resource resource = isoEnterpriseReportService.exportISOEnterpriseReport(reportId, locale);
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            
            // 设置Content-Disposition，支持中文文件名
            String fileName = resource.getFilename();
            if (fileName != null) {
                String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
                headers.setContentDispositionFormData("attachment", encodedFileName);
            }
            
            // 设置Content-Type为HTML
            headers.setContentType(MediaType.parseMediaType("text/html;charset=UTF-8"));
            
            // 设置缓存控制
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            
            log.info("ISO标准企业级报告导出成功: reportId={}, fileName={}", reportId, fileName);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
                    
        } catch (IllegalArgumentException e) {
            log.error("导出ISO标准企业级报告参数错误: reportId={}, error={}", reportId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("导出ISO标准企业级报告失败: reportId={}, error={}", reportId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 导出Allure风格测试报告（技术详细版）
     * 面向测试和开发人员，包含详细的测试步骤、HTTP请求/响应、错误堆栈等
     *
     * @param reportId 报告ID
     * @param locale 语言环境（zh_CN/en_US）
     * @return HTML报告文件
     */
    @GetMapping("/{reportId}/export/allure")
    @GlobalInterceptor(checkLogin = true)
    public ResponseEntity<Resource> exportAllureReport(@PathVariable("reportId") Long reportId,
                                                       @RequestParam(value = "locale", required = false, defaultValue = "zh_CN") String locale) {
        try {
            log.info("开始导出Allure风格测试报告: reportId={}, locale={}", reportId, locale);

            // 导出Allure风格测试报告
            Resource resource = allureReportService.exportAllureReport(reportId, locale);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();

            // 设置Content-Disposition，支持中文文件名
            String fileName = resource.getFilename();
            if (fileName != null) {
                String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
                headers.setContentDispositionFormData("attachment", encodedFileName);
            }

            // 设置Content-Type为HTML
            headers.setContentType(MediaType.parseMediaType("text/html;charset=UTF-8"));

            // 设置缓存控制
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            log.info("Allure风格测试报告导出成功: reportId={}, fileName={}", reportId, fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IllegalArgumentException e) {
            log.error("导出Allure风格测试报告参数错误: reportId={}, error={}", reportId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("导出Allure风格测试报告失败: reportId={}, error={}", reportId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 删除测试报告
     * 
     * @param reportId 报告ID
     * @param force 是否强制删除（物理删除）
     * @return 删除结果
     */
    @DeleteMapping("/{reportId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<DeleteReportResponseDTO> deleteReport(@PathVariable("reportId") Long reportId,
                                                           @RequestParam(value = "force", required = false) Boolean force) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer currentUserId = 1; // 临时硬编码，实际应该从认证上下文获取
            
            // 执行删除操作
            DeleteReportResponseDTO result = reportService.deleteTestReport(reportId, force, currentUserId);
            
            // 根据删除类型返回不同的成功消息
            String message = "hard_delete".equals(result.getDeletionType()) ? "报告已永久删除" : "报告删除成功";
            
            return ResponseVO.success(message, result);
            
        } catch (IllegalArgumentException e) {
            // 根据不同的错误类型返回不同的错误响应
            if (e.getMessage().contains("报告不存在")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("报告已被删除")) {
                return ResponseVO.businessError(e.getMessage());
            } else if (e.getMessage().contains("被其他数据引用")) {
                return ResponseVO.businessError(e.getMessage());
            } else if (e.getMessage().contains("权限不足")) {
                return ResponseVO.forbidden(e.getMessage());
            } else if (e.getMessage().contains("参数验证失败") || 
                      e.getMessage().contains("不能为空") || 
                      e.getMessage().contains("小于等于0")) {
                return ResponseVO.paramError(e.getMessage());
            } else {
                return ResponseVO.businessError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("删除报告失败：" + e.getMessage());
        }
    }
}
