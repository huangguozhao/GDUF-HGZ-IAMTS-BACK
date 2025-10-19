package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 快捷操作DTO
 */
@Data
public class QuickActionDTO {

    /**
     * 操作名称
     */
    private String name;

    /**
     * 图标
     */
    private String icon;

    /**
     * 链接URL
     */
    private String url;

    /**
     * 描述
     */
    private String description;
}

