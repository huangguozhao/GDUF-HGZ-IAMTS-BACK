package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目成员信息DTO
 */
@Data
public class ProjectMemberDTO {
    
    /**
     * 成员关系ID
     */
    private Integer memberId;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户基本信息
     */
    private UserInfoDTO userInfo;
    
    /**
     * 权限级别
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
    private LocalDateTime joinTime;
    
    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;
    
    /**
     * 分配任务数
     */
    private Integer assignedTasks;
    
    /**
     * 完成任务数
     */
    private Integer completedTasks;
    
    /**
     * 任务完成率
     */
    private Double completionRate;
    
    /**
     * 附加角色信息
     */
    private String additionalRoles;
    
    /**
     * 添加人ID
     */
    private Integer createdBy;
    
    /**
     * 添加人姓名
     */
    private String creatorName;
}
