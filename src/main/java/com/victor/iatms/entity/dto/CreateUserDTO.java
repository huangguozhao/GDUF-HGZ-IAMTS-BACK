package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * @description: 创建用户DTO
 * @author: victor
 * @date: 2025-12-07
 */
@Data
public class CreateUserDTO {

    private String name;

    private String email;

    private String password;

    private String phone;

    private String avatarUrl;

    private Integer departmentId;

    private String employeeId;

    private String position;

    private String description;

    private String status;
}


