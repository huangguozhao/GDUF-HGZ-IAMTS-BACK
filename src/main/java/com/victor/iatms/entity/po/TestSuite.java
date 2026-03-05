package com.victor.iatms.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试套件实体类
 */
@Data
@TableName("testsuites")
public class TestSuite {
    private Integer suiteId;
    private String suiteCode;
    
    @TableField("suite_name")
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
