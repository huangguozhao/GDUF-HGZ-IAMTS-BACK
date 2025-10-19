package com.victor.iatms.entity.dto;

import lombok.Data;
import java.util.List;

/**
 * 报告依赖检查响应DTO
 */
@Data
public class ReportDependencyCheckDTO {
    
    /**
     * 报告ID
     */
    private Long reportId;
    
    /**
     * 依赖列表
     */
    private List<ReportDependencyDTO> dependencies;
}
