package com.victor.iatms.entity.dto;

import lombok.Data;

@Data
public class CreateRoleDTO {
    private String roleName;      // required, unique
    private String description;
    private Boolean isSuperAdmin; // default false
}

