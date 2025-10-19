package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 报告导出查询DTO
 */
@Data
public class ReportExportQueryDTO {
    
    /**
     * 报告ID
     */
    private Long reportId;
    
    /**
     * 导出格式：excel, csv, json
     */
    private String exportFormat;
    
    /**
     * 是否包含详细的用例执行结果
     */
    private Boolean includeDetails = true;
    
    /**
     * 是否包含附件信息（链接）
     */
    private Boolean includeAttachments = false;
    
    /**
     * 是否包含失败详情
     */
    private Boolean includeFailureDetails = true;
    
    /**
     * 时区设置
     */
    private String timezone = "UTC";
}
