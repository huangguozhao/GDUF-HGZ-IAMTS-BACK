package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 复制测试用例请求DTO
 */
@Data
public class CopyTestCaseRequestDTO {

    /**
     * 新用例编码
     */
    private String caseCode;

    /**
     * 新用例名称
     */
    private String name;

    /**
     * 新用例描述
     */
    private String description;
}

