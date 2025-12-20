package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 更新用户项目成员信息 请求DTO
 */
@Data
public class UpdateUserProjectDTO {
    private String projectRole;       // owner, manager, developer, tester, viewer
    private String permissionLevel;   // read, write, admin
    private String status;            // active, inactive, removed
    private String additionalRoles;   // JSON string simplified
    private String customPermissions; // JSON string simplified
    private String notes;
}