package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PermissionListItemDTO {
    private Integer permissionId;
    private String permissionName;
    private String description;
    private Integer roleCount;
    private Boolean isAssigned; // when roleId is provided in query
    private Date createdAt;
    private Date updatedAt;
    private Boolean isDeleted;
    private Date deletedAt;
}

