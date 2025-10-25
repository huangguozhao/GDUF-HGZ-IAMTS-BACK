package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 最近编辑的项目项DTO
 */
@Data
public class RecentProjectItemDTO {

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
     * 最后访问时间
     */
    private LocalDateTime lastAccessed;

    /**
     * 访问次数
     */
    private Integer accessCount;

    /**
     * 模块数量
     */
    private Integer moduleCount;

    /**
     * 接口数量
     */
    private Integer apiCount;

    /**
     * 用例数量
     */
    private Integer caseCount;

    /**
     * 最后活动信息
     */
    private LastActivityDTO lastActivity;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}








