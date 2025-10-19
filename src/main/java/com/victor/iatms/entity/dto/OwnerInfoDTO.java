package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 负责人信息DTO
 */
@Data
public class OwnerInfoDTO {
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户姓名
     */
    private String name;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
}
