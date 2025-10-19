package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 编辑测试用例响应DTO
 */
@Data
public class UpdateTestCaseResponseDTO {

    /**
     * 用例ID
     */
    private Integer caseId;

    /**
     * 用例编码
     */
    private String caseCode;

    /**
     * 接口ID
     */
    private Integer apiId;

    /**
     * 用例名称
     */
    private String name;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 严重程度
     */
    private String severity;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
