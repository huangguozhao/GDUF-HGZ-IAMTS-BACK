package com.victor.iatms.entity.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目访问日志实体类
 */
@Data
public class ProjectAccessLog {

    /**
     * 日志ID
     */
    private Integer logId;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 访问时间
     */
    private LocalDateTime accessTime;

    /**
     * 操作类型
     */
    private String actionType;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;
}







