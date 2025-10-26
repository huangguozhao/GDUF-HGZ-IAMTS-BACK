package com.victor.iatms.service;

import com.victor.iatms.entity.dto.EnterpriseReportDTO;
import org.springframework.core.io.Resource;

/**
 * 企业级报告服务接口
 * 
 * @author Victor
 * @since 2024-10-26
 */
public interface EnterpriseReportService {
    
    /**
     * 导出企业级测试报告
     * 
     * @param reportId 报告ID
     * @param locale 语言环境（zh_CN/en_US）
     * @return HTML资源
     */
    Resource exportEnterpriseReport(Long reportId, String locale);
    
    /**
     * 构建企业级报告数据
     * 
     * @param reportId 报告ID
     * @return 企业级报告DTO
     */
    EnterpriseReportDTO buildEnterpriseReportData(Long reportId);
}

