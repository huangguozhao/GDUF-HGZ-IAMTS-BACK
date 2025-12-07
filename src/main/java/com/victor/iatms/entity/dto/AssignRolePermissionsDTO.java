package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssignRolePermissionsDTO {
    private List<Integer> permissionIds;
}
