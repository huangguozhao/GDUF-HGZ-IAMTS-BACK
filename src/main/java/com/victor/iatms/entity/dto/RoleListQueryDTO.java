package com.victor.iatms.entity.dto;

import lombok.Data;

@Data
public class RoleListQueryDTO {
    private String roleName;
    private Boolean isSuperAdmin;
    private Boolean includeDeleted;
    private Integer page = 1;
    private Integer pageSize = 10;
}

