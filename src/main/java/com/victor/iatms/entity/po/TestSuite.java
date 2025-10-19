package com.victor.iatms.entity.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试套件实体类
 */
@Data
public class TestSuite {
    private Integer suiteId;
    private String suiteCode;
    private String name;
    private String description;
    private String status; // active, inactive, archived
    private Integer projectId;
    private Integer createdBy;
    private Integer updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private Integer deletedBy;
}
