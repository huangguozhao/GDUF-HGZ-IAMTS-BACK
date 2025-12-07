package com.victor.iatms.entity.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 项目成员关联表
 * @author: victor
 * @date: 2025-12-07
 */
@Data
public class ProjectMember implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 成员关系ID，自增主键
     */
    private Integer memberId;

    /**
     * 项目ID，关联Projects表
     */
    private Integer projectId;

    /**
     * 用户ID，关联Users表
     */
    private Integer userId;

    /**
     * 权限级别：只读、可编辑、管理员
     */
    private String permissionLevel;

    /**
     * 项目角色
     */
    private String projectRole;

    /**
     * 成员状态
     */
    private String status;

    /**
     * 加入时间
     */
    private Date joinTime;

    /**
     * 离开时间
     */
    private Date leaveTime;

    /**
     * 最后活跃时间
     */
    private Date lastActiveTime;

    /**
     * 分配任务数
     */
    private Integer assignedTasks;

    /**
     * 完成任务数
     */
    private Integer completedTasks;

    /**
     * 附加角色信息
     */
    private String additionalRoles;

    /**
     * 自定义权限配置
     */
    private String customPermissions;

    /**
     * 备注信息
     */
    private String notes;

    /**
     * 添加人ID，关联Users表
     */
    private Integer createdBy;

    /**
     * 最后更新人ID
     */
    private Integer updatedBy;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}


