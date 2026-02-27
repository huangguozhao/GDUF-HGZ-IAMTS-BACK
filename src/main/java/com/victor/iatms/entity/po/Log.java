package com.victor.iatms.entity.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Data
public class Log {

    /**
     * 日志ID
     */
    private Long logId;

    /**
     * 操作用户ID
     */
    private Integer userId;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 目标ID
     */
    private Integer targetId;

    /**
     * 目标名称
     */
    private String targetName;

    /**
     * 目标类型
     */
    private String targetType;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 操作状态
     */
    private String status;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 请求路径
     */
    private String requestPath;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 执行耗时(毫秒)
     */
    private Long executionTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 操作时间
     */
    private LocalDateTime timestamp;

    /**
     * 是否删除
     */
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 删除时间
     */
    private LocalDateTime deletedAt;
}

