package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RoleListItemDTO {
    private Integer roleId;
    private String roleName;
    private String description;
    private Boolean isSuperAdmin;
    private Integer userCount;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isDeleted;
    private Date deletedAt;
}

