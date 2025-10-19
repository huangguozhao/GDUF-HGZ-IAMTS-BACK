package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目列表响应DTO
 */
@Data
public class ProjectListResponseDTO {
    
    /**
     * 项目ID
     */
    private Integer projectId;
    
    /**
     * 项目名称
     */
    private String name;
    
    /**
     * 项目描述
     */
    private String description;
    
    /**
     * 创建人信息
     */
    private CreatorInfoDTO creatorInfo;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否已删除
     */
    private Boolean isDeleted;
    
    /**
     * 删除时间
     */
    private LocalDateTime deletedAt;
    
    /**
     * 创建人信息DTO
     */
    @Data
    public static class CreatorInfoDTO {
        /**
         * 用户ID
         */
        private Integer userId;
        
        /**
         * 姓名
         */
        private String name;
        
        /**
         * 头像URL
         */
        private String avatarUrl;
    }
}
