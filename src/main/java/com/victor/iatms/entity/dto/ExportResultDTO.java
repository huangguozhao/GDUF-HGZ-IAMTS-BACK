package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 导出结果DTO
 */
@Data
public class ExportResultDTO {

    /**
     * 文件字节数组
     */
    private byte[] fileData;

    /**
     * MIME类型
     */
    private String mimeType;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件大小
     */
    private long fileSize;
}
