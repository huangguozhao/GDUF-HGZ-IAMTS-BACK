package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 最后活动信息DTO
 */
@Data
public class LastActivityDTO {

    /**
     * 活动类型
     */
    private String type;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 活动时间
     */
    private LocalDateTime timestamp;

    /**
     * 操作用户ID
     */
    private Integer userId;

    /**
     * 操作用户姓名
     */
    private String userName;
}



