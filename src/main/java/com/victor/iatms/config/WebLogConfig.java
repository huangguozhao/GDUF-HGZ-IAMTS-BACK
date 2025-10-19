package com.victor.iatms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Web日志配置
 * 用于控制WebLogAspect的行为
 */
@Configuration
@ConfigurationProperties(prefix = "web.log")
public class WebLogConfig {

    /**
     * 是否启用Web日志
     */
    private boolean enabled = true;

    /**
     * 是否记录请求头
     */
    private boolean logHeaders = true;

    /**
     * 是否记录请求参数
     */
    private boolean logParams = true;

    /**
     * 是否记录方法参数
     */
    private boolean logArgs = true;

    /**
     * 是否记录响应结果
     */
    private boolean logResponse = true;

    /**
     * 响应结果最大长度（超过则截断）
     */
    private int maxResponseLength = 1000;

    /**
     * 参数最大长度（超过则截断）
     */
    private int maxParamLength = 500;

    // Getters and Setters
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLogHeaders() {
        return logHeaders;
    }

    public void setLogHeaders(boolean logHeaders) {
        this.logHeaders = logHeaders;
    }

    public boolean isLogParams() {
        return logParams;
    }

    public void setLogParams(boolean logParams) {
        this.logParams = logParams;
    }

    public boolean isLogArgs() {
        return logArgs;
    }

    public void setLogArgs(boolean logArgs) {
        this.logArgs = logArgs;
    }

    public boolean isLogResponse() {
        return logResponse;
    }

    public void setLogResponse(boolean logResponse) {
        this.logResponse = logResponse;
    }

    public int getMaxResponseLength() {
        return maxResponseLength;
    }

    public void setMaxResponseLength(int maxResponseLength) {
        this.maxResponseLength = maxResponseLength;
    }

    public int getMaxParamLength() {
        return maxParamLength;
    }

    public void setMaxParamLength(int maxParamLength) {
        this.maxParamLength = maxParamLength;
    }
}

