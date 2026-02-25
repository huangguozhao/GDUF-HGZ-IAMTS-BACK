package com.victor.iatms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * DeepSeek API配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "deepseek.api")
public class DeepSeekConfig {

    /**
     * API密钥
     */
    private String key;

    /**
     * API基础URL
     */
    private String baseUrl = "https://api.deepseek.com";

    /**
     * 使用的模型
     */
    private String model = "deepseek-chat";

    /**
     * 请求超时时间(毫秒)
     */
    private Integer timeout = 30000;

    /**
     * 最大token数
     */
    private Integer maxTokens = 2000;
}

