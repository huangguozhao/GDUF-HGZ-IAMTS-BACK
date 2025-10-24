package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 创建接口请求DTO
 */
@Data
public class CreateApiDTO {

    /**
     * 接口编码（可选，不提供则自动生成）
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
     * 请求方法：GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
     */
    private String method;

    /**
     * 接口路径
     */
    private String path;

    /**
     * 基础URL（可选）
     */
    private String baseUrl;

    /**
     * 查询参数配置
     */
    private List<Map<String, Object>> requestParameters;

    /**
     * 路径参数配置
     */
    private List<Map<String, Object>> pathParameters;

    /**
     * 请求头配置
     */
    private List<Map<String, Object>> requestHeaders;

    /**
     * 请求体内容
     */
    private String requestBody;

    /**
     * 请求体类型：json, xml, form, text
     */
    private String requestBodyType;

    /**
     * 响应体类型：json, xml, html, text
     */
    private String responseBodyType;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 接口状态：draft, active, deprecated
     */
    private String status;

    /**
     * 版本号
     */
    private String version;

    /**
     * 认证类型：none, basic, bearer, apikey, oauth2
     */
    private String authType;

    /**
     * 认证配置
     */
    private Map<String, Object> authConfig;

    /**
     * 标签数组
     */
    private List<String> tags;

    /**
     * 请求示例
     */
    private List<Map<String, Object>> examples;

    /**
     * 超时时间（秒）
     */
    private Integer timeoutSeconds;
}

