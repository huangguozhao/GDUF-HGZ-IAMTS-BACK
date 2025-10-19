package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.Map;

/**
 * 项目成员统计摘要DTO
 */
@Data
public class ProjectMembersSummaryDTO {
    
    /**
     * 总成员数
     */
    private Integer totalMembers;
    
    /**
     * 活跃成员数
     */
    private Integer activeMembers;
    
    /**
     * 按权限级别统计
     */
    private Map<String, Integer> byPermission;
    
    /**
     * 按项目角色统计
     */
    private Map<String, Integer> byRole;
}
