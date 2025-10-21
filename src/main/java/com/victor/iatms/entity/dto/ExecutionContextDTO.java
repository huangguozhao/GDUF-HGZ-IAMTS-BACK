package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * 执行上下文信息DTO
 */
@Data
public class ExecutionContextDTO {

    /**
     * 执行环境
     */
    private String environment;

    /**
     * 基础URL
     */
    @JsonProperty("base_url")
    private String baseUrl;

    /**
     * 完整请求URL
     */
    @JsonProperty("request_url")
    private String requestUrl;

    /**
     * 请求方法
     */
    @JsonProperty("request_method")
    private String requestMethod;

    /**
     * 请求头信息
     */
    @JsonProperty("request_headers")
    private Map<String, Object> requestHeaders;

    /**
     * 请求体内容
     */
    @JsonProperty("request_body")
    private String requestBody;

    /**
     * 响应状态码
     */
    @JsonProperty("response_status")
    private Integer responseStatus;

    /**
     * 响应头信息
     */
    @JsonProperty("response_headers")
    private Map<String, Object> responseHeaders;

    /**
     * 响应体内容（可能截断）
     */
    @JsonProperty("response_body")
    private String responseBody;

    /**
     * 响应大小（字节）
     */
    @JsonProperty("response_size")
    private Long responseSize;

    /**
     * 执行变量
     */
    private Map<String, Object> variables;
}



