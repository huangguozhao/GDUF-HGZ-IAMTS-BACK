package com.victor.iatms.entity.po;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 部门实体类
 */
@Data
public class Department {

    /**
     * 部门ID，自增主键
     */
    private Integer departmentId;

    /**
     * 部门编码，唯一标识
     */
    private String departmentCode;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 父部门ID，关联本表主键
     */
    private Integer parentId;

    /**
     * 部门负责人ID，关联Users表
     */
    private Integer managerId;

    /**
     * 部门描述
     */
    private String description;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 部门层级（1为最高级）
     */
    private Integer level;

    /**
     * 部门路径（存储所有上级部门ID）
     */
    private String path;

    /**
     * 是否为叶子部门（无子部门）
     */
    private Boolean isLeaf;

    /**
     * 部门状态
     */
    private String status;

    /**
     * 成立日期
     */
    private LocalDate establishedDate;

    /**
     * 创建人ID，关联Users表
     */
    private Integer createdBy;

    /**
     * 更新人ID
     */
    private Integer updatedBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
