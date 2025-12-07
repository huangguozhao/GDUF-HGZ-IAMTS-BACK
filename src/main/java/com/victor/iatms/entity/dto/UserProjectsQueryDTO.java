package com.victor.iatms.entity.dto;

import lombok.Data;

@Data
public class UserProjectsQueryDTO {
    private Integer userId;
    private String status;       // active, inactive, removed
    private String projectRole;  // owner, manager, developer, tester, viewer
    private Integer page = 1;
    private Integer pageSize = 10;
}

