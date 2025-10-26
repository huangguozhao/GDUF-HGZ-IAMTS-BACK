package com.victor.iatms.service;

import com.victor.iatms.entity.dto.AllureReportDTO;
import org.springframework.core.io.Resource;

/**
 * Allure风格测试报告服务接口
 * 
 * @author Victor
 * @since 2024-10-26
 */
public interface AllureReportService {
    
    /**
     * 导出Allure风格测试报告
     * 
     * @param reportId 报告ID
     * @param locale 语言环境
     * @return HTML报告资源
     */
    Resource exportAllureReport(Long reportId, String locale);
    
    /**
     * 构建Allure报告数据
     * 
     * @param reportId 报告ID
     * @return Allure报告DTO
     */
    AllureReportDTO buildAllureReportData(Long reportId);
}

