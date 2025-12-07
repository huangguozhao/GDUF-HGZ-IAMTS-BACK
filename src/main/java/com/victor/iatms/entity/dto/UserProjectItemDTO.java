package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserProjectItemDTO {
    private Integer memberId;
    private Integer projectId;

    @Data
    public static class ProjectInfo {
        private String name;
        private String description;
        private Integer creatorId;
        private Date createdAt;
    }

    private ProjectInfo projectInfo;

    private String permissionLevel; // read, write, admin
    private String projectRole;     // owner, manager, developer, tester, viewer
    private String status;          // active, inactive, removed
    private Date joinTime;
    private Date leaveTime;
    private Integer assignedTasks;
    private Integer completedTasks;
    private String additionalRoles;     // JSON string simplified
    private String customPermissions;   // JSON string simplified
}
