package com.victor.iatms.entity.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 删除报告响应DTO
 */
@Data
public class DeleteReportResponseDTO {
    
    /**
     * 报告ID
     */
    private Long reportId;
    
    /**
     * 报告名称
     */
    private String reportName;
    
    /**
     * 是否删除成功
     */
    private Boolean deleted;
    
    /**
     * 删除类型：soft_delete（软删除）或 hard_delete（物理删除）
     */
    private String deletionType;
    
    /**
     * 删除时间
     */
    private LocalDateTime deletedAt;
    
    /**
     * 影响的记录数
     */
    private Integer affectedRows;
    
    /**
     * 删除人ID（软删除时）
     */
    private Integer deletedBy;
}
