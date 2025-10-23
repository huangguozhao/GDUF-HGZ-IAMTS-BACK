package com.victor.iatms.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 接口信息DTO
 */
@Data
public class ApiDTO {

    /**
     * 接口ID
     */
    private Integer apiId;

    /**
     * 接口编码
     */
    private String apiCode;

    /**
     * 模块ID
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
     * 查询参数
     */
    private Object requestParameters;

    /**
     * 路径参数
     */
    private Object pathParameters;

    /**
     * 请求头信息
     */
    private Object requestHeaders;

    /**
     * 请求体内容
     */
    private String requestBody;

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
     * 认证类型
     */
    private String authType;

    /**
     * 认证配置
     */
    private Object authConfig;

    /**
     * 标签数组
     */
    private List<String> tags;

    /**
     * 请求示例
     */
    private Object examples;

    /**
     * 请求体类型
     */
    private String requestBodyType;

    /**
     * 响应体类型
     */
    private String responseBodyType;

    /**
     * 超时时间
     */
    private Integer timeoutSeconds;

    /**
     * 前置条件数量
     */
    private Integer preconditionCount;

    /**
     * 测试用例数量
     */
    private Integer testCaseCount;

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
}
