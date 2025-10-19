package com.victor.iatms.entity.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 接口实体类
 */
@Data
public class Api {

    /**
     * 接口ID，自增主键
     */
    private Integer apiId;

    /**
     * 接口编码，模块内唯一
     */
    private String apiCode;

    /**
     * 模块ID，关联Modules表的主键
     */
    private Integer moduleId;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 接口路径
     */
    private String path;

    /**
     * 基础URL
     */
    private String baseUrl;

    /**
     * 完整URL
     */
    private String fullUrl;

    /**
     * 查询参数，JSON格式
     */
    private String requestParameters;

    /**
     * 路径参数，JSON格式
     */
    private String pathParameters;

    /**
     * 请求头信息，JSON格式
     */
    private String requestHeaders;

    /**
     * 请求体内容
     */
    private String requestBody;

    /**
     * 请求体类型
     */
    private String requestBodyType;

    /**
     * 响应体类型
     */
    private String responseBodyType;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 接口状态
     */
    private String status;

    /**
     * 版本号
     */
    private String version;

    /**
     * 超时时间(秒)
     */
    private Integer timeoutSeconds;

    /**
     * 认证类型
     */
    private String authType;

    /**
     * 认证配置
     */
    private String authConfig;

    /**
     * 标签，JSON数组格式
     */
    private String tags;

    /**
     * 请求示例
     */
    private String examples;

    /**
     * 创建人ID，关联用户表
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

    /**
     * 是否删除
     */
    private Boolean isDeleted;

    /**
     * 删除时间
     */
    private LocalDateTime deletedAt;

    /**
     * 删除人ID
     */
    private Integer deletedBy;
}