package com.victor.iatms.entity.dto;

import lombok.Data;

@Data
public class UpdateProjectMemberDTO {
    private String projectRole;       // owner, manager, developer, tester, viewer
    private String permissionLevel;   // read, write, admin
    private String additionalRoles;   // JSON string simplified
    private String customPermissions; // JSON string simplified
    private String notes;
    private String status;            // active, inactive, removed
}
