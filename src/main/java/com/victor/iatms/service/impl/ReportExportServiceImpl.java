package com.victor.iatms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.iatms.entity.constants.Constants;
import com.victor.iatms.entity.dto.ReportExportQueryDTO;
import com.victor.iatms.entity.dto.ReportExportResponseDTO;
import com.victor.iatms.entity.enums.ReportExportFormatEnum;
import com.victor.iatms.entity.po.TestReportSummary;
import com.victor.iatms.mappers.ReportMapper;
import com.victor.iatms.service.ReportExportService;
import com.victor.iatms.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 报告导出服务实现类
 */
@Slf4j
@Service
public class ReportExportServiceImpl implements ReportExportService {
    
    @Autowired
    private ReportMapper reportMapper;
    
    @Autowired
    private ReportService reportService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Resource exportReport(ReportExportQueryDTO queryDTO, Integer currentUserId) {
        // 参数校验
        validateExportQuery(queryDTO);

        // 验证报告是否可以导出
        validateReportForExport(queryDTO.getReportId());

        // 获取导出数据
        ReportExportResponseDTO exportData = getReportExportData(
            queryDTO.getReportId(),
            queryDTO.getIncludeDetails(),
            queryDTO.getIncludeAttachments(),
            queryDTO.getIncludeFailureDetails(),
            currentUserId
        );

        // 根据格式生成文件
        byte[] fileContent = generateFileContent(exportData, queryDTO.getExportFormat());

        // 创建资源
        String fileName = generateExportFileName(queryDTO.getReportId(), queryDTO.getExportFormat());
        return new ByteArrayResource(fileContent) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };
    }
    
    @Override
    public ReportExportResponseDTO getReportExportData(Long reportId, Boolean includeDetails,
                                                      Boolean includeAttachments, Boolean includeFailureDetails, Integer currentUserId) {
        // 获取报告基本信息
        ReportExportResponseDTO.ReportSummaryInfoDTO reportSummary = reportMapper.selectReportExportData(reportId);
        if (reportSummary == null) {
            throw new IllegalArgumentException("报告不存在");
        }
        
        // 获取统计信息
        ReportExportResponseDTO.ReportStatisticsDTO statistics = reportMapper.selectReportStatistics(reportId);
        
        // 获取测试结果详情
        List<ReportExportResponseDTO.TestCaseResultDTO> testResults = null;
        if (includeDetails != null && includeDetails) {
            testResults = reportMapper.selectReportTestResults(reportId, includeDetails, includeAttachments, includeFailureDetails);
            
            // 调试日志：检查失败用例的详情是否被查询
            if (testResults != null && !testResults.isEmpty()) {
                long failedCount = testResults.stream()
                    .filter(r -> "failed".equalsIgnoreCase(r.getStatus()) || "broken".equalsIgnoreCase(r.getStatus()))
                    .count();
                long withFailureInfo = testResults.stream()
                    .filter(r -> ("failed".equalsIgnoreCase(r.getStatus()) || "broken".equalsIgnoreCase(r.getStatus()))
                                 && r.getFailureMessage() != null)
                    .count();
                log.debug("=== 报告导出调试信息 ===");
                log.debug("总测试结果数: {}, 失败/异常用例数: {}, 包含失败信息的用例数: {}, includeFailureDetails: {}",
                    testResults.size(), failedCount, withFailureInfo, includeFailureDetails);

                // 打印第一个失败用例的详情
                testResults.stream()
                    .filter(r -> "failed".equalsIgnoreCase(r.getStatus()) || "broken".equalsIgnoreCase(r.getStatus()))
                    .findFirst()
                    .ifPresent(r -> {
                        log.debug("第一个失败用例: 用例名称: {}, 失败消息: {}, 失败类型: {}, 堆栈跟踪长度: {}",
                            r.getCaseName(), r.getFailureMessage(), r.getFailureType(),
                            r.getFailureTrace() != null ? r.getFailureTrace().length() : 0);
                    });
            }
        }
        
        // 构建导出元数据
        ReportExportResponseDTO.ExportMetadataDTO exportMetadata = new ReportExportResponseDTO.ExportMetadataDTO();
        exportMetadata.setExportedAt(LocalDateTime.now());
        exportMetadata.setExportedBy(currentUserId != null ? currentUserId : 1);
        exportMetadata.setIncludeDetails(includeDetails);
        exportMetadata.setIncludeAttachments(includeAttachments);
        
        // 构建响应
        ReportExportResponseDTO response = new ReportExportResponseDTO();
        response.setReportSummary(reportSummary);
        response.setStatistics(statistics);
        response.setTestResults(testResults);
        response.setExportMetadata(exportMetadata);
        
        return response;
    }
    
    @Override
    public TestReportSummary validateReportForExport(Long reportId) {
        if (reportId == null || reportId <= 0) {
            throw new IllegalArgumentException("报告ID不能为空或小于等于0");
        }
        
        TestReportSummary report = reportService.getReportById(reportId);
        
        // 检查报告状态
        if (Constants.REPORT_STATUS_GENERATING.equals(report.getReportStatus())) {
            // ⭐ 优化：如果报告已有数据，即使状态是generating也允许导出
            if (report.getTotalCases() != null && report.getTotalCases() > 0) {
                log.warn("报告{}状态为generating但已有数据，允许导出", reportId);
            } else {
                throw new IllegalArgumentException("报告正在生成中，请稍后再试");
            }
        }
        
//        if (Constants.REPORT_STATUS_FAILED.equals(report.getReportStatus())) {
//            throw new IllegalArgumentException("报告生成失败，无法导出");
//        }
        
        return report;
    }
    
    @Override
    public String generateExportFileName(Long reportId, String exportFormat) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String extension = exportFormat.toLowerCase();
        return String.format(Constants.EXPORT_FILE_NAME_FORMAT, reportId, timestamp, extension);
    }
    
    /**
     * 校验导出查询参数
     */
    private void validateExportQuery(ReportExportQueryDTO queryDTO) {
        if (queryDTO == null) {
            throw new IllegalArgumentException("导出参数不能为空");
        }
        
        if (queryDTO.getReportId() == null || queryDTO.getReportId() <= 0) {
            throw new IllegalArgumentException("报告ID不能为空或小于等于0");
        }
        
        if (!StringUtils.hasText(queryDTO.getExportFormat())) {
            throw new IllegalArgumentException("导出格式不能为空");
        }
        
        if (!ReportExportFormatEnum.isValid(queryDTO.getExportFormat())) {
            throw new IllegalArgumentException("不支持的导出格式：" + queryDTO.getExportFormat());
        }
    }
    
    /**
     * 根据格式生成文件内容
     */
    private byte[] generateFileContent(ReportExportResponseDTO exportData, String exportFormat) {
        try {
            switch (exportFormat.toLowerCase()) {
                case "html":
                    return generateHtmlContent(exportData);
                case "excel":
                    return generateExcelContent(exportData);
                case "csv":
                    return generateCsvContent(exportData);
                case "json":
                    return generateJsonContent(exportData);
                case "pdf":
                    return generatePdfContent(exportData);
                default:
                    throw new IllegalArgumentException("不支持的导出格式：" + exportFormat);
            }
        } catch (Exception e) {
            throw new RuntimeException("生成导出文件失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 生成HTML内容（使用HTMLTemplateBuilder）
     */
    private byte[] generateHtmlContent(ReportExportResponseDTO exportData) throws IOException {
        com.victor.iatms.utils.HTMLTemplateBuilder builder = 
            new com.victor.iatms.utils.HTMLTemplateBuilder(exportData);
        String htmlContent = builder.build();
        return htmlContent.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
    
    /**
     * 生成HTML内容（旧版本，已弃用）
     */
    @Deprecated
    private byte[] generateHtmlContentOld(ReportExportResponseDTO exportData) throws IOException {
        StringBuilder html = new StringBuilder();
        
        // 准备图表数据
        ReportExportResponseDTO.ReportStatisticsDTO stats = exportData.getStatistics();
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"zh-CN\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>").append(exportData.getReportSummary().getReportName()).append("</title>\n");
        html.append("    <script src=\"https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js\"></script>\n");
        html.append("    <style>\n");
        html.append("        * { margin: 0; padding: 0; box-sizing: border-box; }\n");
        html.append("        body { font-family: 'Microsoft YaHei', 'Segoe UI', Arial, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; }\n");
        html.append("        .container { max-width: 1400px; margin: 0 auto; background-color: white; border-radius: 15px; box-shadow: 0 10px 40px rgba(0,0,0,0.2); overflow: hidden; }\n");
        html.append("        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 40px; text-align: center; }\n");
        html.append("        .header h1 { font-size: 32px; margin-bottom: 10px; }\n");
        html.append("        .header .subtitle { font-size: 16px; opacity: 0.9; }\n");
        html.append("        .content { padding: 30px; }\n");
        html.append("        h2 { color: #333; margin: 30px 0 20px 0; padding-bottom: 10px; border-bottom: 2px solid #667eea; font-size: 24px; }\n");
        html.append("        .info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin: 20px 0; }\n");
        html.append("        .info-item { background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); padding: 20px; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }\n");
        html.append("        .info-label { font-weight: bold; color: #555; font-size: 12px; text-transform: uppercase; letter-spacing: 1px; }\n");
        html.append("        .info-value { color: #333; font-size: 18px; margin-top: 8px; font-weight: 600; }\n");
        html.append("        .stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 20px; margin: 30px 0; }\n");
        html.append("        .stat-card { text-align: center; padding: 25px; color: white; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.2); transition: transform 0.3s; }\n");
        html.append("        .stat-card:hover { transform: translateY(-5px); }\n");
        html.append("        .stat-card.total { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }\n");
        html.append("        .stat-card.success { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }\n");
        html.append("        .stat-card.failed { background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%); }\n");
        html.append("        .stat-card.skipped { background: linear-gradient(135deg, #f2994a 0%, #f2c94c 100%); }\n");
        html.append("        .stat-card.broken { background: linear-gradient(135deg, #9c27b0 0%, #e91e63 100%); }\n");
        html.append("        .stat-number { font-size: 42px; font-weight: bold; margin-bottom: 8px; }\n");
        html.append("        .stat-label { font-size: 14px; opacity: 0.95; text-transform: uppercase; letter-spacing: 1px; }\n");
        html.append("        .charts-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(400px, 1fr)); gap: 30px; margin: 30px 0; }\n");
        html.append("        .chart-container { background: white; padding: 25px; border-radius: 12px; box-shadow: 0 2px 15px rgba(0,0,0,0.1); }\n");
        html.append("        .chart-title { font-size: 18px; font-weight: 600; color: #333; margin-bottom: 15px; text-align: center; }\n");
        html.append("        table { width: 100%; border-collapse: collapse; margin: 20px 0; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n");
        html.append("        th, td { padding: 15px; text-align: left; }\n");
        html.append("        th { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; font-weight: 600; text-transform: uppercase; font-size: 12px; letter-spacing: 1px; }\n");
        html.append("        tr:nth-child(even) { background-color: #f8f9fa; }\n");
        html.append("        tr:hover { background-color: #e9ecef; }\n");
        html.append("        .status-badge { display: inline-block; padding: 5px 12px; border-radius: 20px; font-size: 11px; font-weight: bold; text-transform: uppercase; }\n");
        html.append("        .status-passed { background-color: #d4edda; color: #155724; }\n");
        html.append("        .status-failed { background-color: #f8d7da; color: #721c24; }\n");
        html.append("        .status-skipped { background-color: #fff3cd; color: #856404; }\n");
        html.append("        .status-broken { background-color: #f5c6cb; color: #721c24; }\n");
        html.append("        .priority-badge { display: inline-block; padding: 4px 10px; border-radius: 4px; font-size: 11px; font-weight: bold; }\n");
        html.append("        .priority-P0 { background-color: #dc3545; color: white; }\n");
        html.append("        .priority-P1 { background-color: #ffc107; color: #333; }\n");
        html.append("        .priority-P2 { background-color: #17a2b8; color: white; }\n");
        html.append("        .priority-P3 { background-color: #6c757d; color: white; }\n");
        html.append("        .error-section { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 20px; margin: 20px 0; border-radius: 8px; }\n");
        html.append("        .error-title { font-weight: bold; color: #856404; margin-bottom: 10px; font-size: 16px; }\n");
        html.append("        .error-details { background-color: #fff; padding: 15px; border-radius: 5px; margin-top: 10px; }\n");
        html.append("        .error-message { color: #d32f2f; font-weight: 600; margin-bottom: 10px; }\n");
        html.append("        .error-trace { background-color: #f5f5f5; padding: 10px; border-radius: 4px; font-family: 'Courier New', monospace; font-size: 12px; color: #333; white-space: pre-wrap; word-wrap: break-word; max-height: 200px; overflow-y: auto; }\n");
        html.append("        .failure-section { margin-top: 30px; }\n");
        html.append("        .failure-card { background-color: #fff; border: 1px solid #dee2e6; border-radius: 8px; padding: 20px; margin-bottom: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }\n");
        html.append("        .failure-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; padding-bottom: 10px; border-bottom: 2px solid #f8d7da; }\n");
        html.append("        .failure-case-name { font-size: 16px; font-weight: 600; color: #333; }\n");
        html.append("        .failure-meta { font-size: 12px; color: #666; margin-top: 5px; }\n");
        html.append("        .footer { margin-top: 50px; padding: 30px; background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); text-align: center; color: #666; font-size: 13px; border-radius: 0 0 15px 15px; }\n");
        html.append("        .footer p { margin: 5px 0; }\n");
        html.append("        @media print { body { background: white; padding: 0; } .container { box-shadow: none; } }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class=\"container\">\n");
        
        // 报告头部
        html.append("        <div class=\"header\">\n");
        html.append("            <h1>").append(escapeHtml(exportData.getReportSummary().getReportName())).append("</h1>\n");
        html.append("            <div class=\"subtitle\">").append(escapeHtml(exportData.getReportSummary().getProjectName())).append(" · ").append(exportData.getReportSummary().getEnvironment()).append("环境</div>\n");
        html.append("        </div>\n");
        
        html.append("        <div class=\"content\">\n");
        
        // 基本信息
        html.append("            <h2>📋 报告概览</h2>\n");
        html.append("            <div class=\"info-grid\">\n");
        html.append("                <div class=\"info-item\"><div class=\"info-label\">报告类型</div><div class=\"info-value\">").append(exportData.getReportSummary().getReportType()).append("</div></div>\n");
        html.append("                <div class=\"info-item\"><div class=\"info-label\">开始时间</div><div class=\"info-value\">").append(exportData.getReportSummary().getStartTime()).append("</div></div>\n");
        html.append("                <div class=\"info-item\"><div class=\"info-label\">结束时间</div><div class=\"info-value\">").append(exportData.getReportSummary().getEndTime()).append("</div></div>\n");
        html.append("                <div class=\"info-item\"><div class=\"info-label\">总耗时</div><div class=\"info-value\">").append(formatDuration(exportData.getReportSummary().getDuration())).append("</div></div>\n");
        html.append("            </div>\n");
        
        // 统计卡片
        html.append("            <h2>📊 执行统计</h2>\n");
        html.append("            <div class=\"stats\">\n");
        html.append("                <div class=\"stat-card total\"><div class=\"stat-number\">").append(exportData.getReportSummary().getTotalCases()).append("</div><div class=\"stat-label\">总用例数</div></div>\n");
        html.append("                <div class=\"stat-card success\"><div class=\"stat-number\">").append(exportData.getReportSummary().getPassedCases()).append("</div><div class=\"stat-label\">通过 ✓</div></div>\n");
        html.append("                <div class=\"stat-card failed\"><div class=\"stat-number\">").append(exportData.getReportSummary().getFailedCases()).append("</div><div class=\"stat-label\">失败 ✗</div></div>\n");
        html.append("                <div class=\"stat-card broken\"><div class=\"stat-number\">").append(exportData.getReportSummary().getBrokenCases()).append("</div><div class=\"stat-label\">异常 ⚠</div></div>\n");
        html.append("                <div class=\"stat-card skipped\"><div class=\"stat-number\">").append(exportData.getReportSummary().getSkippedCases()).append("</div><div class=\"stat-label\">跳过 ⊘</div></div>\n");
        html.append("                <div class=\"stat-card total\"><div class=\"stat-number\">").append(String.format("%.1f%%", exportData.getReportSummary().getSuccessRate())).append("</div><div class=\"stat-label\">成功率</div></div>\n");
        html.append("            </div>\n");
        
        // 图表部分
        if (stats != null) {
            html.append("            <h2>📈 数据可视化</h2>\n");
            html.append("            <div class=\"charts-grid\">\n");
            
            // 状态分布饼图
            html.append("                <div class=\"chart-container\">\n");
            html.append("                    <div class=\"chart-title\">测试结果分布</div>\n");
            html.append("                    <canvas id=\"statusChart\"></canvas>\n");
            html.append("                </div>\n");
            
            // 优先级分布柱状图
            html.append("                <div class=\"chart-container\">\n");
            html.append("                    <div class=\"chart-title\">优先级分布</div>\n");
            html.append("                    <canvas id=\"priorityChart\"></canvas>\n");
            html.append("                </div>\n");
            
            // 严重程度分布
            html.append("                <div class=\"chart-container\">\n");
            html.append("                    <div class=\"chart-title\">严重程度分布</div>\n");
            html.append("                    <canvas id=\"severityChart\"></canvas>\n");
            html.append("                </div>\n");
            
            html.append("            </div>\n");
        }
        
        // 失败用例详情
        if (exportData.getTestResults() != null) {
            java.util.List<ReportExportResponseDTO.TestCaseResultDTO> failedCases = exportData.getTestResults().stream()
                .filter(r -> "failed".equalsIgnoreCase(r.getStatus()) || "broken".equalsIgnoreCase(r.getStatus()))
                .collect(java.util.stream.Collectors.toList());
            
            if (!failedCases.isEmpty()) {
                html.append("            <h2>❌ 失败用例详情</h2>\n");
                html.append("            <div class=\"failure-section\">\n");
                
                for (ReportExportResponseDTO.TestCaseResultDTO failedCase : failedCases) {
                    html.append("                <div class=\"failure-card\">\n");
                    html.append("                    <div class=\"failure-header\">\n");
                    html.append("                        <div>\n");
                    html.append("                            <div class=\"failure-case-name\">").append(escapeHtml(failedCase.getCaseName() != null ? failedCase.getCaseName() : "未命名用例")).append("</div>\n");
                    html.append("                            <div class=\"failure-meta\">用例编号: ").append(failedCase.getCaseCode() != null ? failedCase.getCaseCode() : "N/A").append(" | 优先级: ").append(failedCase.getPriority() != null ? failedCase.getPriority() : "N/A").append(" | 严重程度: ").append(failedCase.getSeverity() != null ? failedCase.getSeverity() : "N/A").append("</div>\n");
                    html.append("                        </div>\n");
                    html.append("                        <span class=\"status-badge status-").append(failedCase.getStatus() != null ? failedCase.getStatus().toLowerCase() : "failed").append("\">").append(failedCase.getStatus() != null ? failedCase.getStatus().toUpperCase() : "FAILED").append("</span>\n");
                    html.append("                    </div>\n");
                    
                    if (failedCase.getFailureMessage() != null && !failedCase.getFailureMessage().isEmpty()) {
                        html.append("                    <div class=\"error-details\">\n");
                        html.append("                        <div class=\"error-message\">❗ 错误信息: ").append(escapeHtml(failedCase.getFailureMessage())).append("</div>\n");
                        
                        if (failedCase.getFailureType() != null && !failedCase.getFailureType().isEmpty()) {
                            html.append("                        <div style=\"margin-bottom: 10px; color: #666;\">错误类型: ").append(escapeHtml(failedCase.getFailureType())).append("</div>\n");
                        }
                        
                        if (failedCase.getFailureTrace() != null && !failedCase.getFailureTrace().isEmpty()) {
                            html.append("                        <div class=\"error-trace\">").append(escapeHtml(failedCase.getFailureTrace())).append("</div>\n");
                        }
                        html.append("                    </div>\n");
                    }
                    
                    html.append("                </div>\n");
                }
                
                html.append("            </div>\n");
            }
        }
        
        // 所有测试用例列表
        if (exportData.getTestResults() != null && !exportData.getTestResults().isEmpty()) {
            html.append("            <h2>📝 所有测试用例</h2>\n");
            html.append("            <table>\n");
            html.append("                <thead>\n");
            html.append("                    <tr><th>用例编号</th><th>用例名称</th><th>状态</th><th>优先级</th><th>严重程度</th><th>耗时</th><th>开始时间</th></tr>\n");
            html.append("                </thead>\n");
            html.append("                <tbody>\n");
            for (ReportExportResponseDTO.TestCaseResultDTO result : exportData.getTestResults()) {
                html.append("                    <tr>\n");
                html.append("                        <td>").append(result.getCaseCode() != null ? escapeHtml(result.getCaseCode()) : "N/A").append("</td>\n");
                html.append("                        <td>").append(result.getCaseName() != null ? escapeHtml(result.getCaseName()) : "N/A").append("</td>\n");
                html.append("                        <td><span class=\"status-badge status-").append(result.getStatus() != null ? result.getStatus().toLowerCase() : "unknown").append("\">").append(result.getStatus() != null ? result.getStatus().toUpperCase() : "UNKNOWN").append("</span></td>\n");
                html.append("                        <td><span class=\"priority-badge priority-").append(result.getPriority() != null ? result.getPriority() : "P3").append("\">").append(result.getPriority() != null ? result.getPriority() : "N/A").append("</span></td>\n");
                html.append("                        <td>").append(result.getSeverity() != null ? result.getSeverity() : "N/A").append("</td>\n");
                html.append("                        <td>").append(formatDuration(result.getDuration() != null ? result.getDuration().longValue() : 0L)).append("</td>\n");
                html.append("                        <td>").append(result.getStartTime() != null ? result.getStartTime() : "N/A").append("</td>\n");
                html.append("                    </tr>\n");
            }
            html.append("                </tbody>\n");
            html.append("            </table>\n");
        }
        
        html.append("        </div>\n");
        
        // 页脚
        html.append("        <div class=\"footer\">\n");
        html.append("            <p><strong>报告生成时间:</strong> ").append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</p>\n");
        html.append("            <p>IATMS - 接口自动化测试管理系统 | Powered by Spring Boot & MyBatis</p>\n");
        html.append("        </div>\n");
        html.append("    </div>\n");
        
        // Chart.js 图表脚本
        if (stats != null) {
            html.append("    <script>\n");
            
            // 状态分布饼图
            html.append("        const statusCtx = document.getElementById('statusChart').getContext('2d');\n");
            html.append("        new Chart(statusCtx, {\n");
            html.append("            type: 'doughnut',\n");
            html.append("            data: {\n");
            html.append("                labels: ['通过', '失败', '异常', '跳过'],\n");
            html.append("                datasets: [{\n");
            html.append("                    data: [").append(exportData.getReportSummary().getPassedCases()).append(", ")
                                                    .append(exportData.getReportSummary().getFailedCases()).append(", ")
                                                    .append(exportData.getReportSummary().getBrokenCases()).append(", ")
                                                    .append(exportData.getReportSummary().getSkippedCases()).append("],\n");
            html.append("                    backgroundColor: ['#38ef7d', '#f45c43', '#e91e63', '#f2c94c'],\n");
            html.append("                    borderWidth: 2,\n");
            html.append("                    borderColor: '#fff'\n");
            html.append("                }]\n");
            html.append("            },\n");
            html.append("            options: { responsive: true, maintainAspectRatio: true, plugins: { legend: { position: 'bottom' } } }\n");
            html.append("        });\n");
            
            // 优先级分布柱状图
            if (stats.getByPriority() != null) {
                html.append("        const priorityCtx = document.getElementById('priorityChart').getContext('2d');\n");
                html.append("        new Chart(priorityCtx, {\n");
                html.append("            type: 'bar',\n");
                html.append("            data: {\n");
                html.append("                labels: [").append(stats.getByPriority().keySet().stream().map(k -> "'" + k + "'").collect(java.util.stream.Collectors.joining(", "))).append("],\n");
                html.append("                datasets: [{\n");
                html.append("                    label: '用例数量',\n");
                html.append("                    data: [").append(stats.getByPriority().values().stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(", "))).append("],\n");
                html.append("                    backgroundColor: ['#dc3545', '#ffc107', '#17a2b8', '#6c757d'],\n");
                html.append("                    borderWidth: 0\n");
                html.append("                }]\n");
                html.append("            },\n");
                html.append("            options: { responsive: true, maintainAspectRatio: true, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true } } }\n");
                html.append("        });\n");
            }
            
            // 严重程度分布
            if (stats.getBySeverity() != null) {
                html.append("        const severityCtx = document.getElementById('severityChart').getContext('2d');\n");
                html.append("        new Chart(severityCtx, {\n");
                html.append("            type: 'polarArea',\n");
                html.append("            data: {\n");
                html.append("                labels: [").append(stats.getBySeverity().keySet().stream().map(k -> "'" + k + "'").collect(java.util.stream.Collectors.joining(", "))).append("],\n");
                html.append("                datasets: [{\n");
                html.append("                    data: [").append(stats.getBySeverity().values().stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(", "))).append("],\n");
                html.append("                    backgroundColor: ['#dc3545', '#ff6384', '#ff9f40', '#4bc0c0'],\n");
                html.append("                }]\n");
                html.append("            },\n");
                html.append("            options: { responsive: true, maintainAspectRatio: true, plugins: { legend: { position: 'bottom' } } }\n");
                html.append("        });\n");
            }
            
            html.append("    </script>\n");
        }
        
        html.append("</body>\n");
        html.append("</html>");
        
        return html.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
    
    /**
     * HTML转义，防止XSS
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    /**
     * 格式化持续时间（毫秒转为可读格式）
     */
    private String formatDuration(Long durationMs) {
        if (durationMs == null || durationMs == 0) {
            return "0秒";
        }
        long seconds = durationMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%d小时%d分钟%d秒", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%d分钟%d秒", minutes, seconds % 60);
        } else {
            return String.format("%d秒", seconds);
        }
    }
    
    /**
     * 生成Excel内容
     */
    private byte[] generateExcelContent(ReportExportResponseDTO exportData) throws IOException {
        // TODO: 使用Apache POI或EasyExcel生成Excel文件
        // 这里先返回JSON格式作为占位符
        return generateJsonContent(exportData);
    }
    
    /**
     * 生成CSV内容
     */
    private byte[] generateCsvContent(ReportExportResponseDTO exportData) throws IOException {
        // TODO: 生成CSV格式文件
        // 这里先返回JSON格式作为占位符
        return generateJsonContent(exportData);
    }
    
    /**
     * 生成JSON内容
     */
    private byte[] generateJsonContent(ReportExportResponseDTO exportData) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        objectMapper.writeValue(outputStream, exportData);
        return outputStream.toByteArray();
    }
    
    /**
     * 清理文本用于PDF显示 - 移除换行符等非法字符
     */
    private String sanitizeTextForPdf(String text) {
        if (text == null) {
            return "";
        }
        // 替换换行符和回车符为空格
        return text.replace("\n", " ").replace("\r", " ").replace("\t", " ");
    }
    
    /**
     * 生成PDF内容
     */
    private byte[] generatePdfContent(ReportExportResponseDTO exportData) throws IOException {
        PDDocument document = new PDDocument();
        try {
            // 加载中文字体支持
            PDType0Font font = null;
            PDType0Font boldFont = null;
            boolean chineseFontLoaded = false;
            
            // 尝试从 classpath 加载中文字体
            String[] classpathFonts = {
                "fonts/NotoSansSC-Regular.ttf",
                "fonts/simsun.ttc"
            };
            
            for (String fontPath : classpathFonts) {
                try {
                    InputStream fontStream = getClass().getClassLoader().getResourceAsStream(fontPath);
                    if (fontStream != null) {
                        font = PDType0Font.load(document, fontStream, true);
                        boldFont = font;
                        log.info("成功从classpath加载中文字体: {}", fontPath);
                        chineseFontLoaded = true;
                        break;
                    }
                } catch (Exception e) {
                    log.warn("尝试从classpath加载字体失败: {}", fontPath);
                }
            }
            
            if (!chineseFontLoaded) {
                // 尝试从系统字体目录加载
                String[] systemFontPaths = {
                    "C:/Windows/Fonts/msyh.ttc",
                    "C:/Windows/Fonts/simsun.ttc",
                    "C:/Windows/Fonts/simhei.ttf"
                };
                
                for (String fontPath : systemFontPaths) {
                    try {
                        File fontFile = new File(fontPath);
                        if (fontFile.exists()) {
                            font = PDType0Font.load(document, new FileInputStream(fontFile), true);
                            boldFont = PDType0Font.load(document, new FileInputStream(fontFile), true);
                            log.info("成功加载系统字体: {}", fontPath);
                            chineseFontLoaded = true;
                            break;
                        }
                    } catch (Exception e) {
                        log.warn("尝试加载系统字体失败: {}", fontPath);
                    }
                }
            }
            
            if (!chineseFontLoaded) {
                throw new IOException("PDF中文渲染失败：请在 C:/Windows/Fonts 目录确保有 msyh.ttc（微软雅黑）或 simsun.ttc（宋体）字体文件");
            }
            
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            PDPageContentStream content = new PDPageContentStream(document, page);
            
            float yPosition = 800;
            
            // ===== 标题 =====
            content.beginText();
            content.newLineAtOffset(50, yPosition);
            content.setFont(boldFont, 20);
            String title = sanitizeTextForPdf(exportData.getReportSummary().getReportName());
            content.showText(title != null ? title : "Test Report");
            content.endText();
            
            yPosition -= 40;
            
            // ===== 基本信息 =====
            content.beginText();
            content.newLineAtOffset(50, yPosition);
            content.setFont(boldFont, 14);
            content.showText("Report Overview");
            content.endText();
            
            yPosition -= 30;
            content.setFont(font, 10);
            
            ReportExportResponseDTO.ReportSummaryInfoDTO summary = exportData.getReportSummary();
            
            String startTimeStr = summary.getStartTime() != null ? summary.getStartTime().toString() : "N/A";
            String endTimeStr = summary.getEndTime() != null ? summary.getEndTime().toString() : "N/A";
            
            String[] infoLines = {
                "Project: " + sanitizeTextForPdf(summary.getProjectName()),
                "Environment: " + sanitizeTextForPdf(summary.getEnvironment()),
                "Report Type: " + sanitizeTextForPdf(summary.getReportType()),
                "Execution Time: " + sanitizeTextForPdf(startTimeStr) + " ~ " + sanitizeTextForPdf(endTimeStr),
                "Duration: " + formatDuration(summary.getDuration())
            };
            
            for (String line : infoLines) {
                content.beginText();
                content.newLineAtOffset(50, yPosition);
                content.showText(line);
                content.endText();
                yPosition -= 20;
            }
            
            yPosition -= 30;
            
            // ===== 统计信息 =====
            content.beginText();
            content.newLineAtOffset(50, yPosition);
            content.setFont(boldFont, 14);
            content.showText("Execution Statistics");
            content.endText();
            
            yPosition -= 30;
            content.setFont(font, 11);
            
            String[] statLines = {
                "Total Cases: " + summary.getTotalCases(),
                "Passed: " + summary.getPassedCases() + " | Failed: " + summary.getFailedCases() + " | Broken: " + summary.getBrokenCases() + " | Skipped: " + summary.getSkippedCases(),
                "Success Rate: " + String.format("%.2f%%", summary.getSuccessRate())
            };
            
            for (String line : statLines) {
                content.beginText();
                content.newLineAtOffset(50, yPosition);
                content.showText(line);
                content.endText();
                yPosition -= 20;
            }
            
            yPosition -= 30;
            
            // ===== 失败用例详情 =====
            List<ReportExportResponseDTO.TestCaseResultDTO> testResults = exportData.getTestResults();
            if (testResults != null && !testResults.isEmpty()) {
                List<ReportExportResponseDTO.TestCaseResultDTO> failedCases = testResults.stream()
                    .filter(r -> "failed".equalsIgnoreCase(r.getStatus()) || "broken".equalsIgnoreCase(r.getStatus()))
                    .limit(30)
                    .collect(java.util.stream.Collectors.toList());
                
                if (!failedCases.isEmpty()) {
                    content.beginText();
                    content.newLineAtOffset(50, yPosition);
                    content.setFont(boldFont, 14);
                    content.showText("Failed Cases (Top " + failedCases.size() + ")");
                    content.endText();
                    
                    yPosition -= 30;
                    
                    for (ReportExportResponseDTO.TestCaseResultDTO failedCase : failedCases) {
                        if (yPosition < 100) {
                            content.close();
                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            content = new PDPageContentStream(document, page);
                            yPosition = 800;
                        }
                        
                        content.setFont(boldFont, 10);
                        content.beginText();
                        content.newLineAtOffset(50, yPosition);
                        String caseName = sanitizeTextForPdf(failedCase.getCaseName());
                        if (caseName == null || caseName.isEmpty()) {
                            caseName = "Unnamed Case";
                        }
                        if (caseName.length() > 50) {
                            caseName = caseName.substring(0, 50) + "...";
                        }
                        content.showText("Case: " + caseName);
                        content.endText();
                        
                        yPosition -= 18;
                        content.setFont(font, 9);
                        
                        if (failedCase.getCaseCode() != null) {
                            content.beginText();
                            content.newLineAtOffset(50, yPosition);
                            content.showText("  Code: " + sanitizeTextForPdf(failedCase.getCaseCode()));
                            content.endText();
                            yPosition -= 15;
                        }
                        
                        if (failedCase.getStatus() != null) {
                            content.beginText();
                            content.newLineAtOffset(50, yPosition);
                            content.showText("  Status: " + failedCase.getStatus().toUpperCase());
                            content.endText();
                            yPosition -= 15;
                        }
                        
                        if (failedCase.getFailureMessage() != null && !failedCase.getFailureMessage().isEmpty()) {
                            content.beginText();
                            content.newLineAtOffset(50, yPosition);
                            String msg = sanitizeTextForPdf(failedCase.getFailureMessage());
                            if (msg.length() > 80) {
                                msg = msg.substring(0, 80) + "...";
                            }
                            content.showText("  Error: " + msg);
                            content.endText();
                            yPosition -= 15;
                        }
                        
                        if (failedCase.getDuration() != null) {
                            content.beginText();
                            content.newLineAtOffset(50, yPosition);
                            content.showText("  Duration: " + formatDuration(failedCase.getDuration()));
                            content.endText();
                            yPosition -= 15;
                        }
                        
                        yPosition -= 15;
                    }
                }
            }
            
            // ===== 页脚 =====
            yPosition -= 30;
            content.setFont(font, 8);
            content.beginText();
            content.newLineAtOffset(50, yPosition);
            content.showText("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            content.endText();
            
            yPosition -= 12;
            content.beginText();
            content.newLineAtOffset(50, yPosition);
            content.showText("IATMS - Interface Automation Testing Management System");
            content.endText();
            
            content.close();
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        } finally {
            document.close();
        }
    }
}
