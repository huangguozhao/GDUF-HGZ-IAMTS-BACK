package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * @description: 更新用户状态DTO
 * @author: victor
 * @date: 2025-12-07
 */
@Data
public class UpdateUserStatusDTO {

    /**
     * 目标状态。可选值: active, inactive, pending
     */
    private String status;
}


