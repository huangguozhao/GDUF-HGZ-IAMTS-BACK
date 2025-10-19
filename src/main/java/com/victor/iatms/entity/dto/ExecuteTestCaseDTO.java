package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.Map;

/**
 * 执行测试用例请求DTO
 */
@Data
public class ExecuteTestCaseDTO {

    /**
     * 执行环境标识
     */
    private String environment;

    /**
     * 覆盖接口的基础URL
     */
    private String baseUrl;

    /**
     * 超时时间（秒）
     */
    private Integer timeout;

    /**
     * 认证信息覆盖配置
     */
    private Map<String, Object> authOverride;

    /**
     * 执行变量，用于参数化测试
     */
    private Map<String, Object> variables;

    /**
     * 是否异步执行
     */
    private Boolean async;

    /**
     * 异步执行完成后的回调URL
     */
    private String callbackUrl;
}
