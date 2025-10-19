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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 报告导出服务实现类
 */
@Service
public class ReportExportServiceImpl implements ReportExportService {
    
    @Autowired
    private ReportMapper reportMapper;
    
    @Autowired
    private ReportService reportService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Resource exportReport(ReportExportQueryDTO queryDTO) {
        // 参数校验
        validateExportQuery(queryDTO);
        
        // 验证报告是否可以导出
        validateReportForExport(queryDTO.getReportId());
        
        // 获取导出数据
        ReportExportResponseDTO exportData = getReportExportData(
            queryDTO.getReportId(),
            queryDTO.getIncludeDetails(),
            queryDTO.getIncludeAttachments(),
            queryDTO.getIncludeFailureDetails()
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
                                                      Boolean includeAttachments, Boolean includeFailureDetails) {
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
        }
        
        // 构建导出元数据
        ReportExportResponseDTO.ExportMetadataDTO exportMetadata = new ReportExportResponseDTO.ExportMetadataDTO();
        exportMetadata.setExportedAt(LocalDateTime.now());
        exportMetadata.setExportedBy(1); // TODO: 从当前用户上下文获取
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
            throw new IllegalArgumentException("报告正在生成中，请稍后再试");
        }
        
        if (Constants.REPORT_STATUS_FAILED.equals(report.getReportStatus())) {
            throw new IllegalArgumentException("报告生成失败，无法导出");
        }
        
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
                case "excel":
                    return generateExcelContent(exportData);
                case "csv":
                    return generateCsvContent(exportData);
                case "json":
                    return generateJsonContent(exportData);
                default:
                    throw new IllegalArgumentException("不支持的导出格式：" + exportFormat);
            }
        } catch (Exception e) {
            throw new RuntimeException("生成导出文件失败：" + e.getMessage(), e);
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
}
