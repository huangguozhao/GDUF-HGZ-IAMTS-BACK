package com.victor.iatms.entity.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目实体类
 */
@Data
public class Project {
    private Integer projectId;
    private String projectCode;
    private String name;
    private String description;
    private String status; // active, inactive, archived
    private String version;
    private Integer createdBy;
    private Integer updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private Integer deletedBy;
}
