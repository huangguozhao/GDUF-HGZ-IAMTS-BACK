package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 导出查询参数DTO
 */
@Data
public class ExportQueryDTO {

    /**
     * 接口ID
     */
    private Integer apiId;

    /**
     * 导出格式
     */
    private String format;

    /**
     * 是否包含已禁用的用例
     */
    private Boolean includeDisabled;

    /**
     * 是否包含模板用例
     */
    private Boolean includeTemplates;

    /**
     * 指定导出的字段
     */
    private List<String> fields;

    /**
     * 导出文件的名称（不包含扩展名）
     */
    private String filename;
}
