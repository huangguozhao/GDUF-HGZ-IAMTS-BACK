package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @description: 更新用户DTO
 * @author: victor
 * @date: 2025-12-07
 */
@Data
public class UpdateUserDTO {

    private String name;

    private String email;

    private String phone;

    private String avatarUrl;

    private Integer departmentId;

    private String employeeId;

    private String position;

    private String description;

    private List<Integer> roleIds;
}


