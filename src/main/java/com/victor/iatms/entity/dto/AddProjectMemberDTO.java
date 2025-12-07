package com.victor.iatms.entity.dto;

import lombok.Data;

@Data
public class AddProjectMemberDTO {
    private Integer userId; // required
    private String projectRole; // owner, manager, developer, tester, viewer
    private String permissionLevel; // read, write, admin
    private String additionalRoles; // JSON string simplified
    private String customPermissions; // JSON string simplified
    private String notes;
}
