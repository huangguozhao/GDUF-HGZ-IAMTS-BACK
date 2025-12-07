package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 为用户分配项目 请求DTO
 */
@Data
public class AssignUserProjectDTO {
    private Integer projectId;
    private String projectRole;       // owner, manager, developer, tester, viewer
    private String permissionLevel;   // read, write, admin
    private String additionalRoles;   // JSON string simplified
    private String customPermissions; // JSON string simplified
    private String notes;
}

