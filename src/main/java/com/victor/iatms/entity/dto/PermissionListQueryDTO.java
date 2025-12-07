package com.victor.iatms.entity.dto;

import lombok.Data;

@Data
public class PermissionListQueryDTO {
    private String permissionName;
    private Integer roleId;       // optional: when present, mark is_assigned
    private Boolean includeDeleted;
    private Integer page = 1;
    private Integer pageSize = 10;
}

