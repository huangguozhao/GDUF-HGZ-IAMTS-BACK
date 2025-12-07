package com.victor.iatms.entity.dto;

import lombok.Data;

@Data
public class UpdateRoleDTO {
    private String roleName;
    private String description;
    private Boolean isSuperAdmin;
}

