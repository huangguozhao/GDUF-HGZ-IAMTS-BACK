package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 附件信息DTO
 */
@Data
public class ArtifactDTO {

    /**
     * 附件类型
     */
    private String type;

    /**
     * 附件名称
     */
    private String name;

    /**
     * 附件URL
     */
    private String url;

    /**
     * 附件大小（字节）
     */
    private Long size;
}


