package com.victor.iatms.service;

import com.victor.iatms.entity.dto.ISOEnterpriseReportDTO;
import org.springframework.core.io.Resource;

/**
 * ISO/IEC/IEEE 29119标准企业级报告服务接口
 * 
 * @author Victor
 * @since 2024-10-26
 */
public interface ISOEnterpriseReportService {
    
    /**
     * 导出ISO标准企业级报告
     * 
     * @param reportId 报告ID
     * @param locale 语言环境 (zh_CN/en_US)
     * @return 报告文件资源
     */
    Resource exportISOEnterpriseReport(Long reportId, String locale);
    
    /**
     * 构建ISO标准企业级报告数据
     * 
     * @param reportId 报告ID
     * @return 完整的ISO报告DTO
     */
    ISOEnterpriseReportDTO buildISOEnterpriseReportData(Long reportId);
}

