package com.victor.iatms.service;

import com.victor.iatms.entity.dto.ReportExportQueryDTO;
import com.victor.iatms.entity.dto.ReportExportResponseDTO;
import com.victor.iatms.entity.po.TestReportSummary;
import org.springframework.core.io.Resource;

/**
 * 报告导出服务接口
 */
public interface ReportExportService {
    
    /**
     * 导出报告为指定格式
     * 
     * @param queryDTO 导出查询参数
     * @return 导出文件资源
     */
    Resource exportReport(ReportExportQueryDTO queryDTO);
    
    /**
     * 获取报告导出数据
     * 
     * @param reportId 报告ID
     * @param includeDetails 是否包含详细信息
     * @param includeAttachments 是否包含附件信息
     * @param includeFailureDetails 是否包含失败详情
     * @return 报告导出响应数据
     */
    ReportExportResponseDTO getReportExportData(Long reportId, Boolean includeDetails, 
                                               Boolean includeAttachments, Boolean includeFailureDetails);
    
    /**
     * 验证报告是否可以导出
     * 
     * @param reportId 报告ID
     * @return 报告信息
     */
    TestReportSummary validateReportForExport(Long reportId);
    
    /**
     * 生成导出文件名
     * 
     * @param reportId 报告ID
     * @param exportFormat 导出格式
     * @return 文件名
     */
    String generateExportFileName(Long reportId, String exportFormat);
}
