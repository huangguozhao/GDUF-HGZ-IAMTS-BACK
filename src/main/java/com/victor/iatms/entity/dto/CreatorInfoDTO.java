package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 创建人信息DTO
 */
@Data
public class CreatorInfoDTO {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像URL
     */
    private String avatarUrl;
}
